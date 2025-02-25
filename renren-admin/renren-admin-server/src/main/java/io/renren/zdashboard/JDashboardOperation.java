package io.renren.zdashboard;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zcommon.CommonUtils;
import io.renren.zdashboard.dto.BalanceItem;
import io.renren.zdashboard.dto.InMoneyItem;
import io.renren.zdashboard.dto.DashboardOperationDTO;
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
public class JDashboardOperation {
    @Resource
    private JMoneyDao jMoneyDao;
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
        BalanceItem item = BalanceItem.zero(currency);

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
            } else if (balanceEntity.getBalanceType().startsWith("CARD_COUNT")) {
                item.setTotalCard(balanceEntity.getBalance().longValue());
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
        if (va == null || subVa == null) {
            throw new RenException("va sub_va not found");
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

    // 清算笔数 + 清算金额
    private Map<String, JStatEntity> settleMap() {
        Map<String, JStatEntity> map = new HashMap<>();
        jStatDao.selectSumOfOperation()
                .stream().collect(Collectors.groupingBy(JStatEntity::getCurrency))
                .forEach((currency, items) -> {
                    map.put(currency, items.get(0));
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
        Map<String, List<JMoneyEntity>> moneyMap = jMoneyDao.selectByDateOfOperation(today)
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

            // 入金数据
            List<JMoneyEntity> moneyEntities = moneyMap.get(currency);
            if (moneyEntities != null && moneyEntities.size() > 0) {
                JMoneyEntity jMoneyEntity = moneyEntities.get(0);
                log.info("今日入金数据: {}", jMoneyEntity);
                item.setInMoneyCount(jMoneyEntity.getId());
                item.setInMoney(jMoneyEntity.getAmount());
            }

            item.setStatDate(today);
            item.setCurrency(currency);

            if (item.getSettleamount() == null) {
                item.setSettleamount(BigDecimal.ZERO);
            }
            if (item.getSettlecount() == null) {
                item.setSettlecount(0L);
            }

            map.put(currency, item);
        }
        return map;
    }

    // 过去30天统计
    private Map<String, List<StatItem>> monthMap(Date today) {
        Date beginDate = CommonUtils.dateSubtract(today, -30);

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

    // 近30天入金
    private Map<String, List<InMoneyItem>> monthInMoneyMap(Date today) {
        Date beginDate = CommonUtils.dateSubtract(today, -30);
        Map<String, List<JMoneyEntity>> collect = jMoneyDao.selectLatestOfOperation(beginDate)
                .stream()
                .collect(Collectors.groupingBy(JMoneyEntity::getCurrency));
        Map<String, List<InMoneyItem>> map = new HashMap<>();
        collect.forEach((currency, items) -> {
            log.info("入金数据: {} -> {}", currency, items.size());
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

    // 通联va余额情况
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
    public Map<String, DashboardOperationDTO> dashboard(Date today) {

        // 账户情况
        CompletableFuture<Map<String, BalanceItem>> balanceMapFuture = CompletableFuture.supplyAsync(() -> {
            return balanceMap();
        });

        // 清算情况
        CompletableFuture<Map<String, JStatEntity>> settleMapFuture = CompletableFuture.supplyAsync(() -> {
            return settleMap();
        });

        // 当日情况
        CompletableFuture<Map<String, StatItem>> todayMapFuture = CompletableFuture.supplyAsync(() -> {
            return todayMap(today);
        });

        // 近30天情况
        CompletableFuture<Map<String, List<StatItem>>> monthMapFuture = CompletableFuture.supplyAsync(() -> {
            return monthMap(today);
        });

        // 近30天入金情况
        CompletableFuture<Map<String, List<InMoneyItem>>> monthInMoneyMapFuture = CompletableFuture.supplyAsync(() -> {
            return monthInMoneyMap(today);
        });

        // 通联va情况
        CompletableFuture<Map<String, BigDecimal>> vaMapFuture = CompletableFuture.supplyAsync(() -> {
            return vaMap();
        });

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(balanceMapFuture, todayMapFuture, monthMapFuture, vaMapFuture, monthInMoneyMapFuture);
        try {
            allFuture.get();
        } catch (InterruptedException e) {
            throw new RenException("系统错误");
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RenException("系统错误");
        }

        try {
            // 账户情况
            Map<String, BalanceItem> balanceMap = balanceMapFuture.get();
            // 清算情况
            Map<String, JStatEntity> settleMap = settleMapFuture.get();
            // 当日情况
            Map<String, StatItem> todayMap = todayMapFuture.get();
            // 当月情况
            Map<String, List<StatItem>> monthMap = monthMapFuture.get();
            // 当月入金情况
            Map<String, List<InMoneyItem>> monthInMoneyMap = monthInMoneyMapFuture.get();
            // 通联va余额
            Map<String, BigDecimal> vaMap = vaMapFuture.get();

            Set<String> currencySet = new HashSet<>();
            currencySet.addAll(balanceMap.keySet());
            currencySet.addAll(todayMap.keySet());
            currencySet.addAll(monthMap.keySet());
            currencySet.addAll(monthInMoneyMap.keySet());

            // 返回值
            Map<String, DashboardOperationDTO> map = new HashMap<>();

            for (String currency : currencySet) {
                DashboardOperationDTO dto = new DashboardOperationDTO();

                BalanceItem balanceItem = balanceMap.get(currency);
                JStatEntity statEntity = settleMap.get(currency);

                if (statEntity != null) {
                    balanceItem.setSettleamount(statEntity.getSettleamount());
                    balanceItem.setSettlecount(statEntity.getSettlecount());
                } else {
                    balanceItem.setSettleamount(BigDecimal.ZERO);
                    balanceItem.setSettlecount(0L);
                }

                dto.setBalanceSummary(balanceItem);
                dto.setName("机构");
                dto.setToday(today);

                dto.setMonthStat(monthMap.get(currency));
                dto.setTodayStat(todayMap.get(currency));
                if (dto.getTodayStat() == null) {
                    dto.setTodayStat(StatItem.zero(currency, today));
                }
                dto.setVa(vaMap.get(currency));
                dto.setMoneyStat(monthInMoneyMap.get(currency));
                map.put(currency, dto);
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RenException("系统错误");
        }
    }
}