/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.ureport.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import io.renren.dao.UReportDataDao;
import io.renren.entity.UReportDataEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UReport Provider
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class RenUReportProvider implements ReportProvider {
    private final static String NAME = "renren-provider";
    private final static String PREFIX = "renren-";

    @Resource
    private UReportDataDao ureportDataDao;

    @Override
    public InputStream loadReport(String file) {
        UReportDataEntity entity = ureportDataDao.selectOne(this.getWrapper(file));
        if (entity == null) {
            return null;
        }

        return new ByteArrayInputStream(entity.getContent());
    }

    @Override
    public void deleteReport(String file) {
        ureportDataDao.delete(this.getWrapper(file));
    }

    @Override
    public List<ReportFile> getReportFiles() {
        List<UReportDataEntity> list = ureportDataDao.selectList(null);
        List<ReportFile> reportList = list.stream().map(item -> new ReportFile(item.getFileName(), item.getUpdateDate()))
                .collect(Collectors.toList());
        return reportList;
    }

    @Override
    public void saveReport(String file, String content) {
        UReportDataEntity entity = ureportDataDao.selectOne(this.getWrapper(file));
        if (entity == null) {
            entity = new UReportDataEntity();
            entity.setFileName(this.getFileName(file));
            entity.setContent(content.getBytes());
            ureportDataDao.insert(entity);
        } else {
            entity.setContent(content.getBytes());
            ureportDataDao.updateById(entity);
        }
    }

    private QueryWrapper<UReportDataEntity> getWrapper(String file) {
        QueryWrapper<UReportDataEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name", this.getFileName(file));

        return wrapper;
    }

    private String getFileName(String name) {
        if (name.startsWith(PREFIX)) {
            name = name.substring(PREFIX.length(), name.length());
        }
        return name;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean disabled() {
        return false;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }


}
