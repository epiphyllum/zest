package io.renren.zadmin.zorg.txn;

import io.renren.commons.log.annotation.LogOperation;
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
import io.renren.zadmin.dto.JMfreeDTO;
import io.renren.zadmin.entity.JMfreeEntity;
import io.renren.zadmin.excel.JMfreeExcel;
import io.renren.zadmin.service.JMfreeService;
import io.renren.zmanager.JMfreeManager;
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
* j_mfree
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-10-21
*/
@RestController
@RequestMapping("zorg/jmfree")
@Tag(name = "j_mfree")
public class JMfreeController {
    @Resource
    private JMfreeService jMfreeService;

    @Resource
    private JMfreeManager jMfreeManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmfree:page')")
    public Result<PageData<JMfreeDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JMfreeDTO> page = jMfreeService.page(params);

        return new Result<PageData<JMfreeDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmfree:info')")
    public Result<JMfreeDTO> get(@PathVariable("id") Long id){
        JMfreeDTO data = jMfreeService.get(id);

        return new Result<JMfreeDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmfree:save')")
    public Result save(@RequestBody JMfreeDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JMfreeEntity entity = ConvertUtils.sourceToTarget(dto, JMfreeEntity.class);
        jMfreeManager.save(entity);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmfree:update')")
    public Result update(@RequestBody JMfreeDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jMfreeService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmfree:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jMfreeService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmfree:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMfreeDTO> list = jMfreeService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_mfree", list, JMfreeExcel.class);
    }

}