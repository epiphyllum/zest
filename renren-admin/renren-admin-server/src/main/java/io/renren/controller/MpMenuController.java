package io.renren.controller;

import io.renren.commons.tools.utils.Result;
import io.renren.dto.MpMenuDTO;
import io.renren.service.MpMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 公众号自定义菜单
 *
 * @author Mark sunlightcs@gmail.com
 */
@AllArgsConstructor
@RestController
@RequestMapping("mp/menu/{appId}")
@Tag(name = "公众号自定义菜单")
public class MpMenuController {
    private final MpMenuService mpMenuService;
    private final WxMpService wxService;

    @GetMapping
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('mp:menu:all')")
    public Result<MpMenuDTO> get(@PathVariable("appId") String appId) {
        MpMenuDTO data = mpMenuService.getByAppId(appId);

        return new Result<MpMenuDTO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "发布到微信")
    @PreAuthorize("hasAuthority('mp:menu:all')")
    public Result push(@PathVariable("appId") String appId, @RequestBody MpMenuDTO dto) {
        MpMenuDTO data = mpMenuService.getByAppId(appId);
        if (data == null) {
            mpMenuService.save(dto);
        } else {
            dto.setId(data.getId());
            mpMenuService.update(dto);
        }

        if (!this.wxService.switchover(appId)) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }

        //发布到微信
        try {
            wxService.getMenuService().menuCreate(data.getMenu());
        } catch (WxErrorException e) {
            return new Result().error(e.getMessage());
        }

        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除微信菜单")
    @PreAuthorize("hasAuthority('mp:menu:all')")
    public Result delete(@PathVariable("appId") String appId) {
        if (!this.wxService.switchover(appId)) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }

        //删除微信菜单
        try {
            wxService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            return new Result().error(e.getMessage());
        }

        //删除系统菜单
        mpMenuService.deleteByAppId(appId);

        return new Result();
    }

}