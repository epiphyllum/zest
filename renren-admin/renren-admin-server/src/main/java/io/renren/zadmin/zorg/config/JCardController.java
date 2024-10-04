package io.renren.zadmin.zorg.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
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
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.excel.JCardExcel;
import io.renren.zadmin.service.JCardService;
import io.renren.zapi.notifyevent.CardApplyNotifyEvent;
import io.renren.zbalance.Ledger;
import io.renren.zbalance.LedgerUtil;
import io.renren.zin.config.CardProductConfig;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinConstant;
import io.renren.zin.service.cardapply.ZinCardApplyService;
import io.renren.zin.service.cardapply.dto.*;
import io.renren.zin.service.file.ZinFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * 子卡管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jscard")
@Tag(name = "j_scard")
@Slf4j
public class JCardController {
    @Resource
    private JCardService jCardService;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private ZinCardApplyService zinCardApplyService;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private Ledger ledger;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private ApplicationEventPublisher publisher;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jcard:page')")
    public Result<PageData<JCardDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JCardDTO> page = jCardService.page(params);
        return new Result<PageData<JCardDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jcard:info')")
    public Result<JCardDTO> get(@PathVariable("id") Long id) {
        JCardDTO data = jCardService.get(id);
        return new Result<JCardDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jcard:save')")
    public Result save(@RequestBody JCardDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        dto.setApi(0);

        tx.executeWithoutResult(status -> {
            jCardService.save(dto);
        });
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result update(@RequestBody JCardDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jCardService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jcard:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jCardService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jcard:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JCardDTO> list = jCardService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_card", list, JCardExcel.class);
    }

    @GetMapping("submit")
    @Operation(summary = "提交通联")
    @LogOperation("提交通联")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result submit(@RequestParam("id") Long id) {
        JCardEntity jCardEntity = jCardDao.selectById(id);

        this.uploadFiles(jCardEntity);
        TCardSubApplyRequest request = ConvertUtils.sourceToTarget(jCardEntity, TCardSubApplyRequest.class);
        request.setMeraplid(jCardEntity.getId().toString());
        TCardSubApplyResponse response = zinCardApplyService.cardSubApply(request);

        // 更新applyid
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setApplyid(response.getApplyid());
        jCardDao.updateById(update);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "查询通联")
    @LogOperation("查询通联")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result query(@RequestParam("id") Long id) {
        JCardEntity jCardEntity = jCardDao.selectById(id);
        TCardApplyQuery query = new TCardApplyQuery();
        query.setMeraplid(jCardEntity.getId().toString());
        TCardApplyResponse response = zinCardApplyService.cardApplyQuery(query);

        // 准备待更新字段
        JCardEntity update = new JCardEntity();
        update.setId(jCardEntity.getId());
        update.setState(response.getState());
        update.setFeecurrency(response.getFeecurrency());
        update.setFee(response.getFee());
        update.setCardno(response.getCardno());

        // 开卡成功了
        if (response.getState().equals(ZinConstant.CARD_APPLY_SUCCESS)) {
            jCardDao.updateById(update);
            if (jCardEntity.getApi().equals(1)) {
                publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
            }
            return Result.ok;
        }


        // 从非失败  -> 失败,  处理退款
        String prevState = jCardEntity.getState();
        String nextState = response.getState();
        if (!(prevState.equals(ZinConstant.CARD_APPLY_VERIFY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_CLOSE)
        ) && (nextState.equals(ZinConstant.CARD_APPLY_VERIFY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_FAIL) ||
                prevState.equals(ZinConstant.CARD_APPLY_CLOSE))
        ) {
            tx.executeWithoutResult(status -> {
                jCardDao.updateById(update);
                ledger.ledgeOpenCardUnFreeze(jCardEntity);
            });
            // 通知商户
            if (jCardEntity.getApi().equals(1)) {
                publisher.publishEvent(new CardApplyNotifyEvent(this, jCardEntity.getId()));
            }
        } else {
            jCardDao.updateById(update);
        }

        return Result.ok;
    }

    private void uploadFiles(JCardEntity cardEntity) {
        // 拿到所有文件fid
        String photofront = cardEntity.getPhotofront();
        String photoback = cardEntity.getPhotoback();
        String photofront2 = cardEntity.getPhotofront2();
        String photoback2 = cardEntity.getPhotoback2();

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