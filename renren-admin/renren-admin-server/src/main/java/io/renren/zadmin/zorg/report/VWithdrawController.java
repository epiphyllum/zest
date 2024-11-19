package io.renren.zadmin.zorg.report;

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
import io.renren.zadmin.dto.VWithdrawDTO;
import io.renren.zadmin.excel.VWithdrawExcel;
import io.renren.zadmin.service.VWithdrawService;
import io.renren.zcommon.ZestConstant;
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
 * VIEW
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-18
 */
@RestController
@RequestMapping("zorg/vwithdraw")
@Tag(name = "VIEW")
public class VWithdrawController {
    @Resource
    private VWithdrawService vWithdrawService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:vwithdraw:page')")
    public Result<PageData<VWithdrawDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<VWithdrawDTO> page = vWithdrawService.page(params);
        UserDetail user = SecurityUser.getUser();
        if (!ZestConstant.USER_TYPE_OPERATION.equals(user.getUserType())) {
            for (VWithdrawDTO vWithdrawDTO : page.getList()) {
                vWithdrawDTO.setAipCharge(null);
            }
        }
        return new Result<PageData<VWithdrawDTO>>().ok(page);
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:vwithdraw:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<VWithdrawDTO> list = vWithdrawService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "VIEW", list, VWithdrawExcel.class);
    }

}