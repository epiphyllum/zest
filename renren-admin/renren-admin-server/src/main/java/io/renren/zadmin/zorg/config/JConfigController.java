package io.renren.zadmin.zorg.config;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dto.JConfigDTO;
import io.renren.zadmin.excel.JConfigExcel;
import io.renren.zadmin.service.JConfigService;
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
* j_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-09
*/
@RestController
@RequestMapping("zorg/jconfig")
@Tag(name = "j_config")
public class JConfigController {
    @Resource
    private JConfigService jConfigService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jconfig:page')")
    public Result<PageData<JConfigDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        if (!ZestConstant.isOperation()) {
            throw new RenException("not permitted");
        }
        PageData<JConfigDTO> page = jConfigService.page(params);

        return new Result<PageData<JConfigDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jconfig:info')")
    public Result<JConfigDTO> get(@PathVariable("id") Long id){
        JConfigDTO data = jConfigService.get(id);
        return new Result<JConfigDTO>().ok(data);
    }

    @GetMapping("single")
    @Operation(summary = "信息")
//    @PreAuthorize("hasAuthority('zorg:jconfig:single')")
    public Result<JConfigDTO> get(){
        List<JConfigDTO> cfgList = jConfigService.list(new HashMap<>());
        JConfigDTO jConfigDTO = cfgList.get(0);
        return new Result<JConfigDTO>().ok(jConfigDTO);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jconfig:save')")
    public Result save(@RequestBody JConfigDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        jConfigService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jconfig:update')")
    public Result update(@RequestBody JConfigDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jConfigService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jconfig:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jConfigService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jconfig:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JConfigDTO> list = jConfigService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_config", list, JConfigExcel.class);
    }

}