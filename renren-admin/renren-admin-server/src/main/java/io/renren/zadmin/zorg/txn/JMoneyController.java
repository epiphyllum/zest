package io.renren.zadmin.zorg.txn;

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
import io.renren.zadmin.dto.JMoneyDTO;
import io.renren.zadmin.excel.JMoneyExcel;
import io.renren.zadmin.service.JMoneyService;
import io.renren.zin.service.accountmanage.AccountManageNotify;
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
* j_money
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-08-20
*/
@RestController
@RequestMapping("zorg/jmoney")
@Tag(name = "j_money")
public class JMoneyController {
    @Resource
    private JMoneyService jMoneyService;
    @Resource
    private AccountManageNotify accountManageNotify;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmoney:page')")
    public Result<PageData<JMoneyDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JMoneyDTO> page = jMoneyService.page(params);
        return new Result<PageData<JMoneyDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmoney:info')")
    public Result<JMoneyDTO> get(@PathVariable("id") Long id){
        JMoneyDTO data = jMoneyService.get(id);
        return new Result<JMoneyDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmoney:save')")
    public Result save(@RequestBody JMoneyDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jMoneyService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmoney:update')")
    public Result update(@RequestBody JMoneyDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jMoneyService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmoney:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jMoneyService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmoney:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMoneyDTO> list = jMoneyService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "入金流水", list, JMoneyExcel.class);
    }

    //////////////////////////////////////////
    /**
     * 匹配来账
     * @param id
     * @return
     */
    @GetMapping("match")
    @Operation(summary = "匹配来账")
    @LogOperation("匹配来账")
    @PreAuthorize("hasAuthority('zorg:jmoney:update')")
    public Result match(@RequestParam("id") Long id)  {

        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }

        if (accountManageNotify.match(id)) {
            return Result.ok;
        } else {
            return Result.fail(9999, "匹配失败");
        }
    }

}