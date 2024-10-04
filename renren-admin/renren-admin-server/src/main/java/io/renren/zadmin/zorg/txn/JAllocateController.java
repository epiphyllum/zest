package io.renren.zadmin.zorg.txn;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
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
import io.renren.manager.JAllocateManager;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JAllocateDTO;
import io.renren.zadmin.entity.JAllocateEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.excel.JAllocateExcel;
import io.renren.zadmin.service.JAllocateService;
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
 * j_inout
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-09-01
 */
@RestController
@RequestMapping("zorg/jallocate")
@Tag(name = "j_allocate")
public class JAllocateController {
    @Resource
    private JAllocateService jAllocateService;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JAllocateManager jAllocateManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jallocate:page')")
    public Result<PageData<JAllocateDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JAllocateDTO> page = jAllocateService.page(params);
        return new Result<PageData<JAllocateDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jallocate:info')")
    public Result<JAllocateDTO> get(@PathVariable("id") Long id) {
        JAllocateDTO data = jAllocateService.get(id);
        return new Result<JAllocateDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jallocate:save')")
    public Result save(@RequestBody JAllocateDTO dto) {
        // 商户才能调拨资金
        UserDetail user = SecurityUser.getUser();
        if (!"merchant".equals(user.getUserType()) && !"operation".equals(user.getUserType())) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        // 填充代理 + 商户名
        JMerchantEntity merchant = jMerchantDao.selectById(dto.getMerchantId());
        dto.setAgentId(merchant.getAgentId());
        dto.setAgentName(merchant.getAgentName());
        dto.setMerchantName(merchant.getCusname());

        // 非api操作
        dto.setApi(0);

        // 子商户-商户资金调度， 需要子商户信息
        String type = dto.getType();
        if (type.equals("s2m") || type.equals("m2s")) {
            JSubEntity subEntity = jSubDao.selectById(dto.getSubId());
            dto.setSubName(subEntity.getCusname());
        }
        JAllocateEntity jAllocateEntity = ConvertUtils.sourceToTarget(dto, JAllocateEntity.class);

        // 资金调度统一入口
        jAllocateManager.handleAllocation(jAllocateEntity, merchant);
        return new Result();
    }


    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jallocate:update')")
    public Result update(@RequestBody JAllocateDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jAllocateService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jallocate:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jAllocateService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jallocate:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JAllocateDTO> list = jAllocateService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_allocate", list, JAllocateExcel.class);
    }

}