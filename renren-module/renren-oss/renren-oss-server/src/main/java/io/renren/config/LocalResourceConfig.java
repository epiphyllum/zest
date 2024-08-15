package io.renren.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 本地资源文件映射配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class LocalResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问前缀，需要和前端上传配置的路径一致
        String localPrefix = "upload";
        // 上传目录，需要和前端上传配置的路径一致
        String localPath = "/work/data";

        registry.addResourceHandler("/" + localPrefix + "/**")
                .addResourceLocations("file:" + localPath + "/" + localPrefix + "/");
    }
}
