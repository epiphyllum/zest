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
import io.renren.zadmin.dto.JPayChannelDTO;
import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zadmin.excel.JPayChannelExcel;
import io.renren.zadmin.service.JPayChannelService;
import io.renren.zmanager.JPayChannelManager;
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
* j_pay_channel
*
* @author epiphyllum epiphyllum.zhou@gmail.com
* @since 3.0 2024-11-28
*/
@RestController
@RequestMapping("zorg/jpaychannel")
@Tag(name = "j_pay_channel")
public class JPayChannelController {
    @Resource
    private JPayChannelService jPayChannelService;
    @Resource
    private JPayChannelManager jPayChannelManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jpaychannel:page')")
    public Result<PageData<JPayChannelDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params){
        PageData<JPayChannelDTO> page = jPayChannelService.page(params);

        return new Result<PageData<JPayChannelDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jpaychannel:info')")
    public Result<JPayChannelDTO> get(@PathVariable("id") Long id){
        JPayChannelDTO data = jPayChannelService.get(id);

        return new Result<JPayChannelDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jpaychannel:save')")
    public Result save(@RequestBody JPayChannelDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        JPayChannelEntity jPayChannelEntity = ConvertUtils.sourceToTarget(dto, JPayChannelEntity.class);
        jPayChannelManager.save(jPayChannelEntity);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jpaychannel:update')")
    public Result update(@RequestBody JPayChannelDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        jPayChannelService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jpaychannel:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        jPayChannelService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jpaychannel:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JPayChannelDTO> list = jPayChannelService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_pay_channel", list, JPayChannelExcel.class);
    }

}