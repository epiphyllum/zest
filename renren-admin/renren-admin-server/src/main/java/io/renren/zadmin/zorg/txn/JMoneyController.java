package io.renren.zadmin.zorg.txn;

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
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zcommon.CommonUtils;
import io.renren.zmanager.JMoneyManager;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JMoneyDao;
import io.renren.zadmin.dto.JMoneyDTO;
import io.renren.zadmin.entity.JMoneyEntity;
import io.renren.zadmin.excel.JMoneyExcel;
import io.renren.zadmin.service.JMoneyService;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * j_money
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@RestController
@RequestMapping("zorg/jmoney")
@Tag(name = "j_money")
@Slf4j
public class JMoneyController {
    @Resource
    private JMoneyService jMoneyService;
    @Resource
    private JMoneyDao jMoneyDao;
    @Resource
    private JMaccountDao jMaccountDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;
    @Resource
    private JMoneyManager jMoneyManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmoney:page')")
    public Result<PageData<JMoneyDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        PageData<JMoneyDTO> page = jMoneyService.page(params);
        return new Result<PageData<JMoneyDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmoney:info')")
    public Result<JMoneyDTO> get(@PathVariable("id") Long id) {
        JMoneyDTO data = jMoneyService.get(id);
        return new Result<JMoneyDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmoney:save')")
    public Result<Long> save(@RequestBody JMoneyDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JMoneyEntity entity = ConvertUtils.sourceToTarget(dto, JMoneyEntity.class);
        entity.setApi(0);
        entity.setMeraplid(CommonUtils.uniqueId());
        JMerchantEntity merchant = jMerchantDao.selectById(dto.getMerchantId());
        jMoneyManager.saveAndSubmit(entity, merchant, dto.getCardid());
        Result<Long> result = new Result<>();
        result.setData(entity.getId());
        return result;
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmoney:update')")
    public Result update(@RequestBody JMoneyDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jMoneyService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmoney:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jMoneyService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmoney:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMoneyDTO> list = jMoneyService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "入金流水", list, JMoneyExcel.class);
    }

    //////////////////////////////////////////
    @GetMapping("confirm")
    @Operation(summary = "确认申请单")
    @LogOperation("确认申请单")
    @PreAuthorize("hasAuthority('zorg:jmoney:update')")
    public Result confirm(
            @RequestParam("id") Long id,
            @RequestParam("transferfid") String transferfid,
            @RequestParam("otherfid") String otherfid,
            @RequestParam("applyAmount") String applyAmount
    ) {
        // 查询出来
        JMoneyEntity jMoneyEntity = jMoneyDao.selectById(id);
        jMoneyEntity.setTransferfid(transferfid);
        jMoneyEntity.setOtherfid(otherfid);
        jMoneyEntity.setApplyAmount(new BigDecimal(applyAmount));
        jMoneyManager.confirm(jMoneyEntity);
        return new Result();
    }

    //////////////////////////////////////////
    @GetMapping("makeup")
    @Operation(summary = "补充材料")
    @LogOperation("补充材料")
    @PreAuthorize("hasAuthority('zorg:jmoney:makeup')")
    public Result confirm(
            @RequestParam("id") Long id,
            @RequestParam("transferfid") String transferfid,
            @RequestParam("otherfid") String otherfid
    ) {
        // 查询出来
        JMoneyEntity jMoneyEntity = jMoneyDao.selectById(id);
        jMoneyEntity.setTransferfid(transferfid);
        jMoneyEntity.setOtherfid(otherfid);
        jMoneyManager.makeup(jMoneyEntity);
        return new Result();
    }

    /**
     * 匹配来账
     *
     * @param id
     * @return
     */
    @GetMapping("match")
    @Operation(summary = "匹配来账")
    @LogOperation("匹配来账")
    @PreAuthorize("hasAuthority('zorg:jmoney:match')")
    public Result match(@RequestParam("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }

        jMoneyManager.matchMoney(id);

        return new Result();
    }

    //////////////////////////////////////////
    @GetMapping("mockMoneyInNotify")
    @Operation(summary = "模拟入账通知")
    @LogOperation("模拟入账通知")
    @PreAuthorize("hasAuthority('zorg:jmoney:mockNotify')")
    public Result mockMoneyInNotify(@RequestParam("id") Long id) {
        // 查询出来
        JMoneyEntity jMoneyEntity = jMoneyDao.selectById(id);
        jMoneyManager.mockMoneyInNotify(jMoneyEntity);
        return new Result();
    }

    //////////////////////////////////////////
    @GetMapping("manualNotify")
    @Operation(summary = "模拟入账通知")
    @LogOperation("模拟入账通知")
    @PreAuthorize("hasAuthority('zorg:jmoney:manual')")
    public Result manualNotify(@RequestParam("id") Long id) {
        // 查询出来
        JMoneyEntity jMoneyEntity = jMoneyDao.selectById(id);
        jMoneyManager.mockMoneyInNotify(jMoneyEntity);
        return new Result();
    }

}