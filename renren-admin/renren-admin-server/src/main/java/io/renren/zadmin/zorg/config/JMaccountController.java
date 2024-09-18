package io.renren.zadmin.zorg.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JMaccountDTO;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.excel.JMaccountExcel;
import io.renren.zadmin.service.JMaccountService;
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
 * 来账账户管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@RestController
@RequestMapping("zorg/jmaccount")
@Tag(name = "j_maccount")
public class JMaccountController {
    @Resource
    private JMaccountService jMaccountService;

    @Resource
    private JMerchantDao jMerchantDao;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmaccount:page')")
    public Result<PageData<JMaccountDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JMaccountDTO> page = jMaccountService.page(params);

        return new Result<PageData<JMaccountDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmaccount:info')")
    public Result<JMaccountDTO> get(@PathVariable("id") Long id) {
        JMaccountDTO data = jMaccountService.get(id);

        return new Result<JMaccountDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmaccount:save')")
    public Result save(@RequestBody JMaccountDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        jMaccountService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmaccount:update')")
    public Result update(@RequestBody JMaccountDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jMaccountService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmaccount:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jMaccountService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmaccount:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMaccountDTO> list = jMaccountService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "j_maccount", list, JMaccountExcel.class);
    }

    ///////////////
    @GetMapping("merchantList")
    @PreAuthorize("hasAuthority('zorg:jmaccount:info')")
    public Result<List<JMerchantDTO>> merchantList() {
        Result<List<JMerchantDTO>> result = new Result<>();
        List<JMerchantEntity> jMerchantEntities = jMerchantDao.selectList(Wrappers.<JMerchantEntity>lambdaQuery()
                .eq(JMerchantEntity::getParent, 0L)
        );
        List<JMerchantDTO> jMerchantDTOS = ConvertUtils.sourceToTarget(jMerchantEntities, JMerchantDTO.class);
        result.setData(jMerchantDTOS);
        return result;
    }

}