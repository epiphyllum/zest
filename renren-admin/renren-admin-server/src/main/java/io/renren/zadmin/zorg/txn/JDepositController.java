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
import io.renren.manager.JDepositManager;
import io.renren.zadmin.dao.JDepositDao;
import io.renren.zadmin.dto.JDepositDTO;
import io.renren.zadmin.entity.JDepositEntity;
import io.renren.zadmin.entity.JMcardEntity;
import io.renren.zadmin.excel.JDepositExcel;
import io.renren.zadmin.service.JDepositService;
import io.renren.zin.config.CommonUtils;
import io.renren.zin.service.cardapply.dto.TCardApplyQuery;
import io.renren.zin.service.cardapply.dto.TCardApplyResponse;
import io.renren.zin.service.cardapply.dto.TCardMainApplyRequest;
import io.renren.zin.service.cardapply.dto.TCardMainApplyResponse;
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
 * j_deposit
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@RestController
@RequestMapping("zorg/jdeposit")
@Tag(name = "j_deposit")
public class JDepositController {
    @Resource
    private JDepositService jDepositService;
    @Resource
    private JDepositDao jDepositDao;
    @Resource
    private JDepositManager jDepositManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jdeposit:page')")
    public Result<PageData<JDepositDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JDepositDTO> page = jDepositService.page(params);
        return new Result<PageData<JDepositDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jdeposit:info')")
    public Result<JDepositDTO> get(@PathVariable("id") Long id) {
        JDepositDTO data = jDepositService.get(id);
        return new Result<JDepositDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jdeposit:save')")
    public Result<Long> save(@RequestBody JDepositDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JDepositEntity entity = ConvertUtils.sourceToTarget(dto, JDepositEntity.class);
        entity.setApi(0);
        entity.setMeraplid(CommonUtils.newRequestId());
        jDepositManager.save(entity);
        return new Result<Long>().ok(entity.getId());
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jdeposit:update')")
    public Result update(@RequestBody JDepositDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jDepositManager.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jdeposit:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jDepositService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jdeposit:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JDepositDTO> list = jDepositService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_deposit", list, JDepositExcel.class);
    }

    @GetMapping("submit")
    @Operation(summary = "提交通联")
    @LogOperation("提交通联")
    @PreAuthorize("hasAuthority('zorg:jdeposit:submit')")
    public Result submit(@RequestParam("id") Long id) {
        JDepositEntity entity = jDepositDao.selectById(id);
        jDepositManager.submit(entity);
        return Result.ok;
    }

    @GetMapping("cancel")
    @Operation(summary = "作废")
    @LogOperation("作废")
    @PreAuthorize("hasAuthority('zorg:jdeposit:cancel')")
    public Result cancel(@RequestParam("id") Long id) {
        JDepositEntity entity = jDepositDao.selectById(id);
        jDepositManager.cancel(entity);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "查询通联")
    @LogOperation("查询通联")
    @PreAuthorize("hasAuthority('zorg:jdeposit:query')")
    public Result query(@RequestParam("id") Long id) {
        JDepositEntity entity = jDepositDao.selectById(id);
        jDepositManager.query(entity);
        return Result.ok;
    }


}