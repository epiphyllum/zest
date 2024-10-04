package io.renren.zadmin.zorg.member;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import io.renren.manager.JMerchantManager;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JMerchantDTO;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.excel.JMerchantExcel;
import io.renren.zadmin.service.JAgentService;
import io.renren.zadmin.service.JMerchantService;
import io.renren.zin.service.file.ZinFileService;
import io.renren.zin.service.sub.ZinSubService;
import io.renren.zin.service.sub.dto.TSubCreateRequest;
import io.renren.zin.service.sub.dto.TSubCreateResponse;
import io.renren.zin.service.sub.dto.TSubQuery;
import io.renren.zin.service.sub.dto.TSubQueryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * 商户管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-18
 */
@RestController
@RequestMapping("zorg/jmerchant")
@Tag(name = "j_merchant")
@Slf4j
public class JMerchantController {

    @Resource
    private JMerchantManager jMerchantManager;
    @Resource
    private JMerchantService jMerchantService;
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
    @PreAuthorize("hasAuthority('zorg:jmerchant:page')")
    public Result<PageData<JMerchantDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JMerchantDTO> page = jMerchantService.page(params);
        return new Result<PageData<JMerchantDTO>>().ok(page);
    }

    /**
     * 商户列表
     */
    @GetMapping("list")
    public Result<List<JMerchantDTO>> list(
            @RequestParam(value = "agentId", required = false) Long agentId,
            @RequestParam(value = "merchantId", required = false) Long merchantId
    ) {
        UserDetail user = SecurityUser.getUser();
        System.out.println("userType = " + user.getUserType());
        List<JMerchantEntity> jMerchantEntities = jMerchantDao.selectList(Wrappers.<JMerchantEntity>lambdaQuery()
                .eq(agentId != null, JMerchantEntity::getAgentId, agentId)
                .eq(merchantId != null, JMerchantEntity::getId, merchantId)
                .eq("agent".equals(user.getUserType()), JMerchantEntity::getAgentId, user.getDeptId())
                .eq("merchant".equals(user.getUserType()), JMerchantEntity::getId, user.getDeptId())
        );

        Result<List<JMerchantDTO>> result = new Result<>();
        List<JMerchantDTO> dtos = ConvertUtils.sourceToTarget(jMerchantEntities, JMerchantDTO.class);
        result.setData(dtos);
        return result;
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmerchant:info')")
    public Result<JMerchantDTO> get(@PathVariable("id") Long id) {
        JMerchantDTO data = jMerchantService.get(id);
        return new Result<JMerchantDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmerchant:save')")
    public Result save(@RequestBody JMerchantDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        jMerchantService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmerchant:update')")
    public Result update(@RequestBody JMerchantDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jMerchantService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmerchant:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jMerchantService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmerchant:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMerchantDTO> list = jMerchantService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_merchant", list, JMerchantExcel.class);
    }

    /**
     * 发起到通联创建子商户
     */
    @GetMapping("createAllinpay")
    @PreAuthorize("hasAuthority('zorg:jmerchant:update')")
    public Result createAllinpay(@RequestParam("id") Long id) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(id);
        jMerchantManager.submit(jMerchantEntity);
        return new Result();
    }

    /**
     * 发起到通联查子商户
     */
    @GetMapping("queryAllinpay")
    @PreAuthorize("hasAuthority('zorg:jmerchant:update')")
    public Result queryAllinpay(@RequestParam("id") Long id) {
        JMerchantEntity jMerchantEntity = jMerchantDao.selectById(id);
        jMerchantManager.query(jMerchantEntity);
        return new Result();
    }

}