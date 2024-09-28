package io.renren.zadmin.zorg.config;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zadmin.dto.JCardDTO;
import io.renren.zadmin.excel.JCardExcel;
import io.renren.zadmin.service.JCardService;
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
 * 子卡管理
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jscard")
@Tag(name = "j_scard")
public class JCardController {
    @Resource
    private JCardService jCardService;

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
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jCardService.save(dto);
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

}