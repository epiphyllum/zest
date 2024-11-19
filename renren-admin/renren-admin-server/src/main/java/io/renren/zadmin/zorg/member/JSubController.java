package io.renren.zadmin.zorg.member;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zcommon.ZinConstant;
import io.renren.zmanager.JSubManager;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JSubDTO;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.excel.JSubExcel;
import io.renren.zadmin.service.JSubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 子商户管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jsub")
@Tag(name = "j_jsub")
public class JSubController {
    @Resource
    private JSubService jSubService;
    @Resource
    private JSubManager jSubManager;
    @Resource
    private JSubDao jSubDao;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jsub:page')")
    public Result<PageData<JSubDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JSubDTO> page = jSubService.page(params);
        return new Result<PageData<JSubDTO>>().ok(page);
    }

    /**
     * @return
     */
    @GetMapping("list")
    public Result<List<JSubDTO>> list(
            @RequestParam(value = "merchantId", required = false) Long merchantId,
            @RequestParam(value = "agentId", required = false) Long agentId
    ) {
        Result<List<JSubDTO>> result = new Result<>();
        List<JSubEntity> jSubEntities = jSubDao.selectList(Wrappers.<JSubEntity>lambdaQuery()
                .eq(merchantId != null, JSubEntity::getMerchantId, merchantId)
                .eq(agentId != null, JSubEntity::getAgentId, agentId)
        );
        List<JSubDTO> jSubDTOS = ConvertUtils.sourceToTarget(jSubEntities, JSubDTO.class);
        result.setData(jSubDTOS);
        return result;
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jsub:info')")
    public Result<JSubDTO> get(@PathVariable("id") Long id) {
        JSubDTO data = jSubService.get(id);
        return new Result<JSubDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jsub:save')")
    public Result save(@RequestBody JSubDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JSubEntity jSubEntity = ConvertUtils.sourceToTarget(dto, JSubEntity.class);
        jSubManager.save(jSubEntity);
        return new Result();
    }

    /**
     * 审核
     */
    @GetMapping("verify")
    @Operation(summary = "审核")
    @LogOperation("审核")
    @PreAuthorize("hasAuthority('zorg:jsub:verify')")
    public Result verify(@RequestParam("id") Long id, @RequestParam("state") String state) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized");
        }


        jSubManager.verify(id, state);
        return new Result();
    }


    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jsub:update')")
    public Result update(@RequestBody JSubDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jSubService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jsub:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent") && !user.getUserType().equals("merchant")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jSubService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jsub:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JSubDTO> list = jSubService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_merchant", list, JSubExcel.class);
    }

}