package io.renren.zadmin.zorg.balance;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dto.JLogDTO;
import io.renren.zadmin.excel.JLogExcel;
import io.renren.zadmin.service.JLogService;
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
 * j_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-17
 */
@RestController
@RequestMapping("zorg/jlog")
@Tag(name = "j_log")
public class JLogController {
    @Resource
    private JLogService jLogService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize(
            "hasAuthority('zorg:jlog:page') || " +
                    "hasAuthority('zorg:jlog-va:page') || " +
                    "hasAuthority('zorg:jlog-deposit:page') || " +
                    "hasAuthority('zorg:jlog-fee:page') ||" +
                    "hasAuthority('zorg:jlog-txnfee:page') ||" +
                    "hasAuthority('zorg:jlog-subva:page') ||" +
                    "hasAuthority('zorg:jlog-subsum:page') ||" +
                    "hasAuthority('zorg:jlog-subfee:page')"
    )
    public Result<PageData<JLogDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        params.put(Constant.ORDER, "desc");
        if (ZestConstant.isMerchant()) {
            params.put("merchantId", SecurityUser.getDeptId().toString());
        }
        else if (ZestConstant.isSub()) {
            params.put("subId", SecurityUser.getDeptId().toString());
        }
        else if (ZestConstant.isAgent()) {
            params.put("agentId", SecurityUser.getDeptId().toString());
        }
        PageData<JLogDTO> page = jLogService.page(params);
        return new Result<PageData<JLogDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jlog:info')")
    public Result<JLogDTO> get(@PathVariable("id") Long id) {
        JLogDTO data = jLogService.get(id);
        return new Result<JLogDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jlog:save')")
    public Result save(@RequestBody JLogDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jLogService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jlog:update')")
    public Result update(@RequestBody JLogDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jLogService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jlog:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jLogService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jlog:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JLogDTO> list = jLogService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "账变流水", list, JLogExcel.class);
    }

}