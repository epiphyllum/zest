package io.renren.zadmin.zorg.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JMcardDao;
import io.renren.zadmin.dto.JMcardDTO;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.excel.JCardExcel;
import io.renren.zadmin.service.JMcardService;
import io.renren.zin.cardapply.ZinCardApplyService;
import io.renren.zin.cardapply.dto.TCardApplyQuery;
import io.renren.zin.cardapply.dto.TCardApplyResponse;
import io.renren.zin.cardapply.dto.TCardMainApplyRequest;
import io.renren.zin.cardapply.dto.TCardMainApplyResponse;
import io.renren.zin.file.ZinFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * 主卡管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jmcard")
@Tag(name = "j_mcard")
@Slf4j
public class JMcardController {
    @Resource
    private JMcardService jmcardService;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private JMcardDao jMcardDao;

    @Resource
    private ZinCardApplyService zinCardApplyService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmcard:page')")
    public Result<PageData<JMcardDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        if (!ZestConstant.isOperationOrAgent()) {
            throw new RenException("not permitted");
        }
        PageData<JMcardDTO> page = jmcardService.page(params);
        return new Result<PageData<JMcardDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmcard:info')")
    public Result<JMcardDTO> get(@PathVariable("id") Long id) {
        JMcardDTO data = jmcardService.get(id);
        return new Result<JMcardDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmcard:save')")
    public Result save(@RequestBody JMcardDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jmcardService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmcard:update')")
    public Result update(@RequestBody JMcardDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jmcardService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmcard:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jmcardService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmcard:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMcardDTO> list = jmcardService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "主卡列表", list, JCardExcel.class);
    }

    @GetMapping("submit")
    @Operation(summary = "提交通联")
    @LogOperation("提交通联")
    @PreAuthorize("hasAuthority('zorg:jmcard:update')")
    public Result submit(@RequestParam("id") Long id) {
        JMcardEntity jMcardEntity = jMcardDao.selectById(id);
        this.uploadFiles(jMcardEntity);
        TCardMainApplyRequest request = ConvertUtils.sourceToTarget(jMcardEntity, TCardMainApplyRequest.class);
        request.setMeraplid(jMcardEntity.getId().toString());
        TCardMainApplyResponse response = zinCardApplyService.cardMainApply(request);

        // 更新applyid
        JMcardEntity update = new JMcardEntity();
        update.setId(jMcardEntity.getId());
        update.setApplyid(response.getApplyid());
        jMcardDao.updateById(update);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "查询通联")
    @LogOperation("查询通联")
    @PreAuthorize("hasAuthority('zorg:jmcard:update')")
    public Result query(@RequestParam("id") Long id) {
        JMcardEntity jMcardEntity = jMcardDao.selectById(id);
        TCardApplyQuery query = new TCardApplyQuery();
//        query.setApplyid(jMcardEntity.getApplyid());
        query.setMeraplid(jMcardEntity.getId().toString());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        JMcardEntity update = new JMcardEntity();
        update.setId(jMcardEntity.getId());
        update.setState(response.getState());
        update.setFeecurrency(response.getFeecurrency());
        update.setFee(response.getFee());
        update.setCardno(response.getCardno());


        jMcardDao.updateById(update);
        return Result.ok;
    }

    private void uploadFiles(JMcardEntity mcardEntity) {
        // 拿到所有文件fid
        String photofront = mcardEntity.getPhotofront();
        String photoback = mcardEntity.getPhotoback();
        String photofront2 = mcardEntity.getPhotofront2();
        String photoback2 = mcardEntity.getPhotoback2();

        List<String> fids = List.of(photofront, photoback, photofront2, photoback2);
        Map<String, CompletableFuture<String>> jobs = new HashMap<>();
        for (String fid : fids) {
            if (StringUtils.isBlank(fid)) {
                continue;
            }
            jobs.put(fid, CompletableFuture.supplyAsync(() -> {
                return zinFileService.upload(fid);
            }));
        }
        jobs.forEach((j, f) -> {
            log.info("wait {}...", j);
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RenException("can not upload file:" + j);
            }
        });
        log.info("文件上传完毕, 开始请求创建商户...");
    }

}