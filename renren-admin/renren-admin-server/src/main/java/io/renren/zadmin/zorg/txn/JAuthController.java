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
import io.renren.zadmin.dao.JAuthDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JAuthDTO;
import io.renren.zadmin.entity.JAuthEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.excel.JAuthExcel;
import io.renren.zadmin.service.JAuthService;
import io.renren.zmanager.JAuthManager;
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
* j_auth
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-19
*/
@RestController
@RequestMapping("zorg/jauth")
@Tag(name = "j_auth")
public class JAuthController {
    @Resource
    private JAuthService jAuthService;
    @Resource
    private JAuthManager jAuthManager;


    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jauth:page')")
    public Result<PageData<JAuthDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JAuthDTO> page = jAuthService.page(params);
        return new Result<PageData<JAuthDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jauth:info')")
    public Result<JAuthDTO> get(@PathVariable("id") Long id){
        JAuthDTO data = jAuthService.get(id);
        return new Result<JAuthDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jauth:save')")
    public Result save(@RequestBody JAuthDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jAuthService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jauth:update')")
    public Result update(@RequestBody JAuthDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jAuthService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jauth:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jAuthService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jauth:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JAuthDTO> list = jAuthService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_auth", list, JAuthExcel.class);
    }

    @GetMapping("notify")
    @Operation(summary = "通知商户")
    @LogOperation("通知商户")
    @PreAuthorize("hasAuthority('zorg:jauth:notify')")
    public Result notify( @RequestParam("id") Long id) throws Exception {
        jAuthManager.notify(id);
        return new Result();
    }
}