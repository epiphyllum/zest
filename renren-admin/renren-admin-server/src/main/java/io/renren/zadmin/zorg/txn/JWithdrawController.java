package io.renren.zadmin.zorg.txn;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.manager.JWithdrawManager;
import io.renren.zadmin.dao.JWithdrawDao;
import io.renren.zadmin.dto.JWithdrawDTO;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JWithdrawEntity;
import io.renren.zadmin.excel.JWithdrawExcel;
import io.renren.zadmin.service.JWithdrawService;
import io.renren.zadmin.service.impl.CommonFilter;
import io.renren.zin.config.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;


/**
 * j_withdraw
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@RestController
@RequestMapping("zorg/jwithdraw")
@Tag(name = "j_withdraw")
public class JWithdrawController {
    @Resource
    private JWithdrawService jWithdrawService;
    @Resource
    private JWithdrawDao jWithdrawDao;
    @Resource
    private JWithdrawManager jWithdrawManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jwithdraw:page')")
    public Result<PageData<JWithdrawDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JWithdrawDTO> page = jWithdrawService.page(params);
        return new Result<PageData<JWithdrawDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:info')")
    public Result<JWithdrawDTO> get(@PathVariable("id") Long id) {
        JWithdrawDTO data = jWithdrawService.get(id);
        return new Result<JWithdrawDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:save')")
    public Result<Long> save(@RequestBody JWithdrawDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JWithdrawEntity jWithdrawEntity = ConvertUtils.sourceToTarget(dto, JWithdrawEntity.class);
        jWithdrawEntity.setApi(0);
        jWithdrawEntity.setMeraplid(CommonUtils.newRequestId());
        jWithdrawManager.save(jWithdrawEntity);
        return new Result<Long>().ok(jWithdrawEntity.getId());
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:update')")
    public Result update(@RequestBody JWithdrawDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jWithdrawService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jWithdrawService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JWithdrawDTO> list = jWithdrawService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "提取资金", list, JWithdrawExcel.class);
    }

    @GetMapping("submit")
    @Operation(summary = "提交通联")
    @LogOperation("提交通联")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:submit')")
    public Result submit(@RequestParam("id") Long id) {
        JWithdrawEntity entity = jWithdrawDao.selectById(id);
        jWithdrawManager.submit(entity);
        return Result.ok;
    }

    @GetMapping("cancel")
    @Operation(summary = "作废")
    @LogOperation("作废")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:cancel')")
    public Result cancel(@RequestParam("id") Long id) {
        JWithdrawEntity entity = jWithdrawDao.selectById(id);
        jWithdrawManager.cancel(entity);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "查询通联")
    @LogOperation("查询通联")
    @PreAuthorize("hasAuthority('zorg:jwithdraw:query')")
    public Result query(@RequestParam("id") Long id) {
        JWithdrawEntity entity = jWithdrawDao.selectById(id);
        jWithdrawManager.query(entity);
        return Result.ok;
    }

}