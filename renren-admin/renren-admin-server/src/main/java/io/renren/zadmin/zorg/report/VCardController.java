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
import io.renren.zadmin.dto.VCardDTO;
import io.renren.zadmin.excel.VCardExcel;
import io.renren.zadmin.service.VCardService;
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
@RequestMapping("zorg/vcard")
@Tag(name = "VIEW")
public class VCardController {
    @Resource
    private VCardService vCardService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:vcard:page')")
    public Result<PageData<VCardDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<VCardDTO> page = vCardService.page(params);

        UserDetail user = SecurityUser.getUser();
        if(!ZestConstant.USER_TYPE_OPERATION.equals(user.getUserType())) {
            for (VCardDTO vCardDTO : page.getList()) {
                vCardDTO.setFee(null);
            }
        }

        return new Result<PageData<VCardDTO>>().ok(page);
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:vcard:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<VCardDTO> list = vCardService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "VIEW", list, VCardExcel.class);
    }

}