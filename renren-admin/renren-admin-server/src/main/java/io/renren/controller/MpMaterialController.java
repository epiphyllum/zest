package io.renren.controller;

import io.renren.commons.tools.constant.Constant;
import io.renren.commons.tools.page.PageData;
import io.renren.commons.tools.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNewsBatchGetResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公众号素材管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@AllArgsConstructor
@RestController
@RequestMapping("mp/material")
@Tag(name = "公众号素材管理")
public class MpMaterialController {
    private final WxMpService wxService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @Parameters({
            @Parameter(name = "appId", description = "appId", required = true),
            @Parameter(name = "type", description = "可选值：news、voice、image、video", required = true),
            @Parameter(name = "offset", description = "起始位置"),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数")
    })
    public Result page(String appId, String type, int offset, int limit) throws Exception {
        if (!this.wxService.switchover(appId)) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }

        //素材服务
        WxMpMaterialService materialService = wxService.getMaterialService();

        if (WxConsts.MaterialType.NEWS.equals(type)) {
            WxMpMaterialNewsBatchGetResult result = materialService.materialNewsBatchGet(offset, limit);

            return new Result<>().ok(new PageData<>(result.getItems(), result.getTotalCount()));
        } else {
            WxMpMaterialFileBatchGetResult result = materialService.materialFileBatchGet(type, offset, limit);

            return new Result<>().ok(new PageData<>(result.getItems(), result.getTotalCount()));
        }

    }

    @GetMapping("get")
    @Operation(summary = "获取永久素材")
    @Parameters({
            @Parameter(name = "appId", description = "appId", required = true),
            @Parameter(name = "mediaId", description = "素材ID", required = true)
    })
    public Result get(String appId, String mediaId) throws Exception {
        if (!this.wxService.switchover(appId)) {
            throw new IllegalArgumentException(String.format("未找到对应appId=[%s]的配置，请核实！", appId));
        }


        //获取永久素材
        WxMpMaterialNews data = wxService.getMaterialService().materialNewsInfo(mediaId);

        return new Result().ok(data);
    }
}
