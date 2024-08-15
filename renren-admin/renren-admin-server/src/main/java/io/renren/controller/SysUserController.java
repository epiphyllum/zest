/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import io.renren.commons.log.annotation.LogOperation;
import io.renren.commons.security.user.SecurityUser;
import io.renren.commons.security.user.UserDetail;
import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.exception.ErrorCode;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.ExcelUtils;
import io.renren.commons.tools.utils.Result;
import io.renren.commons.tools.validator.AssertUtils;
import io.renren.commons.tools.validator.ValidatorUtils;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.renren.dto.PasswordDTO;
import io.renren.dto.SysUserDTO;
import io.renren.excel.SysUserExcel;
import io.renren.service.SysRoleUserService;
import io.renren.service.SysUserDetailService;
import io.renren.service.SysUserPostService;
import io.renren.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestController
@RequestMapping("user")
@Tag(name = "用户管理")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserDetailService sysUserDetailService;
    @Resource
    private SysRoleUserService sysRoleUserService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private SysUserPostService sysUserPostService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
            @Parameter(name = Constant.ORDER_FIELD, description = "排序字段"),
            @Parameter(name = Constant.ORDER, description = "排序方式，可选值(asc、desc)"),
            @Parameter(name = "username", description = "用户名")
    })
    @PreAuthorize("hasAuthority('sys:user:page')")
    public Result<PageData<SysUserDTO>> page(@Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        PageData<SysUserDTO> page = sysUserService.page(params);

        return new Result<PageData<SysUserDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public Result<SysUserDTO> get(@PathVariable("id") Long id) {
        SysUserDTO data = sysUserService.get(id);

        //用户角色列表
        List<Long> roleIdList = sysRoleUserService.getRoleIdList(id);
        data.setRoleIdList(roleIdList);

        //用户岗位列表
        List<Long> postIdList = sysUserPostService.getPostIdList(id);
        data.setPostIdList(postIdList);

        return new Result<SysUserDTO>().ok(data);
    }

    @GetMapping("info")
    @Operation(summary = "登录用户信息")
    public Result<SysUserDTO> info() {
        SysUserDTO data = sysUserService.get(SecurityUser.getUserId());
        return new Result<SysUserDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存")
    @LogOperation("Save User")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result save(@RequestBody SysUserDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysUserService.save(dto);

        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改")
    @LogOperation("Update User")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@RequestBody SysUserDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysUserService.update(dto);

        return new Result();
    }

    @PutMapping("app")
    @Operation(summary = "修改用户信息")
    @LogOperation("Update User")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result updateUserInfo(@RequestBody SysUserDTO dto) {
        sysUserService.updateUserInfo(dto);

        return new Result();
    }

    @PutMapping("password")
    @Operation(summary = "修改密码")
    @LogOperation("Password User")
    public Result password(@RequestBody PasswordDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto);

        UserDetail user = SecurityUser.getUser();

        //原密码不正确
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new Result().error(ErrorCode.PASSWORD_ERROR);
        }

        sysUserService.updatePassword(user.getId(), dto.getNewPassword());

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @LogOperation("Delete User")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysUserService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @Operation(summary = "导出")
    @LogOperation("Export User")
    @PreAuthorize("hasAuthority('sys:user:export')")
    @Parameter(name = "username", description = "用户名")
    public void export(@Parameter(hidden = true) @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysUserDTO> list = sysUserService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, "用户管理", list, SysUserExcel.class);
    }

    /**
     * 根据用户Id，获取用户信息
     */
    @GetMapping("getById")
    public Result<UserDetail> getById(Long id) {
        UserDetail userDetail = sysUserDetailService.getUserDetailById(id);

        return new Result<UserDetail>().ok(userDetail);
    }

    /**
     * 根据用户ID,查询用户姓名列表
     */
    @PostMapping("getRealNameList")
    public Result<List<String>> getRealNameList(@RequestBody List<Long> ids) {
        List<String> realNameList = sysUserService.getRealNameList(ids);

        return new Result<List<String>>().ok(realNameList);
    }

    /**
     * 根据角色ID,查询用户ID列表
     */
    @PostMapping("getUserIdListByRoleIdList")
    public Result<List<Long>> getUserIdListByRoleIdList(@RequestBody List<Long> ids) {
        List<Long> userIdList = sysUserService.getUserIdListByRoleIdList(ids);

        return new Result<List<Long>>().ok(userIdList);
    }

    /**
     * 根据角色ID,查询角色名称列表
     */
    @PostMapping("getRoleNameList")
    public Result<List<String>> getRoleNameList(@RequestBody List<Long> ids) {
        List<String> userIdList = sysUserService.getRoleNameList(ids);

        return new Result<List<String>>().ok(userIdList);
    }

    /**
     * 根据岗位ID,查询用户ID列表
     */
    @PostMapping("getUserIdListByPostIdList")
    public Result<List<Long>> getUserIdListByPostIdList(@RequestBody List<Long> ids) {
        List<Long> userIdList = sysUserService.getUserIdListByPostIdList(ids);

        return new Result<List<Long>>().ok(userIdList);
    }

    /**
     * 根据部门ID,查询部门领导列表
     */
    @PostMapping("getLeaderIdListByDeptIdList")
    public Result<List<Long>> getLeaderIdListByDeptIdList(@RequestBody List<Long> ids) {
        List<Long> userIdList = sysUserService.getLeaderIdListByDeptIdList(ids);

        return new Result<List<Long>>().ok(userIdList);
    }

    /**
     * 根据用户ID,查询部门领导ID
     */
    @PostMapping("getLeaderIdListByUserId")
    public Result<Long> getLeaderIdListByUserId(Long userId) {
        Long leaderId = sysUserService.getLeaderIdListByUserId(userId);

        return new Result<Long>().ok(leaderId);
    }

}