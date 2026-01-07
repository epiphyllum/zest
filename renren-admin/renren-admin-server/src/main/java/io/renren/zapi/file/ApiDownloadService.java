package io.renren.zapi.file;

import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zapi.ApiContext;
import io.renren.zapi.file.dto.DownloadSettleReq;
import io.renren.zapi.file.dto.DownloadAuthReq;
import io.renren.zcommon.ZestConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class ApiDownloadService {
    @Resource
    private ZestConfig zestConfig;

    // 下载结算文件
    public Result<String> downloadSettle(DownloadSettleReq request, ApiContext context) {
        String entrydate = request.getEntrydate();
        String filename = zestConfig.getUploadDir() + "/settle/" + context.getMerchant().getId() + "/" + entrydate + ".txt";
        File file = new File(filename);
        if (!file.exists()) {
            log.error("结算文件不存在:{}", filename);
            throw new RenException("结算文件不存在:" + entrydate);
        }
        try {
            String content = FileUtils.readFileToString(file);
            Result<String> result = new Result<>();
            result.setData(content);
            return result;
        } catch (IOException e) {
            throw new RenException("结算文件不存在:" + entrydate);
        }
    }

    // 下载结算文件
    public Result<String> downloadAuth(DownloadAuthReq request, ApiContext context) {
        String entrydate = request.getEntrydate();
        String filename = zestConfig.getUploadDir() + "/auth/" + context.getMerchant().getId() + "/" + entrydate + ".txt";
        File file = new File(filename);
        if (!file.exists()) {
            log.error("结算文件不存在:{}", filename);
            throw new RenException("结算文件不存在:" + entrydate);
        }
        try {
            String content = FileUtils.readFileToString(file);
            Result<String> result = new Result<>();
            result.setData(content);
            return result;
        } catch (IOException e) {
            throw new RenException("结算文件不存在:" + entrydate);
        }
    }

}
