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
import io.renren.manager.JCardManager;
import io.renren.zadmin.ZestConstant;
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

import java.util.ArrayList;
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
    private JCardManager jCardManager;
    @Resource
    private JCardDao jCardDao;

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

//        UserDetail user = SecurityUser.getUser();
//        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
//            return Result.fail(9999, "not authorized, you are " + user.getUserType());
//        }

        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        dto.setApi(0);

        JCardEntity entity = ConvertUtils.sourceToTarget(dto, JCardEntity.class);
        jCardManager.save(entity);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result update(@RequestBody JCardDTO dto) {

//        UserDetail user = SecurityUser.getUser();
//        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
//            return Result.fail(9999, "not authorized");
//        }

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
        jCardManager.submit(jCardEntity);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "查询通联")
    @LogOperation("查询通联")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result query(@RequestParam("id") Long id) {
        JCardEntity jCardEntity = jCardDao.selectById(id);
        jCardManager.query(jCardEntity);
        return Result.ok;
    }

    @GetMapping("queryCard")
    @Operation(summary = "查询通联卡")
    @LogOperation("查询通联卡")
    @PreAuthorize("hasAuthority('zorg:jcard:query')")
    public Result queryCard(@RequestParam("id") Long id) {
        JCardEntity jCardEntity = jCardDao.selectById(id);
        jCardManager.queryCard(jCardEntity);
        return Result.ok;
    }

    @GetMapping("cancelCard")
    @Operation(summary = "销卡")
    @LogOperation("销卡")
    @PreAuthorize("hasAuthority('zorg:jcard:cancel')")
    public Result cancelCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::cancelCard);
        return Result.ok;
    }

    @GetMapping("uncancelCard")
    @Operation(summary = "取消销卡")
    @LogOperation("取消销卡")
    @PreAuthorize("hasAuthority('zorg:jcard:uncancel')")
    public Result uncancelCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::uncancelCard);
        return Result.ok;
    }

    @GetMapping("freezeCard")
    @Operation(summary = "止付")
    @LogOperation("止付")
    @PreAuthorize("hasAuthority('zorg:jcard:freeze')")
    public Result freezeCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::freezeCard);
        return Result.ok;
    }

    @GetMapping("unfreezeCard")
    @Operation(summary = "取消止付")
    @LogOperation("取消止付")
    @PreAuthorize("hasAuthority('zorg:jcard:unfreeze')")
    public Result unfreezeCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::unfreezeCard);
        return Result.ok;
    }

    @GetMapping("lossCard")
    @Operation(summary = "挂失")
    @LogOperation("挂失")
    @PreAuthorize("hasAuthority('zorg:jcard:loss')")
    public Result lossCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::lossCard);
        return Result.ok;
    }

    @GetMapping("unlossCard")
    @Operation(summary = "取消挂失")
    @LogOperation("取消挂失")
    @PreAuthorize("hasAuthority('zorg:jcard:unloss')")
    public Result unlossCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::uncancelCard);
        return Result.ok;
    }

    @GetMapping("activateCard")
    @Operation(summary = "激活")
    @LogOperation("激活")
    @PreAuthorize("hasAuthority('zorg:jcard:activate')")
    public Result activateCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::activateCard);
        return Result.ok;
    }

    @GetMapping("balanceCard")
    @Operation(summary = "卡余额")
    @LogOperation("卡余额")
    @PreAuthorize("hasAuthority('zorg:jcard:balance')")
    public Result balanceCard(@RequestParam("id") String id) {
        jCardManager.runList(id, jCardManager::balanceCard);
        return Result.ok;
    }
}
