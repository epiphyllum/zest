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
import io.renren.zadmin.dao.JVpaJobDao;
import io.renren.zadmin.dto.JVpaJobDTO;
import io.renren.zadmin.entity.JCardEntity;
import io.renren.zadmin.entity.JVpaJobEntity;
import io.renren.zadmin.excel.JVpaJobExcel;
import io.renren.zadmin.service.JVpaJobService;
import io.renren.zcommon.CommonUtils;
import io.renren.zmanager.JVpaManager;
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
 * j_vpa_log
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-11-01
 */
@RestController
@RequestMapping("zorg/jvpajob")
@Tag(name = "j_vpa_job")
public class JVpaJobController {
    @Resource
    private JVpaJobService jVpaJobService;

    @Resource
    private JVpaJobDao jVpaJobDao;

    @Resource
    private JVpaManager jVpaManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jvpajob:page')")
    public Result<PageData<JVpaJobDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JVpaJobDTO> page = jVpaJobService.page(params);
        return new Result<PageData<JVpaJobDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jvpajob:info')")
    public Result<JVpaJobDTO> get(@PathVariable("id") Long id) {
        JVpaJobDTO data = jVpaJobService.get(id);
        return new Result<JVpaJobDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jvpajob:save')")
    public Result save(@RequestBody JVpaJobDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        JVpaJobEntity jVpaJobEntity = ConvertUtils.sourceToTarget(dto, JVpaJobEntity.class);
        jVpaJobEntity.setApi(1);
        jVpaJobEntity.setMeraplid(CommonUtils.uniqueId());
        jVpaManager.save(jVpaJobEntity);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jvpajob:update')")
    public Result update(@RequestBody JVpaJobDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jVpaJobService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jvpajob:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jVpaJobService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jvpajob:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JVpaJobDTO> list = jVpaJobService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_vpa_log", list, JVpaJobExcel.class);
    }

    @GetMapping("submit")
    @Operation(summary = "开卡提交通联")
    @LogOperation("vpa开卡提交通联")
    @PreAuthorize("hasAuthority('zorg:jcard:update')")
    public Result submit(@RequestParam("id") Long id) {
        JVpaJobEntity entity = jVpaJobDao.selectById(id);
        jVpaManager.submit(entity);
        return Result.ok;
    }

    @GetMapping("query")
    @Operation(summary = "开卡查询通联")
    @LogOperation("vpa开卡查询通联")
    @PreAuthorize("hasAuthority('zorg:jcard:query')")
    public Result query(@RequestParam("id") Long id) {
        JVpaJobEntity entity = jVpaJobDao.selectById(id);
        jVpaManager.query(entity, false);
        return Result.ok;
    }

}