package io.renren.zdashboard;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.JBalanceEntity;
import io.renren.zadmin.entity.JStatEntity;
import io.renren.zdashboard.dto.PrepaidCardStat;
import io.renren.zdashboard.dto.StatItem;
import io.renren.zdashboard.dto.SubDashboardDTO;
import io.renren.zmanager.JCardManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JSubDashboard {
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JCardDao jCardDao;
    @Resource
    private JCardManager jCardManager;
    @Resource
    private JStatDao jStatDao;

    private StatItem balanceStatItem(List<JBalanceEntity> items) {
        return null;
    }

    private Map<String, StatItem> balanceSummaryMap(Long subId) {
        Map<String, StatItem> map = new HashMap<>();
        jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                        .eq(JBalanceEntity::getOwnerId, subId)
                ).stream()
                .collect(Collectors.groupingBy(JBalanceEntity::getCurrency))
                .forEach((currency, items) -> {
                    map.put(currency, balanceStatItem(items));
                });
        return map;
    }

    // 今日日统计
    private Map<String, StatItem> todayMap(Long subId) {
        return null;
    }

    // 过去30天统计
    private Map<String, List<StatItem>> monthMap(Long subId) {
        Date beginDate = new Date();

        jStatDao.selectList(Wrappers.<JStatEntity>lambdaQuery()
                        .gt(JStatEntity::getStatDate, beginDate)
                ).stream()
                .collect(Collectors.groupingBy(JStatEntity::getCurrency))
                .forEach((currency, items) -> {
                    for (JStatEntity item : items) {
                    }
                });
        return null;
    }

    public List<SubDashboardDTO> dashboard(Long subId) {

        // 账户情况
        Map<String, StatItem> balanceSummaryMap = balanceSummaryMap(subId);

        // 当日情况
        StatItem todayStat;

        // 当月情况
        StatItem monthStat;

        // 预付费卡情况
        List<PrepaidCardStat> prepaidCardStatList;

        return null;
    }
}