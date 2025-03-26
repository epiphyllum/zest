package io.renren.zapi.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.digest.DigestUtil;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JAuthedDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.file.dto.DownloadSettleReq;
import io.renren.zapi.file.dto.UploadRes;
import io.renren.zcommon.ByteUtil;
import io.renren.zcommon.ZestConfig;
import io.renren.zin.file.ZinFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

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
            throw new RenException("结算文件不能存在:" + entrydate);
        }
        try {
            String content = FileUtils.readFileToString(file);
            Result<String> result = new Result<>();
            result.setData(content);
            return result;
        } catch (IOException e) {
            throw new RenException("结算文件不能存在:" + entrydate);
        }
    }

}
