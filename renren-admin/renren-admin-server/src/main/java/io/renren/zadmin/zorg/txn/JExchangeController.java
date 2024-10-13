package io.renren.zadmin.zorg.txn;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.zmanager.JExchangeManager;
import io.renren.zcommon.ZestConstant;
import io.renren.zadmin.dao.JExchangeDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JExchangeDTO;
import io.renren.zadmin.entity.JExchangeEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zadmin.excel.JExchangeExcel;
import io.renren.zadmin.service.JExchangeService;
import io.renren.zcommon.CommonUtils;
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
 * j_exchange
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@RestController
@RequestMapping("zorg/jexchange")
@Tag(name = "j_exchange")
public class JExchangeController {
    @Resource
    private JExchangeService jExchangeService;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JExchangeDao jExchangeDao;
    @Resource
    private JExchangeManager jExchangeManager;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jexchange:page')")
    public Result<PageData<JExchangeDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }

        PageData<JExchangeDTO> page = jExchangeService.page(params);
        return new Result<PageData<JExchangeDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jexchange:info')")
    public Result<JExchangeDTO> get(@PathVariable("id") Long id) {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        JExchangeDTO data = jExchangeService.get(id);
        return new Result<JExchangeDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jexchange:save')")
    public Result<Long> save(@RequestBody JExchangeDTO dto) {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        // 商户才能发起换汇
        UserDetail user = SecurityUser.getUser();
        // 填充字段
        Long merchantId = null;
        if (user.getUserType().equals("merchant")) {
            merchantId = user.getDeptId();
        } else {
            merchantId = dto.getMerchantId();
        }
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);

        JExchangeEntity entity = ConvertUtils.sourceToTarget(dto, JExchangeEntity.class);
        entity.setAgentId(merchant.getAgentId());
        entity.setAgentName(merchant.getAgentName());
        entity.setMerchantId(merchantId);
        entity.setMerchantName(merchant.getCusname());
        entity.setMeraplid(CommonUtils.newRequestId());
        entity.setApi(0);

        jExchangeManager.save(entity);
        return new Result<Long>().ok(entity.getId());
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jexchange:update')")
    public Result update(@RequestBody JExchangeDTO dto) {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jExchangeService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jexchange:delete')")
    public Result delete(@RequestBody Long[] ids) {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jExchangeService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jexchange:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        List<JExchangeDTO> list = jExchangeService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_exchange", list, JExchangeExcel.class);
    }

    // 提交通联
    @GetMapping("submit")
    @PreAuthorize("hasAuthority('zorg:jexchange:submit')")
    public Result submit(@RequestParam("id") Long id) throws Exception {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        JExchangeEntity jExchangeEntity = jExchangeDao.selectById(id);
        jExchangeManager.submit(jExchangeEntity);
        return new Result();
    }

    // 查询通联
    @GetMapping("query")
    @PreAuthorize("hasAuthority('zorg:jexchange:query')")
    public Result query(@RequestParam("id") Long id) throws Exception {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        JExchangeEntity jExchangeEntity = jExchangeDao.selectById(id);
        jExchangeManager.query(jExchangeEntity);
        return new Result();
    }

    // 锁汇
    @GetMapping("lock")
    @PreAuthorize("hasAuthority('zorg:jexchange:lock')")
    public Result lock(@RequestParam("id") Long id) throws Exception {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        JExchangeEntity jExchangeEntity = jExchangeDao.selectById(id);
        jExchangeManager.lock(jExchangeEntity);
        return new Result();
    }

    // 确认
    @GetMapping("confirm")
    @PreAuthorize("hasAuthority('zorg:jexchange:confirm')")
    public Result confirm(@RequestParam("id") Long id) throws Exception {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        JExchangeEntity jExchangeEntity = jExchangeDao.selectById(id);
        jExchangeManager.confirm(jExchangeEntity);
        return new Result();
    }

    // 取消
    @GetMapping("cancel")
    @PreAuthorize("hasAuthority('zorg:jexchange:cancel')")
    public Result cancel(@RequestParam("id") Long id) throws Exception {
        if (!ZestConstant.isOperationOrAgentOrMerchant()) {
            throw new RenException("not permitted");
        }
        JExchangeEntity jExchangeEntity = jExchangeDao.selectById(id);
        jExchangeManager.cancel(jExchangeEntity);
        return new Result();
    }
}