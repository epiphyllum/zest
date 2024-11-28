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
import io.renren.zadmin.dto.JWalletConfigDTO;
import io.renren.zadmin.entity.JWalletConfigEntity;
import io.renren.zadmin.excel.JWalletConfigExcel;
import io.renren.zadmin.service.JWalletConfigService;
import io.renren.zmanager.JWalletConfigManager;
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
* j_wallet_config
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@RestController
@RequestMapping("zorg/jwalletconfig")
@Tag(name = "j_wallet_config")
public class JWalletConfigController {
    @Resource
    private JWalletConfigService jWalletConfigService;

    @Resource
    private JWalletConfigManager jWalletConfigManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jwalletconfig:page')")
    public Result<PageData<JWalletConfigDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JWalletConfigDTO> page = jWalletConfigService.page(params);

        return new Result<PageData<JWalletConfigDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jwalletconfig:info')")
    public Result<JWalletConfigDTO> get(@PathVariable("id") Long id){
        JWalletConfigDTO data = jWalletConfigService.get(id);

        return new Result<JWalletConfigDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jwalletconfig:save')")
    public Result save(@RequestBody JWalletConfigDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        JWalletConfigEntity jWalletConfigEntity = ConvertUtils.sourceToTarget(dto, JWalletConfigEntity.class);
        jWalletConfigManager.save(jWalletConfigEntity);


        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jwalletconfig:update')")
    public Result update(@RequestBody JWalletConfigDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jWalletConfigService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jwalletconfig:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jWalletConfigService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jwalletconfig:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JWalletConfigDTO> list = jWalletConfigService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_wallet_config", list, JWalletConfigExcel.class);
    }

}