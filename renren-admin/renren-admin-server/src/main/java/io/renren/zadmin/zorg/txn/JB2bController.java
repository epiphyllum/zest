package io.renren.zadmin.zorg.txn;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zadmin.dto.JB2bDTO;
import io.renren.zadmin.excel.JB2bExcel;
import io.renren.zadmin.service.JB2bService;
import io.renren.zin.b2b.B2bService;
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
* j_b2b
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2025-04-23
*/
@RestController
@RequestMapping("zorg/jb2b")
@Tag(name = "j_b2b")
public class JB2bController {
    @Resource
    private JB2bService jB2bService;
    @Resource
    private B2bService b2bService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jb2b:page')")
    public Result<PageData<JB2bDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JB2bDTO> page = jB2bService.page(params);

        return new Result<PageData<JB2bDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jb2b:info')")
    public Result<JB2bDTO> get(@PathVariable("id") Long id){
        JB2bDTO data = jB2bService.get(id);

        return new Result<JB2bDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jb2b:save')")
    public Result save(@RequestBody JB2bDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        jB2bService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jb2b:update')")
    public Result update(@RequestBody JB2bDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jB2bService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jb2b:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jB2bService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jb2b:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JB2bDTO> list = jB2bService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_b2b", list, JB2bExcel.class);
    }

    /////////////////////////
    @GetMapping("sync")
    @Operation(summary = "同步状态")
    @LogOperation("同步")
//    @PreAuthorize("hasAuthority('zorg:jb2b:sync')")
    public Result sync(@RequestParam("id") Long id){
        b2bService.sync(id);
        return new Result();
    }
}