/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.utils;

import cn.hutool.core.map.MapUtil;
import freemarker.template.Template;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.JsonUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
public class GenUtils {

    public static List<TemplateContent> getTemplateList() {
        // 模板路径
        String template = "/template/";

        // 模板配置文件
        InputStream isConfig = GenUtils.class.getResourceAsStream(template + "config.json");
        if (isConfig == null) {
            throw new RenException("模板配置文件，config.json不存在");
        }

        try {
            // 读取模板配置文件
            String configContent = StreamUtils.copyToString(isConfig, StandardCharsets.UTF_8);

            List<TemplateContent> templateList = JsonUtils.parseArray(configContent, TemplateContent.class);
            for (TemplateContent templateContent : templateList) {
                // 模板文件
                InputStream isTemplate = GenUtils.class.getResourceAsStream(template + templateContent.getName());
                if (isTemplate == null) {
                    throw new RenException("模板文件 " + templateContent.getName() + " 不存在");
                }
                // 读取模板内容
                String content = StreamUtils.copyToString(isTemplate, StandardCharsets.UTF_8);

                templateContent.setContent(content);
            }

            return templateList;
        } catch (IOException e) {
            throw new RenException("config.json配置文件失败");
        }
    }

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }


    /**
     * 获取模板渲染后的内容
     *
     * @param content   模板内容
     * @param dataModel 数据模型
     */
    public static String getTemplateContent(String content, Map<String, Object> dataModel) {
        if (MapUtil.isEmpty(dataModel)) {
            return content;
        }

        StringReader reader = new StringReader(content);
        StringWriter sw = new StringWriter();
        try {
            //渲染模板
            String templateName = dataModel.getOrDefault("templateName", "generator").toString();
            Template template = new Template(templateName, reader, null, "utf-8");
            template.process(dataModel, sw);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RenException("渲染模板失败，请检查模板语法", e);
        }

        content = sw.toString();

        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(sw);

        return content;
    }
}