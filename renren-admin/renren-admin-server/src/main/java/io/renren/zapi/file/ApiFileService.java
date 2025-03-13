package io.renren.zapi.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.Result;
import io.renren.zadmin.dao.JAuthedDao;
import io.renren.zadmin.dao.JMerchantDao;
import io.renren.zadmin.entity.JAuthedEntity;
import io.renren.zadmin.entity.JMerchantEntity;
import io.renren.zapi.ApiContext;
import io.renren.zapi.ApiService;
import io.renren.zapi.file.dto.DownloadSettleReq;
import io.renren.zapi.file.dto.DownloadSettleRes;
import io.renren.zapi.file.dto.UploadRes;
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
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ApiFileService {
    @Resource
    private ZestConfig zestConfig;
    @Resource
    private ApiService apiService;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private ZinFileService zinFileService;
    @Resource
    private JAuthedDao jAuthedDao;

    // 文件上传
    public Result<UploadRes> upload(Long merchantId, String reqId, String body, String sign, String suffix) {
        JMerchantEntity merchant = jMerchantDao.selectById(merchantId);

        // 不是开发环境, 才验证签名
        if (!zestConfig.isDev()) {
            String bodyDigest = DigestUtil.sha256Hex(body);
            String toSign = bodyDigest + reqId + merchantId;
            Sign merchantVerifier = apiService.getMerchantVerifier(merchant);
            byte[] bytes = DigestUtil.sha256(toSign);
            if (merchantVerifier.verify(bytes, sign.getBytes())) {
                throw new RenException("signature verification failed");
            }
        }

        // 文件内容
        byte[] bodyBytes = Base64.getUrlDecoder().decode(body);

        // $UPLOAD_DIR/yyyy/mm/dd/yyyy-mm-dd-xxxxxxxxxxxxxxxxxxx.png
        Date now = new Date();
        String dirPart = String.format("%04d/%02d/%02d", DateUtil.year(now), DateUtil.month(now), DateUtil.dayOfMonth(now));
        String filenamePrefix = dirPart.replaceAll("/", "-") + "-";

        // 最终存储目录
        String wholeDir = zestConfig.getUploadDir() + "/" + dirPart;

        // 文件名
        String filename = filenamePrefix + UUID.randomUUID().toString().replace("-", "") + "." + suffix;

        try {
            // 创建上传目录
            Files.createDirectories(Paths.get(wholeDir));
            String filePath = wholeDir + "/" + filename;
            // 保存文件
            FileUtils.writeByteArrayToFile(new File(filePath), bodyBytes);
        } catch (IOException e) {
            throw new RenException("save file error");
        }

        // 上传通联
        zinFileService.upload(filename);

        // 应答
        Result<UploadRes> result = new Result<>();
        UploadRes uploadRes = new UploadRes(filename);
        uploadRes.setFid(filename);
        ;
        result.setData(uploadRes);
        return result;
    }

    // 下载结算文件
    public Result<DownloadSettleRes> downloadSettle(DownloadSettleReq request, ApiContext context) {
        String entrydate = request.getEntrydate();
        List<JAuthedEntity> jAuthedEntities = jAuthedDao.selectList(Wrappers.<JAuthedEntity>lambdaQuery()
                .eq(JAuthedEntity::getMerchantId, context.getMerchant().getId())
                .eq(JAuthedEntity::getEntrydate, entrydate)
        );

        return null;
    }
}
