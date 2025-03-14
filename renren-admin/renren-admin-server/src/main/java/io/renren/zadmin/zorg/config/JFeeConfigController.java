package io.renren.zadmin.zorg.config;

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
import io.renren.zadmin.dao.JFeeConfigDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JFeeConfigDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JFeeConfigEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.excel.JFeeConfigExcel;
import io.renren.zadmin.service.JFeeConfigService;
import io.renren.zbalance.LedgerUtil;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JDepositManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;


/**
 * j_fee_config
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-12
 */
@RestController
@RequestMapping("zorg/jfeeconfig")
@Tag(name = "j_fee_config")
public class JFeeConfigController {
    @Resource
    private JDepositManager jDepositManager;
    @Resource
    private JFeeConfigService jFeeConfigService;
    @Resource
    private JFeeConfigDao jFeeConfigDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private LedgerUtil ledgerUtil;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jfeeconfig:page')")
    public Result<PageData<JFeeConfigDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JFeeConfigDTO> page = jFeeConfigService.page(params);

        return new Result<PageData<JFeeConfigDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jfeeconfig:info')")
    public Result<JFeeConfigDTO> get(@PathVariable("id") Long id) {
        JFeeConfigDTO data = jFeeConfigService.get(id);
        return new Result<JFeeConfigDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jfeeconfig:save')")
    public Result save(@RequestBody JFeeConfigDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JMerchantEntity merchant = jMerchantDao.selectById(dto.getMerchantId());
        dto.setAgentId(merchant.getAgentId());
        dto.setAgentName(merchant.getAgentName());
        dto.setMerchantName(merchant.getCusname());
        jFeeConfigService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jfeeconfig:update')")
    public Result update(@RequestBody JFeeConfigDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jFeeConfigService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jfeeconfig:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jFeeConfigService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jfeeconfig:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JFeeConfigDTO> list = jFeeConfigService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_fee_config", list, JFeeConfigExcel.class);
    }

    @GetMapping("single")
    @Operation(summary = "信息")
    public Result<JFeeConfigDTO> single(
            @RequestParam("marketproduct") String marketproduct,
            @RequestParam("merchantId") Long merchantId,
            @RequestParam(value = "subId", required = false) Long subId,
            @RequestParam(value = "currency", required = false) String currency
    ) {
        JFeeConfigEntity feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                .eq(JFeeConfigEntity::getMarketproduct, marketproduct)
                .eq(JFeeConfigEntity::getMerchantId, merchantId)
        );
        if (feeConfig == null) {
            feeConfig = jFeeConfigDao.selectOne(Wrappers.<JFeeConfigEntity>lambdaQuery()
                    .eq(JFeeConfigEntity::getMarketproduct, marketproduct)
                    .eq(JFeeConfigEntity::getMerchantId, 0L)
            );
        }
        if (feeConfig == null) {
            throw new RenException("商户产品配置缺失");
        }
        JFeeConfigDTO jFeeConfigDTO = ConvertUtils.sourceToTarget(feeConfig, JFeeConfigDTO.class);

        if (subId != null) {
            JBalanceEntity subVaAccount = ledgerUtil.getSubVaAccount(subId, currency);
            jFeeConfigDTO.setSubVa(subVaAccount.getBalance());

            BigDecimal rate = BigDecimal.ONE.add(jFeeConfigDTO.getChargeRate()).add(jFeeConfigDTO.getDepositRate());
            BigDecimal maxAmount = subVaAccount.getBalance().divide(rate, 2, RoundingMode.HALF_DOWN);
            jFeeConfigDTO.setMaxAmount(maxAmount);
        }

        Result<JFeeConfigDTO> result = new Result<>();
        result.setData(jFeeConfigDTO);
        return result;
    }
}