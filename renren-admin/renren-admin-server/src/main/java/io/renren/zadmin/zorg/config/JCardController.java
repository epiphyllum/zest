package io.renren.zadmin.zorg.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
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
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zbalance.BalanceType;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JCardManager;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JCardDao;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.excel.JCardExcel;
import io.renren.zadmin.service.JCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 子卡管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jcard")
@Tag(name = "j_card")
@Slf4j
public class JCardController {
    @Resource
    private JCardService jCardService;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JBalanceDao jBalanceDao;

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
        if (!ZestConstant.isOperation()) {
            page.getList().forEach(e -> {
                e.setCvv(null);
            });
        }

        List<Long> mainPPList = new ArrayList<>();
        Map<Long, JCardDTO> map = new HashMap<>();
        page.getList().forEach(e -> {
            e.setCvv(null);
            // 预付费主卡，
            if (ZinConstant.MP_VPA_MAIN_PREPAID.equals(e.getMarketproduct())) {
                mainPPList.add(e.getId());
                map.put(e.getId(), e);
            }
        });

        // 如果有预付费主卡， 加上主卡发卡额度
        if (mainPPList.size() > 0) {
            log.info("本页有预付费主卡:{}", mainPPList.size());
            List<JBalanceEntity> balanceEntities = jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                    .in(JBalanceEntity::getOwnerId, mainPPList)
                    .select(JBalanceEntity::getOwnerId, JBalanceEntity::getBalance, JBalanceEntity::getCurrency, JBalanceEntity::getBalanceType)
            );
            for (JBalanceEntity balanceEntity : balanceEntities) {
                JCardDTO jCardDTO = map.get(balanceEntity.getOwnerId());
                if (balanceEntity.getBalanceType().equals(BalanceType.getPrepaidQuotaAccount(jCardDTO.getCurrency()))) {
                    jCardDTO.setPrepaidQuota(balanceEntity.getBalance());
                }
            }
        }
        return new Result<PageData<JCardDTO>>().ok(page);
    }

    @GetMapping("list")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jcard:list')")
    public Result<List<JCardDTO>> list(@RequestParam Map<String, Object> params) {
        List<JCardDTO> list = jCardService.list(params);
        Result<List<JCardDTO>> result = new Result<>();
        result.setData(list);
        return result;
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jcard:info')")
    public Result<JCardDTO> get(@PathVariable("id") Long id) {
        JCardDTO data = jCardService.get(id);
        data.setExpiredate(null);
        data.setCvv(null);
        return new Result<JCardDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jcard:save')")
    public Result save(@RequestBody JCardDTO dto) {
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JCardEntity entity = ConvertUtils.sourceToTarget(dto, JCardEntity.class);
        entity.setApi(0);
        entity.setMeraplid(CommonUtils.uniqueId());
        entity.setTxnid(CommonUtils.uniqueId());

        // 子商户操作
        if (SecurityUser.getUser().getUserType().equals(ZestConstant.USER_TYPE_SUB)) {
            entity.setSubId(SecurityUser.getDeptId());
        }

        jCardManager.save(entity);
        return new Result();
    }

    @GetMapping("cancel")
    @Operation(summary = "取消发卡")
    @LogOperation("取消发卡")
    @PreAuthorize("hasAuthority('zorg:jcard:cancel')")
    public Result cancel(@RequestParam("id") Long id) {
        jCardManager.cancel(id);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result update(@RequestBody JCardDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        dto.setApplyid("");
        dto.setTxnid(CommonUtils.uniqueId());
        dto.setState(ZinConstant.CARD_APPLY_NEW_DJ);
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
        if (!ZestConstant.isOperation()) {
            list.forEach(e -> {
                        e.setCvv(null);
                        e.setExpiredate(null);
                    }
            );
        }
        ExcelUtils.exportExcelToTarget(response, null, "card", list, JCardExcel.class);
    }

    @GetMapping("submit")
    @Operation(summary = "开卡提交通联")
    @LogOperation("开卡提交通联")
    @PreAuthorize("hasAuthority('zorg:jcard:submit')")
    public Result submit(@RequestParam("id") Long id) {
        JCardEntity jCardEntity = jCardDao.selectById(id);
        jCardManager.submit(jCardEntity);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "开卡查询通联")
    @LogOperation("开卡查询通联")
    @PreAuthorize("hasAuthority('zorg:jcard:query')")
    public Result query(@RequestParam("id") Long id) {
        JCardEntity jCardEntity = jCardDao.selectById(id);
        jCardManager.query(jCardEntity, false);
        return Result.ok;
    }


    //////////////////////////////////////////////////////////////////////////////
    // 开卡后的操作
    //////////////////////////////////////////////////////////////////////////////
    @GetMapping("queryCard")
    @Operation(summary = "卡状态查询通联卡")
    @LogOperation("卡状态查询通联卡")
    @PreAuthorize("hasAuthority('zorg:jcard:queryCard')")
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
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::cancelCard);
        return Result.ok;
    }

    @GetMapping("uncancelCard")
    @Operation(summary = "取消销卡")
    @LogOperation("取消销卡")
    @PreAuthorize("hasAuthority('zorg:jcard:uncancel')")
    public Result uncancelCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::uncancelCard);
        return Result.ok;
    }

    @GetMapping("freezeCard")
    @Operation(summary = "止付")
    @LogOperation("止付")
    @PreAuthorize("hasAuthority('zorg:jcard:freeze')")
    public Result freezeCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::freezeCard);
        return Result.ok;
    }

    @GetMapping("unfreezeCard")
    @Operation(summary = "取消止付")
    @LogOperation("取消止付")
    @PreAuthorize("hasAuthority('zorg:jcard:unfreeze')")
    public Result unfreezeCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::unfreezeCard);
        return Result.ok;
    }

    @GetMapping("lossCard")
    @Operation(summary = "挂失")
    @LogOperation("挂失")
    @PreAuthorize("hasAuthority('zorg:jcard:loss')")
    public Result lossCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::lossCard);
        return Result.ok;
    }

    @GetMapping("unlossCard")
    @Operation(summary = "取消挂失")
    @LogOperation("取消挂失")
    @PreAuthorize("hasAuthority('zorg:jcard:unloss')")
    public Result unlossCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::unlossCard);
        return Result.ok;
    }

    @GetMapping("activateCard")
    @Operation(summary = "激活")
    @LogOperation("激活")
    @PreAuthorize("hasAuthority('zorg:jcard:activate')")
    public Result activateCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::activateCard);
        return Result.ok;
    }

    @GetMapping("balanceCard")
    @Operation(summary = "卡余额")
    @LogOperation("卡余额")
    @PreAuthorize("hasAuthority('zorg:jcard:balance')")
    public Result balanceCard(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            throw new RenException("invalid parameter");
        }
        jCardManager.runList(id, jCardManager::balanceCard);
        return Result.ok;
    }

    @GetMapping("updateCard")
    @Operation(summary = "更新卡状态以及余额")
    @LogOperation("更新卡状态和卡余额")
    @PreAuthorize("hasAuthority('zorg:jcard:updateCard')")
    public Result updateCard(@RequestParam("id") Long id) {
        jCardManager.updateCard(id);
        return Result.ok;
    }

    @GetMapping("prepaidCharge")
    @Operation(summary = "预付费卡充值")
    @LogOperation("预付费卡充值")
    @PreAuthorize("hasAuthority('zorg:jcard:prepaidCharge')")
    public Result prepaidCharge(@RequestParam("id") Long id, @RequestParam("adjustAmount") BigDecimal adjustAmount) {
        jCardManager.prepaidCharge(id, adjustAmount, 0);
        return Result.ok;
    }

    @GetMapping("prepaidWithdraw")
    @Operation(summary = "预付费卡提现")
    @LogOperation("预付费卡提现")
    @PreAuthorize("hasAuthority('zorg:jcard:prepaidWithdraw')")
    public Result prepaidWithdraw(@RequestParam("id") Long id, @RequestParam("adjustAmount") BigDecimal adjustAmount) {
        jCardManager.prepaidWithdraw(id, adjustAmount, 0);
        return Result.ok;
    }

    @GetMapping("setQuota")
    @Operation(summary = "共享卡设置额度")
    @LogOperation("共享卡设置额度")
    @PreAuthorize("hasAuthority('zorg:jcard:setQuota')")
    public Result setQuota(@RequestParam("id") Long id,
                           @RequestParam("authmaxamount") BigDecimal authmaxamount,
                           @RequestParam(value = "authmaxcount", required = false) Integer authmaxcount
    ) {
        jCardManager.setQuota(id, authmaxamount, authmaxcount, 0);
        return Result.ok;
    }

}
