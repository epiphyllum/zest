package io.renren.zdashboard;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zdashboard.dto.BalanceItem;
import io.renren.zdashboard.dto.OrgDashboardDTO;
import io.renren.zdashboard.dto.StatItem;
import io.renren.zmanager.JVaManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JOrgDashboard {
    @Resource
    private JVaDao jVaDao;
    @Resource
    private JVaManager jVaManager;
    @Resource
    private JBalanceDao jBalanceDao;
    @Resource
    private JSubDao jSubDao;
    @Resource
    private JMerchantDao jMerchantDao;
    @Resource
    private JStatDao jStatDao;
    @Resource
    private VDepositDao vDepositDao;
    @Resource
    private VWithdrawDao vWithdrawDao;
    @Resource
    private VCardDao vCardDao;
    @Resource
    private JAuthDao jAuthDao;

    // 某个币种当前余额情况
    private BalanceItem balanceStatItem(List<JBalanceEntity> items, String currency) {
        BalanceItem item = new BalanceItem();
        item.setCurrency(currency);

        BigDecimal va = null;
        BigDecimal subVa = null;

        for (JBalanceEntity balanceEntity : items) {
            // 收入类
            if (balanceEntity.getBalanceType().startsWith("CHARGE_")) {
                item.setCharge(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("DEPOSIT_")) {
                item.setDeposit(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("CARD_FEE_")) {
                item.setCardFee(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("CARD_SUM_")) {
                item.setCardSum(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("SUB_VA_")) {
                subVa = balanceEntity.getBalance();
            } else if (balanceEntity.getBalanceType().startsWith("VA_")) {
                va = balanceEntity.getBalance();
            }
            // 成本类
            else if (balanceEntity.getBalanceType().startsWith("AIP_CARD_SUM_")) {
                item.setAipCardSum(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("AIP_DEPOSIT_")) {
                item.setAipDeposit(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("AIP_CHARGE_")) {
                item.setAipCharge(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("AIP_CARD_FEE_")) {
                item.setAipCardFee(balanceEntity.getBalance());
            }
        }
        item.setBalance(va.add(subVa));
        return item;
    }

    // 当前余额
    private Map<String, BalanceItem> balanceMap() {
        Map<String, BalanceItem> map = new HashMap<>();
        jBalanceDao.selectBalance()
                .stream()
                .collect(Collectors.groupingBy(JBalanceEntity::getCurrency))
                .forEach((currency, items) -> {
                    log.info("currency: {}", currency);
                    for (JBalanceEntity item : items) {
                        log.info("item: {}", item);
                    }
                    BalanceItem item = balanceStatItem(items, currency);
                    map.put(currency, item);
                    log.info("balanceMap: {} -> {}", currency, item);
                });
        return map;
    }

    // 今日日统计
    private Map<String, StatItem> todayMap(Date today) {
        Map<String, List<VDepositEntity>> depositMap = vDepositDao.selectDayOfOperation(today)
                .stream().collect(Collectors.groupingBy(VDepositEntity::getCurrency));
        Map<String, List<VWithdrawEntity>> withdrawMap = vWithdrawDao.selectDayOfOperation(today)
                .stream().collect(Collectors.groupingBy(VWithdrawEntity::getCurrency));
        Map<String, List<VCardEntity>> cardMap = vCardDao.selectDayOfOperation(today)
                .stream().collect(Collectors.groupingBy(VCardEntity::getCurrency));
        Map<String, List<JAuthEntity>> authMap = jAuthDao.selectDayOfOperation(today)
                .stream().collect(Collectors.groupingBy(JAuthEntity::getCurrency));

        Set<String> currencySet = new HashSet<>();
        currencySet.addAll(depositMap.keySet());
        currencySet.addAll(withdrawMap.keySet());
        currencySet.addAll(cardMap.keySet());

        Map<String, StatItem> map = new HashMap<>();
        for (String currency : currencySet) {
            log.info("todayMap-准备币种: {}", currency);
            StatItem item = new StatItem();

            // 充值
            List<VDepositEntity> vDepositEntities = depositMap.get(currency);
            if (vDepositEntities != null && vDepositEntities.size() > 0) {
                VDepositEntity depositEntity = vDepositEntities.get(0);
                log.info("充值记录: {}", depositEntity);
                item.setCardSum(depositEntity.getCardSum());
                item.setCharge(depositEntity.getCharge());
                item.setDeposit(depositEntity.getDeposit());

                item.setAipDeposit(depositEntity.getAipDeposit());
                item.setAipCharge(depositEntity.getAipCharge());
                item.setAipCardSum(depositEntity.getAipCardSum());
            }

            // 提现
            List<VWithdrawEntity> vWithdrawEntities = withdrawMap.get(currency);
            if (vWithdrawEntities != null && vWithdrawEntities.size() > 0) {
                VWithdrawEntity withdrawEntity = vWithdrawEntities.get(0);
                log.info("提现记录: {}", withdrawEntity);
                item.setWithdraw(withdrawEntity.getCardSum());
                item.setWithdrawCharge(withdrawEntity.getCharge());

                item.setAipWithdrawCharge(withdrawEntity.getAipCharge());
            }

            // 开卡数据
            List<VCardEntity> vCardEntities = cardMap.get(currency);
            if (vCardEntities != null && vCardEntities.size() > 0) {
                VCardEntity vCardEntity = vCardEntities.get(0);
                log.info("开卡记录: {}", vCardEntity);
                item.setTotalCard(vCardEntity.getTotalCard());
                item.setCardFee(vCardEntity.getMerchantfee());
                item.setAipCardSum(vCardEntity.getFee());
            }

            // 消费交易数据
            List<JAuthEntity> authEntities = authMap.get(currency);
            if (authEntities != null && authEntities.size() > 0) {
                JAuthEntity jAuthEntity = authEntities.get(0);
                log.info("消费记录: {}", jAuthEntity);
                item.setSettlecount(jAuthEntity.getId());
                item.setSettleamount(jAuthEntity.getSettleamount());
            }
            item.setStatDate(today);
            item.setCurrency(currency);
            map.put(currency, item);
        }
        return map;
    }

    // 过去30天统计
    private Map<String, List<StatItem>> monthMap(Date today) {
        Calendar calendar = Calendar.getInstance(); // 获取当前日期的Calendar实例
        calendar.add(Calendar.DAY_OF_MONTH, -30); // 向当前日期减去30天
        Date beginDate = calendar.getTime();

        Map<String, List<JStatEntity>> collect = jStatDao.selectLastMonthOfOperation(beginDate)
                .stream()
                .collect(Collectors.groupingBy(JStatEntity::getCurrency));

        Map<String, List<StatItem>> map = new HashMap<>();
        collect.forEach((currency, items) -> {
            List<StatItem> statItems = ConvertUtils.sourceToTarget(items, StatItem.class);

            for (StatItem statItem : statItems) {
                if (statItem.getSettleamount() == null) {
                    statItem.setSettleamount(BigDecimal.ZERO);
                }
                if (statItem.getSettlecount() == null) {
                    statItem.setSettlecount(0L);
                }
            }

            log.info("monthMap {} -> {}", currency, items.size());
            map.put(currency, statItems);
        });
        return map;
    }

    private Map<String, BigDecimal> vaMap() {
        jVaManager.refresh();
        Map<String, BigDecimal> map = new HashMap<>();
        for (JVaEntity jVaEntity : jVaDao.selectList(Wrappers.<JVaEntity>lambdaQuery()
                .ne(JVaEntity::getAmount, 0)
                .select(JVaEntity::getAmount, JVaEntity::getCurrency)
        )) {
            map.put(jVaEntity.getCurrency(), jVaEntity.getAmount());
        }
        return map;
    }

    // dashboard
    public Map<String, OrgDashboardDTO> dashboard(Date today) {

        // 账户情况
        CompletableFuture<Map<String, BalanceItem>> balanceMapFuture = CompletableFuture.supplyAsync(() -> {
            return balanceMap();
        });

        // 当日情况
        CompletableFuture<Map<String, StatItem>> todayMapFuture = CompletableFuture.supplyAsync(() -> {
            return todayMap(today);
        });

        // 当月情况
        CompletableFuture<Map<String, List<StatItem>>> monthMapFuture = CompletableFuture.supplyAsync(() -> {
            return monthMap(today);
        });

        // 通联va情况
        CompletableFuture<Map<String, BigDecimal>> vaMapFuture = CompletableFuture.supplyAsync(() -> {
            return vaMap();
        });

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(balanceMapFuture, todayMapFuture, monthMapFuture, vaMapFuture);
        try {
            allFuture.get();
        } catch (InterruptedException e) {
            throw new RenException("系统错误");
        } catch (ExecutionException e) {
            throw new RenException("系统错误");
        }

        try {
            // 账户情况
            Map<String, BalanceItem> balanceMap = balanceMapFuture.get();
            // 当日情况
            Map<String, StatItem> todayMap = todayMapFuture.get();
            // 当月情况
            Map<String, List<StatItem>> monthMap = monthMapFuture.get();
            // 通联va余额
            Map<String, BigDecimal> vaMap = vaMapFuture.get();
            // 返回值
            Map<String, OrgDashboardDTO> map = new HashMap<>();

            Set<String> currencySet = new HashSet<>();
            currencySet.addAll(balanceMap.keySet());
            currencySet.addAll(todayMap.keySet());
            currencySet.addAll(monthMap.keySet());

            for (String currency : currencySet) {
                OrgDashboardDTO dto = new OrgDashboardDTO();
                dto.setBalanceSummary(balanceMap.get(currency));
                dto.setMonthStat(monthMap.get(currency));
                dto.setTodayStat(todayMap.get(currency));
                map.put(currency, dto);
                dto.setVa(vaMap.get(currency));
            }
            return map;
        } catch (Exception ex) {
            throw new RenException("系统错误");
        }
    }
}