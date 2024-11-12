package io.renren.zadmin.zorg.balance;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
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
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zbalance.BalanceType;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JBalanceDao;
import io.renren.zadmin.dto.JBalanceDTO;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.excel.JBalanceExcel;
import io.renren.zadmin.service.JBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * j_balance
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@RestController
@RequestMapping("zorg/jbalance")
@Tag(name = "j_balance")
@Slf4j
public class JBalanceController {
    @Resource
    private JBalanceService jBalanceService;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private TransactionTemplate tx;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jbalance:page')")
    public Result<PageData<JBalanceDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        if (params.get("list") != null) {
            params.put(Constant.LIMIT, "100");
            params.put(Constant.PAGE, "1");
        }

        if (ZestConstant.isMerchant()) {
            params.put("merchantId", SecurityUser.getDeptId().toString());
        } else if (ZestConstant.isSub()) {
            params.put("subId", SecurityUser.getDeptId().toString());
        } else if (ZestConstant.isAgent()) {
            params.put("agentId", SecurityUser.getDeptId().toString());
        }

        PageData<JBalanceDTO> page = jBalanceService.page(params);
        return new Result<PageData<JBalanceDTO>>().ok(page);
    }

    /**
     * 这里是按组展示商户的账户。 必须要有ownerId
     *
     * @param params
     * @return
     */
    @GetMapping("page/merchant")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jbalance:page')")
    public Result<PageData<JBalanceDTO>> pageMerchant(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        if (ZestConstant.isOperationOrAgent()) {
            // 运营+代理登录， 必须有ownerId
            if (StringUtils.isBlank((String) params.get("ownerId"))) {
                PageData<JBalanceDTO> page = new PageData<>();
                page.setTotal(0);
                page.setList(List.of());
                return new Result<PageData<JBalanceDTO>>().ok(page);
            }
            log.info("page/merchant, operation/agent, ownerId: {}", params.get("ownerId"));
        } else if (ZestConstant.isMerchant()) {
            // 商户登录， ownerId 就是自己
            log.info("page/merchant, ownerId: {}", SecurityUser.getDeptId());
            params.put("ownerId", SecurityUser.getDeptId().toString());
        } else {
            // 其他为非法用户
            throw new RenException("invalid user");
        }

        params.put(Constant.LIMIT, "200");
        params.put(Constant.PAGE, "1");
        PageData<JBalanceDTO> page = jBalanceService.page(params);
        Map<String, List<JBalanceDTO>> collectByCurrency = page.getList()
                .stream()
                .collect(Collectors.groupingBy(JBalanceDTO::getCurrency));

        List<JBalanceDTO> newList = new ArrayList<>(15);
        for (Map.Entry<String, List<JBalanceDTO>> entry : collectByCurrency.entrySet()) {
            String currency = entry.getKey();
            List<JBalanceDTO> dtoList = entry.getValue();

            BigDecimal deposit = BigDecimal.ZERO;
            BigDecimal chargeFee = BigDecimal.ZERO;
            BigDecimal txnFee = BigDecimal.ZERO;
            JBalanceDTO va = null;

            String vaType = "VA_" + currency;
            String depositType = "DEPOSIT_" + currency;
            String chargeFeeType = "CHARGE_FEE_" + currency;
            String txnFeeType = "TXN_FEE_" + currency;

            for (JBalanceDTO dto : dtoList) {
                if (dto.getBalanceType().equals(vaType)) {
                    log.debug("check dto: {}-{}-{}-{}", dto.getBalance(), dto.getFrozen(), dto.getCurrency(), dto.getBalanceType());
                    va = dto;
                } else if (dto.getBalanceType().equals(depositType)) {
                    deposit = dto.getBalance();
                } else if (dto.getBalanceType().equals(chargeFeeType)) {
                    chargeFee = dto.getBalance();
                } else if (dto.getBalanceType().equals(txnFeeType)) {
                    txnFee = dto.getBalance();
                }
            }
            if (deposit == null || chargeFee == null || txnFee == null || va == null) {
                log.info("deposit: {}, chargeFee: {}, txnFee: {}, va: {}", deposit, chargeFee, txnFee, va);
                throw new RenException("internal logical error");
            }
            va.setBalanceDeposit(deposit);
            va.setBalanceChargeFee(chargeFee);
            va.setBalanceTxnFee(txnFee);
            newList.add(va);
        }
        page.setList(newList);
        page.setTotal(15);
        return new Result<PageData<JBalanceDTO>>().ok(page);
    }

    @GetMapping("page/sub")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jbalance:page')")
    public Result<PageData<JBalanceDTO>> pageSub(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {

        if (ZestConstant.isOperationOrAgentOrMerchant()) {
            // 机构+代理+商户登录， 必须有ownerId(subId)
            if (StringUtils.isBlank((String) params.get("ownerId"))) {
                PageData<JBalanceDTO> page = new PageData<>();
                page.setTotal(0);
                page.setList(List.of());
                return new Result<PageData<JBalanceDTO>>().ok(page);
            }
            log.info("page/merchant, operation/agent, ownerId: {}", params.get("ownerId"));
        } else if (ZestConstant.isSub()) {
            // 子商户登录， subId就是ownerId
            params.put("ownerId", SecurityUser.getDeptId().toString());
        } else {
            throw new RenException("invalid user");
        }

        params.put(Constant.LIMIT, "200");
        params.put(Constant.PAGE, "1");
        PageData<JBalanceDTO> page = jBalanceService.page(params);
        Map<String, List<JBalanceDTO>> collectByCurrency = page.getList()
                .stream()
                .collect(Collectors.groupingBy(JBalanceDTO::getCurrency));

        List<JBalanceDTO> newList = new ArrayList<>(15);
        for (Map.Entry<String, List<JBalanceDTO>> entry : collectByCurrency.entrySet()) {
            String currency = entry.getKey();
            List<JBalanceDTO> dtoList = entry.getValue();

            BigDecimal subSum = null;
            BigDecimal subFee = null;
            JBalanceDTO subVa = null;

            String subVaType = "SUB_VA_" + currency;
            String subSumType = "SUB_SUM_" + currency;
            String subFeeType = "SUB_FEE_" + currency;

            for (JBalanceDTO dto : dtoList) {
                log.info("check dto: {}-{}-{}-{}", dto.getBalance(), dto.getFrozen(), dto.getCurrency(), dto.getBalanceType());
                if (dto.getBalanceType().equals(subVaType)) {
                    subVa = dto;
                } else if (dto.getBalanceType().equals(subSumType)) {
                    subSum = dto.getBalance();
                } else if (dto.getBalanceType().equals(subFeeType)) {
                    log.info("subFee: {}", dto);
                    subFee = dto.getBalance();
                }
            }
            if (subSum == null || subFee == null || subVa == null) {
                log.error("subSum: {}, subFee: {}, subVa: {}", subSum, subFee, subVa);
                throw new RenException("internal logical error");
            }
            subVa.setBalanceSubFee(subFee);
            subVa.setBalanceSubSum(subSum);
            newList.add(subVa);
        }
        page.setList(newList);
        page.setTotal(15);
        return new Result<PageData<JBalanceDTO>>().ok(page);
    }

    @GetMapping("list")
    @Operation(summary = "list")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jbalance:list')")
    public Result<List<JBalanceDTO>> list(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        params.put(Constant.PAGE, "1");
        params.put(Constant.LIMIT, "1000");
        List<JBalanceDTO> list = jBalanceService.list(params);
        return new Result<List<JBalanceDTO>>().ok(list);
    }

    // 查询指定子商户的父商户的va
    @GetMapping("parent")
    @Operation(summary = "parent")
    @PreAuthorize("hasAuthority('zorg:jbalance:parent')")
    public Result<JBalanceDTO> parent(@RequestParam("ownerId") Long ownerId,
                                      @RequestParam("balanceType") String balanceType,
                                      @RequestParam("currency") String currency
    ) {
        SysDeptEntity deptEntity = sysDeptDao.selectById(ownerId);
        if (deptEntity == null) {
            log.error("can not find dept: {}", ownerId);
            throw new RenException("无法找到到部门");
        }
        Long pid = deptEntity.getPid();
        JBalanceEntity jBalanceEntity = jBalanceDao.selectOne(Wrappers.<JBalanceEntity>lambdaQuery()
                .eq(JBalanceEntity::getOwnerId, pid)
                .eq(JBalanceEntity::getCurrency, currency)
                .eq(JBalanceEntity::getBalanceType, balanceType)
        );
        JBalanceDTO jBalanceDTO = ConvertUtils.sourceToTarget(jBalanceEntity, JBalanceDTO.class);
        return new Result<JBalanceDTO>().ok(jBalanceDTO);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jbalance:info')")
    public Result<JBalanceDTO> get(@PathVariable("id") Long id) {
        JBalanceDTO data = jBalanceService.get(id);
        return new Result<JBalanceDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jbalance:save') || hasAuthority('zorg:jbalance:adjust')")
    public Result save(@RequestBody JBalanceDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jBalanceService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jbalance:update')")
    public Result update(@RequestBody JBalanceDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jBalanceService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jbalance:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jBalanceService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jbalance:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JBalanceDTO> list = jBalanceService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_balance", list, JBalanceExcel.class);
    }

}