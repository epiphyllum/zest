package io.renren.zadmin.zorg.config;

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
import io.renren.zadmin.dto.JWalletDTO;
import io.renren.zadmin.entity.JWalletEntity;
import io.renren.zadmin.excel.JWalletExcel;
import io.renren.zadmin.service.JWalletService;
import io.renren.zmanager.JWalletManager;
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
* j_wallet
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-27
*/
@RestController
@RequestMapping("zorg/jwallet")
@Tag(name = "j_wallet")
public class JWalletController {
    @Resource
    private JWalletService jWalletService;
    @Resource
    private JWalletManager jWalletManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jwallet:page')")
    public Result<PageData<JWalletDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JWalletDTO> page = jWalletService.page(params);

        return new Result<PageData<JWalletDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jwallet:info')")
    public Result<JWalletDTO> get(@PathVariable("id") Long id){
        JWalletDTO data = jWalletService.get(id);
        return new Result<JWalletDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jwallet:save')")
    public Result save(@RequestBody JWalletDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JWalletEntity walletEntity = ConvertUtils.sourceToTarget(dto, JWalletEntity.class);
        jWalletManager.save(walletEntity);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jwallet:update')")
    public Result update(@RequestBody JWalletDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jWalletService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jwallet:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jWalletService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jwallet:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JWalletDTO> list = jWalletService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_wallet", list, JWalletExcel.class);
    }

}