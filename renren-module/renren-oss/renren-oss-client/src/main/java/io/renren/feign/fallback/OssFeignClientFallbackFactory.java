/**
 * Copyright (c) 2016-2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.feign.fallback;

import io.renren.commons.tools.utils.Result;
import io.renren.dto.UploadDTO;
import io.renren.feign.OssFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * OSS FallbackFactory
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@Component
public class OssFeignClientFallbackFactory implements FallbackFactory<OssFeignClient> {
    @Override
    public OssFeignClient create(Throwable throwable) {
        log.error("{}", throwable);

        return file -> new Result<UploadDTO>().error();
    }
}
