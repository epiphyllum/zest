package io.renren.zin.file;

import io.renren.commons.tools.exception.RenException;
import io.renren.zcommon.CommonUtils;
import io.renren.zcommon.ZestConfig;
import io.renren.zin.ZinRequester;
import io.renren.zin.file.dto.DownloadVpaRequest;
import io.renren.zin.file.dto.VpaInfoItem;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.renren.zcommon.CommonUtils.uniqueId;

@Service
@Slf4j
public class ZinFileService {
    @Resource
    private ZinRequester requester;
    @Resource
    private ZestConfig zestConfig;

    /**
     * 7000-文件上传
     * 文件名格式: yyyy-mm-dd-12312312312313.pdf
     */
    public String upload(String fid) {

        String reqId = CommonUtils.uniqueId();
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
    public List<VpaInfoItem> downloadVapInfoAes(DownloadVpaRequest request) {
        byte[] download = requester.download(uniqueId(), "/gcpapi/file/downvapinfoaes", request);
        String raw = CommonUtils.decryptSensitiveBytes(download, zestConfig.getAccessConfig().getSensitiveKey());
        log.info("vpa子卡信息下载数据: {}", raw);
        String[] split = raw.split("\n");
        List<VpaInfoItem> items  = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {

            String line = split[i].replaceAll("\"", "");
            String[] fields = line.split(",");
            VpaInfoItem item = new VpaInfoItem(fields[0], fields[1], fields[2]);
            log.info("vpa item: {}", item);
            items.add(item);
        }
        return items;
    }
}
