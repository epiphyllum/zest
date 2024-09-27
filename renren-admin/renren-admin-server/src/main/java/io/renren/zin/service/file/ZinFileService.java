package io.renren.zin.service.file;

import io.renren.commons.tools.exception.RenException;
import io.renren.zin.config.CommonUtils;
import io.renren.zin.config.ZestConfig;
import io.renren.zin.config.ZinRequester;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ZinFileService {
    @Resource
    private ZinRequester requester;
    @Resource
    private ZestConfig zestConfig;

    /**
     * 7000-文件上传
     *
     * @param fid
     * @return
     */
    public String upload(String fid) {
        String reqId = CommonUtils.newRequestId();

        // yyyy-mm-dd-12312312312313.pdf
        String[] split = fid.split("-");
        String dateDir = String.format("%s/%s/%s", split[0], split[1], split[2]);
        String filename = zestConfig.getUploadDir() + "/" + dateDir + "/" + fid;
        byte[] bytes;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filename));
        } catch (IOException e) {
            throw new RenException("file not saved correctly");
        }
        requester.upload(reqId, bytes, fid);
        return fid;
    }

    /**
     * 7001-文件上传查询
     * /gcpapi/file/upquery 方向：合作方->通联
     * 说明：该接口为上传中断后，可以查询上次传输的文件大小。达到续传目的。
     */


    /**
     * 7002-对账单下载
     * /gcpapi/file/downdailyreport  方向：合作方->通联
     * 说明： 下载商户当天的对账单信息。
     */
    public void downloadDailyReport() {
    }


    /**
     * 7004-VPA子卡信息下载(deprecated)
     */
    public void downloadVpaInfo() {

    }

    /**
     * 7005-VPA子卡信息下载(加密文件)
     */
    public void downloadVapInfoAes() {
    }
}
