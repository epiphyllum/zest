package io.renren.zadmin.zorg.member;

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
import io.renren.service.SysDeptService;
import io.renren.zadmin.dto.JAgentDTO;
import io.renren.zadmin.excel.JAgentExcel;
import io.renren.zadmin.service.JAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 代理管理
 * @author Mark sunlightcs@gmail.com
 * @since 3.0 2024-08-16
 */
@RestController
@RequestMapping("zorg/jagent")
@Tag(name = "j_agent")
public class JAgentController {
    @Resource
    private JAgentService jAgentService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jagent:page')")
    public Result<PageData<JAgentDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JAgentDTO> page = jAgentService.page(params);
        return new Result<PageData<JAgentDTO>>().ok(page);
    }

    /**
     * 获取代理列表
     */
    @GetMapping("agentList")
    public Result<List<JAgentDTO>> agentList() {
        List<JAgentDTO> list = jAgentService.list(new HashMap<>());
        return Result.one(list);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jagent:info')")
    public Result<JAgentDTO> get(@PathVariable("id") Long id) {
        JAgentDTO data = jAgentService.get(id);
        return new Result<JAgentDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jagent:save')")
    public Result save(@RequestBody JAgentDTO dto) {

        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation")) {
            return Result.fail(9999, "not authorized");
        }

        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jAgentService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jagent:update')")
    public Result update(@RequestBody JAgentDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jAgentService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jagent:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jAgentService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jagent:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JAgentDTO> list = jAgentService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_agent", list, JAgentExcel.class);
    }

}