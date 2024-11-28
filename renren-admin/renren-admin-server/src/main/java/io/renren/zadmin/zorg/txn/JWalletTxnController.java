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
import io.renren.zadmin.dto.JWalletTxnDTO;
import io.renren.zadmin.excel.JWalletTxnExcel;
import io.renren.zadmin.service.JWalletTxnService;
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
* j_wallet_txn
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@RestController
@RequestMapping("zorg/jwallettxn")
@Tag(name = "j_wallet_txn")
public class JWalletTxnController {
    @Resource
    private JWalletTxnService jWalletTxnService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jwallettxn:page')")
    public Result<PageData<JWalletTxnDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JWalletTxnDTO> page = jWalletTxnService.page(params);

        return new Result<PageData<JWalletTxnDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jwallettxn:info')")
    public Result<JWalletTxnDTO> get(@PathVariable("id") Long id){
        JWalletTxnDTO data = jWalletTxnService.get(id);

        return new Result<JWalletTxnDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jwallettxn:save')")
    public Result save(@RequestBody JWalletTxnDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        jWalletTxnService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jwallettxn:update')")
    public Result update(@RequestBody JWalletTxnDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jWalletTxnService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jwallettxn:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jWalletTxnService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jwallettxn:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JWalletTxnDTO> list = jWalletTxnService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_wallet_txn", list, JWalletTxnExcel.class);
    }

}