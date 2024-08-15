/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.renren.commons.tools.utils.DateUtils;
import io.renren.commons.tools.utils.TreeNode;
import io.renren.commons.tools.validator.group.AddGroup;
import io.renren.commons.tools.validator.group.DefaultGroup;
import io.renren.commons.tools.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Schema(description = "菜单管理")
public class SysMenuDTO extends TreeNode<SysMenuDTO> implements Serializable {
    private static final long serialVersionUID = 1L;
 
    @Schema(description = "id")
    @Null(message = "{id.null}", groups = AddGroup.class)
    @NotNull(message = "{id.require}", groups = UpdateGroup.class)
    private Long id;

    @Schema(description = "上级ID")
    @NotNull(message = "{sysmenu.pid.require}", groups = DefaultGroup.class)
    private Long pid;

    @Schema(description = "菜单名称")
    @NotBlank(message = "{sysmenu.name.require}", groups = DefaultGroup.class)
    private String name;

    @Schema(description = "菜单URL")
    private String url;

    @Schema(description = "类型  0：菜单   1：按钮")
    @Range(min = 0, max = 1, message = "{sysmenu.type.range}", groups = DefaultGroup.class)
    private Integer menuType;

    @Schema(description = "打开方式   0：内部   1：外部")
    @Range(min = 0, max = 1, message = "{sysmenu.openstyle.range}", groups = DefaultGroup.class)
    private Integer openStyle;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "权限标识，如：sys:menu:save")
    private String permissions;

    @Schema(description = "排序")
    @Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
    private Integer sort;

    @Schema(description = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date createDate;

    @Schema(description = "上级菜单名称")
    private String parentName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getOpenStyle() {
        return openStyle;
    }

    public void setOpenStyle(Integer openStyle) {
        this.openStyle = openStyle;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getPid() {
        return pid;
    }

    @Override
    public void setPid(Long pid) {
        this.pid = pid;
    }
}
