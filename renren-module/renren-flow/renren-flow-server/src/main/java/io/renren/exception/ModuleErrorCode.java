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
 * 如：100001001（100001代表模块，001代表业务代码）
 * </p>
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ModuleErrorCode extends ErrorCode {
    int ACT_DEPLOY_ERROR = 100004001;
    int ACT_MODEL_IMG_ERROR = 100004002;
    int ACT_MODEL_EXPORT_ERROR = 100004003;
    int UPLOAD_FILE_EMPTY = 100004004;
    int ACT_DEPLOY_FORMAT_ERROR = 100004005;
    int TASK_CLIME_FAIL = 100004006;
    int NONE_EXIST_PROCESS = 100004007;
    int SUPERIOR_NOT_EXIST = 100004008;
    int REJECT_MESSAGE = 100004009;
    int ROLLBACK_MESSAGE = 100004010;
    int UNCLAIM_ERROR_MESSAGE = 100004011;
    int PROCESS_START_ERROR = 100004012;
    int REJECT_PROCESS_PARALLEL_ERROR = 100004013;
    int REJECT_PROCESS_HANDLEING_ERROR = 100004014;
    int END_PROCESS_PARALLEL_ERROR = 100004015;
    int END_PROCESS_HANDLEING_ERROR = 100004016;
    int END_PROCESS_MESSAGE = 100004017;
    int BACK_PROCESS_PARALLEL_ERROR = 100004018;
    int BACK_PROCESS_HANDLEING_ERROR = 100004019;
}