package io.renren.zadmin.zorg.member;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JAgentDTO;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.excel.JMerchantExcel;
import io.renren.zadmin.service.JAgentService;
import io.renren.zadmin.service.JMerchantService;
import io.renren.zin.service.file.ZinFileService;
import io.renren.zin.service.sub.ZinSubService;
import io.renren.zin.service.sub.dto.TSubCreateRequest;
import io.renren.zin.service.sub.dto.TSubCreateResponse;
import io.renren.zin.service.sub.dto.TSubQuery;
import io.renren.zin.service.sub.dto.TSubQueryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.renren.zin.config.CommonUtils.newRequestId;


/**
 * 商户管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jmerchant")
@Tag(name = "j_merchant")
@Slf4j
public class JMerchantController {
    @Resource
    private JMerchantService jMerchantService;
    @Resource
    private ZinSubService zinSubService;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private JAgentService jAgentService;
    @Resource
    private JMerchantDao jMerchantDao;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmerchant:page')")
    public Result<PageData<JMerchantDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JMerchantDTO> page = jMerchantService.page(params);
        return new Result<PageData<JMerchantDTO>>().ok(page);
    }

    @GetMapping("list")
    @Operation(summary = "分页")
    @PreAuthorize("hasAuthority('zorg:jmerchant:page')")
    public Result<List<JMerchantDTO>> list() {
        Map<String, Object> params = new HashMap<>();
        List<JMerchantDTO> list = jMerchantService.list(params);
        return new Result<List<JMerchantDTO>>().ok(list);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmerchant:info')")
    public Result<JMerchantDTO> get(@PathVariable("id") Long id) {
        JMerchantDTO data = jMerchantService.get(id);
        return new Result<JMerchantDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmerchant:save')")
    public Result save(@RequestBody JMerchantDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jMerchantService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmerchant:update')")
    public Result update(@RequestBody JMerchantDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jMerchantService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmerchant:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jMerchantService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmerchant:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMerchantDTO> list = jMerchantService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_merchant", list, JMerchantExcel.class);
    }

    /**
     * 获取代理列表
     *
     * @return
     */
    @GetMapping("agentList")
    public Result<List<JAgentDTO>> agentList() {
        List<JAgentDTO> list = jAgentService.list(new HashMap<>());
        return Result.one(list);
    }

    /**
     * 发起到通联创建子商户
     */
    @GetMapping("createAllinpay")
    @PreAuthorize("hasAuthority('zorg:jmerchant:update')")
    public Result createAllinpay(@RequestParam("id") Long id) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(id);

        // 上传文件
        this.uploadFiles(jMerchantEntity);

        // 准备请求
        TSubCreateRequest tSubCreateRequest = ConvertUtils.sourceToTarget(jMerchantEntity, TSubCreateRequest.class);
        tSubCreateRequest.setMeraplid(id.toString());

        // 调用通联
        TSubCreateResponse response = zinSubService.create(tSubCreateRequest);

        // 更新应答
        String cusid = response.getCusid();
        jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                .eq(JMerchantEntity::getId, id)
                .set(JMerchantEntity::getCusid, cusid)
                .set(JMerchantEntity::getMeraplid, tSubCreateRequest.getMeraplid())
        );
        return new Result();
    }

    private void uploadFiles(JMerchantEntity jMerchantEntity) {
        // 拿到所有文件fid
        String agreementfid = jMerchantEntity.getAgreementfid();
        String buslicensefid = jMerchantEntity.getBuslicensefid();
        String creditfid = jMerchantEntity.getCreditfid();
        String legalphotobackfid = jMerchantEntity.getLegalphotobackfid();
        String legalphotofrontfid = jMerchantEntity.getLegalphotofrontfid();
        String taxfid = jMerchantEntity.getTaxfid();
        String organfid = jMerchantEntity.getOrganfid();

        List<String> fids = List.of(agreementfid, buslicensefid, creditfid, legalphotobackfid, legalphotofrontfid, taxfid, organfid);
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

    /**
     * 发起到通联查子商户
     */
    @GetMapping("queryAllinpay")
    @PreAuthorize("hasAuthority('zorg:jmerchant:update')")
    public Result queryAllinpay(@RequestParam("id") Long id) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(id);
        TSubQuery tSubQuery = ConvertUtils.sourceToTarget(jMerchantEntity, TSubQuery.class);

        TSubQueryResponse response = zinSubService.query(tSubQuery);
        jMerchantDao.update(null, Wrappers.<JMerchantEntity>lambdaUpdate()
                .eq(JMerchantEntity::getId, jMerchantEntity.getId())
                .set(JMerchantEntity::getState, response.getState())
        );
        return new Result();
    }

}