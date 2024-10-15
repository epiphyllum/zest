package io.renren.zadmin.zorg.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
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
import io.renren.dao.SysDeptDao;
import io.renren.entity.SysDeptEntity;
import io.renren.zadmin.dao.JMaccountDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.dto.JMaccountDTO;
import io.renren.zadmin.entity.JMaccountEntity;
import io.renren.zadmin.excel.JMaccountExcel;
import io.renren.zadmin.service.JMaccountService;
import io.renren.zcommon.CommonUtils;
import io.renren.zin.accountmanage.ZinAccountManageService;
import io.renren.zin.umbrella.ZinUmbrellaService;
import io.renren.zin.umbrella.dto.TMoneyAccountAdd;
import io.renren.zin.umbrella.dto.TMoneyAccountAddResponse;
import io.renren.zin.umbrella.dto.TMoneyAccountQuery;
import io.renren.zin.umbrella.dto.TMoneyAccountQueryResponse;
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
 * 来账账户管理
 *
 * @author epiphyllum epiphyllum.zhou@gmail.com
 * @since 3.0 2024-08-19
 */
@RestController
@RequestMapping("zorg/jmaccount")
@Tag(name = "j_maccount")
public class JMaccountController {
    @Resource
    private JMaccountService jMaccountService;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private SysDeptDao sysDeptDao;
    @Resource
    private ZinAccountManageService zinAccountManageService;
    @Resource
    private ZinUmbrellaService zinUmbrellaService;
    @Resource
    private JMaccountDao jMaccountDao;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)")
    })
    @PreAuthorize("hasAuthority('zorg:jmaccount:page')")
    public Result<PageData<JMaccountDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<JMaccountDTO> page = jMaccountService.page(params);
        return new Result<PageData<JMaccountDTO>>().ok(page);
    }

    @GetMapping("list")
    @Operation(summary = "list")
//    @PreAuthorize("hasAuthority('zorg:jmaccount:page')")
    public Result<List<JMaccountDTO>> list(@RequestParam(value = "merchantId", required = false) Long merchantId) {
        Result<List<JMaccountDTO>> result = new Result<>();
        List<JMaccountEntity> entities = jMaccountDao.selectList(Wrappers.<JMaccountEntity>lambdaQuery()
                .eq(merchantId != null, JMaccountEntity::getMerchantId, merchantId)
        );
        List<JMaccountDTO> dtos = ConvertUtils.sourceToTarget(entities, JMaccountDTO.class);
        result.setData(dtos);
        return result;
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('zorg:jmaccount:info')")
    public Result<JMaccountDTO> get(@PathVariable("id") Long id) {
        JMaccountDTO data = jMaccountService.get(id);
        return new Result<JMaccountDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("保存")
    @PreAuthorize("hasAuthority('zorg:jmaccount:save')")
    public Result save(@RequestBody JMaccountDTO dto) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") &&
                !user.getUserType().equals("agent") &&
                !user.getUserType().equals("merchant")
        ) {
            return Result.fail(9999, "not authorized, you are " + user.getUserType());
        }

        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        if (user.getUserType().equals("merchant")) {
            SysDeptEntity merchantDept = sysDeptDao.selectById(user.getDeptId());
            SysDeptEntity agentDept = sysDeptDao.selectById(merchantDept.getPid());
            dto.setAgentId(agentDept.getId());
            dto.setAgentName(agentDept.getName());
            dto.setMerchantId(merchantDept.getId());
            dto.setMerchantName(merchantDept.getName());
        } else if (user.getUserType().equals("agent")) {
            SysDeptEntity agentDept = sysDeptDao.selectById(user.getDeptId());
            dto.setAgentId(agentDept.getId());
            dto.setAgentName(agentDept.getName());
        } else if (user.getUserType().equals("operation")) {
        }
        dto.setApi(0);
        dto.setMeraplid(CommonUtils.uniqueId());
        jMaccountService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("修改")
    @PreAuthorize("hasAuthority('zorg:jmaccount:update')")
    public Result update(@RequestBody JMaccountDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        jMaccountService.update(dto);
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("删除")
    @PreAuthorize("hasAuthority('zorg:jmaccount:delete')")
    public Result delete(@RequestBody Long[] ids) {
        UserDetail user = SecurityUser.getUser();
        if (!user.getUserType().equals("operation") && !user.getUserType().equals("agent")) {
            return Result.fail(9999, "not authorized");
        }
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        jMaccountService.delete(ids);
        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("导出")
    @PreAuthorize("hasAuthority('zorg:jmaccount:export')")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<JMaccountDTO> list = jMaccountService.list(params);
        ExcelUtils.exportExcelToTarget(response, null, "j_maccount", list, JMaccountExcel.class);
    }

    // 提交通联
    @GetMapping("submit")
    @PreAuthorize("hasAuthority('zorg:jmaccount:submit')")
    public Result submit(@RequestParam("id") Long id) throws Exception {

        JMaccountEntity jMaccountEntity = jMaccountDao.selectById(id);
        TMoneyAccountAdd tMoneyAccountAdd = ConvertUtils.sourceToTarget(jMaccountEntity, TMoneyAccountAdd.class);
        TMoneyAccountAddResponse response = zinUmbrellaService.addMoneyAccount(tMoneyAccountAdd);
        String cardId = response.getId();
        JMaccountEntity update = new JMaccountEntity();
        update.setId(id);
        update.setCardid(cardId);
        jMaccountDao.updateById(update);

        return new Result();
    }

    // 查询通联
    @GetMapping("query")
    @PreAuthorize("hasAuthority('zorg:jmaccount:query')")
    public Result query(@RequestParam("id") Long id) throws Exception {
        JMaccountEntity jMaccountEntity = jMaccountDao.selectById(id);
        TMoneyAccountQuery query = ConvertUtils.sourceToTarget(jMaccountEntity, TMoneyAccountQuery.class);
        query.setId(jMaccountEntity.getCardid());
        query.setCurrency(null);
        TMoneyAccountQueryResponse response = zinUmbrellaService.queryMoneyAccount(query);
        JMaccountEntity update = new JMaccountEntity();
        update.setId(id);
        update.setState(response.getState());
        jMaccountDao.updateById(update);
        return new Result();
    }
}