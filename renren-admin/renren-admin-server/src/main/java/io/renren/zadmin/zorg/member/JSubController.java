package io.renren.zadmin.zorg.member;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zadmin.dao.JSubDao;
import io.renren.zadmin.dto.JAgentDTO;
import io.renren.zadmin.dto.JSubDTO;
import io.renren.zadmin.entity.JSubEntity;
import io.renren.zadmin.excel.JSubExcel;
import io.renren.zadmin.service.JAgentService;
import io.renren.zadmin.service.JSubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 子商户管理
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
    private JAgentService jAgentService;
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
        params.put("child", 1L);
        PageData<JSubDTO> page = jSubService.page(params);
        return new Result<PageData<JSubDTO>>().ok(page);
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
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        jSubService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jsub:update')")
    public Result update(@RequestBody JSubDTO dto) {
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


    /**
     * 获取代理列表
     *
     * @return
     */
    @GetMapping("agentList")
    @PreAuthorize("hasAuthority('zorg:jsub:info')")
    public Result<List<JAgentDTO>> agentList() {
        List<JAgentDTO> list = jAgentService.list(new HashMap<>());
        return Result.one(list);
    }


    /**
     *
     * @return
     */
    @GetMapping("merchantList")
    @PreAuthorize("hasAuthority('zorg:jsub:info')")
    public Result<List<JSubDTO>> merchantList() {
        Result<List<JSubDTO>> result = new Result<>();
        List<JSubEntity> jSubEntities = jSubDao.selectList(Wrappers.<JSubEntity>lambdaQuery());
        List<JSubDTO> jSubDTOS = ConvertUtils.sourceToTarget(jSubEntities, JSubDTO.class);
        result.setData(jSubDTOS);
        return result;
    }

}