package io.renren.zadmin.zorg.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import io.renren.zadmin.dao.JVaDao;
import io.renren.zadmin.dto.JVaDTO;
import io.renren.zadmin.entity.JVaEntity;
import io.renren.zadmin.excel.JVaExcel;
import io.renren.zadmin.service.JVaService;
import io.renren.zin.service.accountmanage.dto.TVaListRequest;
import io.renren.zin.service.accountmanage.dto.TVaListResponse;
import io.renren.zin.service.accountmanage.ZinAccountManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 大吉VA
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-20
 */
@RestController
@RequestMapping("zorg/jva")
@Tag(name = "j_va")
@Slf4j
public class JVaController {
    @Resource
    private JVaService jVaService;
    @Resource
    private ZinAccountManageService zinAccountManageService;
    @Resource
    private JVaDao jVaDao;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jva:page')")
    public Result<PageData<JVaDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        params.put(Constant.PAGE, "1");
        params.put(Constant.LIMIT, "15");
        PageData<JVaDTO> page = jVaService.page(params);
        return new Result<PageData<JVaDTO>>().ok(page);
    }

    @GetMapping("list")
    @Operation(summary = "list")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jva:page')")
    public Result<List<JVaDTO>> list(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        List<JVaDTO> list = ConvertUtils.sourceToTarget(jVaService.list(params), JVaDTO.class);
        return new Result<List<JVaDTO>>().ok(list);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jva:info')")
    public Result<JVaDTO> get(@PathVariable("id") Long id) {
        JVaDTO data = jVaService.get(id);
        return new Result<JVaDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jva:save')")
    public Result save(@RequestBody JVaDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") &&
                !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        TVaListResponse tVaListResponse = zinAccountManageService.vaList(new TVaListRequest());
        Long aLong = jVaDao.selectCount(Wrappers.emptyWrapper());
        List<JVaEntity> jVaEntities = new ArrayList<>(tVaListResponse.getAccts().size());
        for (TVaListResponse.VaItem acct : tVaListResponse.getAccts()) {
            JVaEntity jVaEntity = ConvertUtils.sourceToTarget(acct, JVaEntity.class);
            jVaEntity.setTid(acct.getId());
            jVaEntities.add(jVaEntity);
        }
        if (aLong > 0) {
            for (JVaEntity jVaEntity : jVaEntities) {
                jVaDao.update(null, Wrappers.<JVaEntity>lambdaUpdate()
                        .eq(JVaEntity::getAccountno, jVaEntity.getAccountno())
                        .set(JVaEntity::getAmount, jVaEntity.getAmount())
                        .set(JVaEntity::getTid, jVaEntity.getTid())
                        .set(JVaEntity::getUpdateDate, new Date())
                );
            }
        } else {
            for (JVaEntity jVaEntity : jVaEntities) {
                jVaDao.insert(jVaEntity);
            }
        }
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jva:update')")
    public Result update(@RequestBody JVaDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jVaService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jva:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") &&
                !user.getUserType().equals("agent")
        ) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jVaService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jva:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JVaDTO> list = jVaService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_va", list, JVaExcel.class);
    }

}