package io.renren.zdashboard;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zdashboard.dto.BalanceItem;
import io.renren.zdashboard.dto.InMoneyItem;
import io.renren.zdashboard.dto.StatItem;
import io.renren.zdashboard.dto.MerchantDashboardDTO;
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
public class JDashboardMerchant {
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
    @Resource
    private JMoneyDao jMoneyDao;

    // 某个币种当前余额情况
    private BalanceItem getBalanceItem(List<JBalanceEntity> items, String currency) {
        BalanceItem item = BalanceItem.zero(currency);

        BigDecimal merchantVa = null;
        for (JBalanceEntity balanceEntity : items) {
            if (balanceEntity.getBalanceType().startsWith("CHARGE_")) {
                item.setCharge(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("DEPOSIT_")) {
                item.setDeposit(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("CARD_FEE")) {
                item.setCardFee(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("CARD_SUM")) {
                item.setCardSum(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("SUB_VA_")) {
                item.setBalance(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("VA_")) {
                merchantVa = balanceEntity.getBalance();
            }
        }
        item.setBalance(item.getBalance().add(merchantVa));
        return item;
    }

    // 当前余额
    private Map<String, BalanceItem> balanceMap(Long merchantId) {
        Map<String, BalanceItem> map = new HashMap<>();

        List<Long> subIdList = jSubDao.selectList(Wrappers.<JSubEntity>lambdaQuery()
                .eq(JSubEntity::getMerchantId, merchantId)
                .select(JSubEntity::getId)
        ).stream().map(JSubEntity::getId).toList();

        List<Long> allId = new ArrayList<>(subIdList);
        allId.add(merchantId);

        jBalanceDao.selectBalanceOfOwners(allId).stream()
                .collect(Collectors.groupingBy(JBalanceEntity::getCurrency))
                .forEach((currency, items) -> {
                    BalanceItem item = getBalanceItem(items, currency);
                    map.put(currency, item);
                    log.info("balanceMap: {} -> {}", currency, item);
                });
        return map;
    }

    // 今日日统计
    private Map<String, StatItem> todayMap(Date today, Long merchantId) {
        Map<String, List<VDepositEntity>> depositMap = vDepositDao.selectDayOfMerchant(today, merchantId)
                .stream().collect(Collectors.groupingBy(VDepositEntity::getCurrency));
        Map<String, List<VWithdrawEntity>> withdrawMap = vWithdrawDao.selectDayOfMerchant(today, merchantId)
                .stream().collect(Collectors.groupingBy(VWithdrawEntity::getCurrency));
        Map<String, List<VCardEntity>> cardMap = vCardDao.selectDayOfMerchant(today, merchantId)
                .stream().collect(Collectors.groupingBy(VCardEntity::getCurrency));
        Map<String, List<JAuthEntity>> authMap = jAuthDao.selectDayOfMerchant(today, merchantId)
                .stream().collect(Collectors.groupingBy(JAuthEntity::getCurrency));
        Map<String, List<JMoneyEntity>> moneyMap = jMoneyDao.selectByDateOfMerchant(today, merchantId)
                .stream().collect(Collectors.groupingBy(JMoneyEntity::getCurrency));

        Set<String> currencySet = new HashSet<>();
        currencySet.addAll(depositMap.keySet());
        currencySet.addAll(withdrawMap.keySet());
        currencySet.addAll(cardMap.keySet());
        currencySet.addAll(moneyMap.keySet());

        Map<String, StatItem> map = new HashMap<>();
        for (String currency : currencySet) {
            log.info("todayMap-准备币种: {}", currency);
            StatItem item = StatItem.zero(currency, today);

            // 充值
            List<VDepositEntity> vDepositEntities = depositMap.get(currency);
            if (vDepositEntities != null && vDepositEntities.size() > 0) {
                VDepositEntity depositEntity = vDepositEntities.get(0);
                log.info("充值记录: {}", depositEntity);
                item.setCardSum(depositEntity.getCardSum());
                item.setCharge(depositEntity.getCharge());
                item.setDeposit(depositEntity.getDeposit());
            }

            // 提现
            List<VWithdrawEntity> vWithdrawEntities = withdrawMap.get(currency);
            if (vWithdrawEntities != null && vWithdrawEntities.size() > 0) {
                VWithdrawEntity withdrawEntity = vWithdrawEntities.get(0);
                log.info("提现记录: {}", withdrawEntity);
                item.setWithdraw(withdrawEntity.getCardSum());
                item.setWithdrawCharge(withdrawEntity.getCharge());
            }

            // 开卡数据
            List<VCardEntity> vCardEntities = cardMap.get(currency);
            if (vCardEntities != null && vCardEntities.size() > 0) {
                VCardEntity vCardEntity = vCardEntities.get(0);
                log.info("开卡记录: {}", vCardEntity);
                item.setTotalCard(vCardEntity.getTotalCard());
                item.setCardFee(vCardEntity.getMerchantfee());
            }

            // 消费交易数据
            List<JAuthEntity> authEntities = authMap.get(currency);
            if (authEntities != null && authEntities.size() > 0) {
                JAuthEntity jAuthEntity = authEntities.get(0);
                log.info("消费记录: {}", jAuthEntity);
                item.setSettlecount(jAuthEntity.getId());
                item.setSettleamount(jAuthEntity.getSettleamount());
            }

            // 入金数据
            List<JMoneyEntity> moneyEntities = moneyMap.get(currency);
            if (moneyEntities != null && moneyEntities.size() > 0) {
                JMoneyEntity jMoneyEntity = moneyEntities.get(0);
                item.setInMoney(jMoneyEntity.getAmount());
                item.setInMoneyCount(jMoneyEntity.getId());
            }

            if (item.getSettleamount() == null) {
                item.setSettleamount(BigDecimal.ZERO);
            }
            if (item.getSettlecount() == null) {
                item.setSettlecount(0L);
            }

            item.setStatDate(today);
            item.setCurrency(currency);
            map.put(currency, item);
        }
        return map;
    }

    // 过去30天统计
    private Map<String, List<StatItem>> monthMap(Date today, Long merchantId) {
        Calendar calendar = Calendar.getInstance(); // 获取当前日期的Calendar实例
        calendar.add(Calendar.DAY_OF_MONTH, -30); // 向当前日期减去30天
        Date beginDate = calendar.getTime();

        Map<String, List<JStatEntity>> collect = jStatDao.selectLastMonthOfMerchant(beginDate, merchantId)
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

    // 近30天入金
    private Map<String, List<InMoneyItem>> monthInMoneyMap(Date today, Long merchantId) {
        Map<String, List<JMoneyEntity>> collect = jMoneyDao.selectLatestOfMerchant(today, merchantId)
                .stream()
                .collect(Collectors.groupingBy(JMoneyEntity::getCurrency));
        Map<String, List<InMoneyItem>> map = new HashMap<>();
        collect.forEach((currency, items) -> {
            List<InMoneyItem> inMoneyItems = new ArrayList<>();
            for (JMoneyEntity item : items) {
                InMoneyItem inMoneyItem = new InMoneyItem();
                inMoneyItem.setAmount(item.getAmount());
                inMoneyItem.setCount(item.getId());
                inMoneyItem.setStatDate(item.getStatDate());
                inMoneyItems.add(inMoneyItem);
            }
            map.put(currency, inMoneyItems);
        });
        return map;
    }

    // dashboard
    public Map<String, MerchantDashboardDTO> dashboard(Date today, Long merchantId) {


        // 账户情况
        CompletableFuture<Map<String, BalanceItem>> balanceMapFuture = CompletableFuture.supplyAsync(() -> {
            return balanceMap(merchantId);
        });

        // 当日情况
        CompletableFuture<Map<String, StatItem>> todayMapFuture = CompletableFuture.supplyAsync(() -> {
            return todayMap(today, merchantId);
        });

        // 当月情况
        CompletableFuture<Map<String, List<StatItem>>> monthMapFuture = CompletableFuture.supplyAsync(() -> {
            return monthMap(today, merchantId);
        });

        // 近30天入金情况
        CompletableFuture<Map<String, List<InMoneyItem>>> monthInMoneyMapFuture = CompletableFuture.supplyAsync(() -> {
            return monthInMoneyMap(today, merchantId);
        });

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(balanceMapFuture, todayMapFuture, monthMapFuture);
        try {
            allFuture.get();
        } catch (InterruptedException e) {
            log.error("中断错误");
            e.printStackTrace();
            throw new RenException("系统错误");
        } catch (ExecutionException e) {
            log.error("执行错误");
            e.printStackTrace();
            throw new RenException("系统错误");
        }

        try {
            // 账户情况
            Map<String, BalanceItem> balanceMap = balanceMapFuture.get();
            // 当日情况
            Map<String, StatItem> todayMap = todayMapFuture.get();
            // 当月情况
            Map<String, List<StatItem>> monthMap = monthMapFuture.get();
            // 当月入金情况
            Map<String, List<InMoneyItem>> monthInMoneyMap = monthInMoneyMapFuture.get();
            // 返回值
            Map<String, MerchantDashboardDTO> map = new HashMap<>();

            Set<String> currencySet = new HashSet<>();
            currencySet.addAll(balanceMap.keySet());
            currencySet.addAll(todayMap.keySet());
            currencySet.addAll(monthMap.keySet());
            currencySet.addAll(monthInMoneyMap.keySet());

            for (String currency : currencySet) {
                MerchantDashboardDTO dto = new MerchantDashboardDTO();
                dto.setBalanceSummary(balanceMap.get(currency));
                dto.setMonthStat(monthMap.get(currency));
                dto.setTodayStat(todayMap.get(currency));
                if (dto.getTodayStat() == null) {
                    dto.setTodayStat(StatItem.zero(currency, today));
                }
                dto.setMoneyStat(monthInMoneyMap.get(currency));
                map.put(currency, dto);
            }
            return map;
        } catch (Exception ex) {
            throw new RenException("系统错误");
        }
    }
}