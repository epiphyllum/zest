/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.exception;

import io.renren.commons.tools.exception.ErrorCode;

/**
 * 模块错误编码，由9位数字组成，前6位为模块编码，后3位为业务编码
 * <p>
 * 如：100001（100001代表模块，001代表业务代码）
 * </p>
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public interface ModuleErrorCode extends ErrorCode {

    int OSS_UPLOAD_FILE_ERROR = 100001001;
    int UPLOAD_FILE_EMPTY = 100001002;
}