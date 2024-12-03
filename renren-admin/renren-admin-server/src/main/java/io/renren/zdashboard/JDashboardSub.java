package io.renren.zdashboard;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.commons.tools.utils.ConvertUtils;
import io.renren.zadmin.dao.*;
import io.renren.zadmin.entity.*;
import io.renren.zcommon.CommonUtils;
import io.renren.zdashboard.dto.BalanceItem;
import io.renren.zdashboard.dto.InMoneyItem;
import io.renren.zdashboard.dto.StatItem;
import io.renren.zdashboard.dto.DashboardSubDTO;
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
public class JDashboardSub {
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
    private JAllocateDao jAllocateDao;

    // 某个币种当前余额情况
    private BalanceItem getBalanceItem(List<JBalanceEntity> items, String currency) {
        BalanceItem item = BalanceItem.zero(currency);
        for (JBalanceEntity balanceEntity : items) {
            if (balanceEntity.getBalanceType().startsWith("CHARGE_")) {
                item.setCharge(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("DEPOSIT_")) {
                item.setDeposit(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("CARD_FEE")) {
                item.setCardFee(balanceEntity.getBalance());
            } else if (balanceEntity.getBalanceType().startsWith("CARD_SUM")) {
            } else if (balanceEntity.getBalanceType().startsWith("CARD_COUNT")) {
                item.setTotalCard(balanceEntity.getBalance().longValue());
            } else if (balanceEntity.getBalanceType().startsWith("SUB_VA_")) {
                item.setBalance(balanceEntity.getBalance());
            }
        }
        return item;
    }

    // 当前余额
    private Map<String, BalanceItem> balanceMap(Long subId) {
        Map<String, BalanceItem> map = new HashMap<>();
        jBalanceDao.selectList(Wrappers.<JBalanceEntity>lambdaQuery()
                        .eq(JBalanceEntity::getOwnerId, subId)
                ).stream()
                .collect(Collectors.groupingBy(JBalanceEntity::getCurrency))
                .forEach((currency, items) -> {
                    BalanceItem item = getBalanceItem(items, currency);
                    map.put(currency, item);
                    log.info("balanceMap: {} -> {}", currency, item);
                });
        return map;
    }

    // 清算笔数 + 清算金额
    private Map<String, JStatEntity> settleMap(Long subId) {
        Map<String, JStatEntity> map = new HashMap<>();
        jStatDao.selectSumOfSub(subId)
                .stream().collect(Collectors.groupingBy(JStatEntity::getCurrency))
                .forEach((currency, items) -> {
                    map.put(currency, items.get(0));
                });
        return map;
    }

    // 今日日统计
    private Map<String, StatItem> todayMap(Date today, Long subId) {
        Map<String, List<VDepositEntity>> depositMap = vDepositDao.selectDayOfSub(today, subId)
                .stream().collect(Collectors.groupingBy(VDepositEntity::getCurrency));
        Map<String, List<VWithdrawEntity>> withdrawMap = vWithdrawDao.selectDayOfSub(today, subId)
                .stream().collect(Collectors.groupingBy(VWithdrawEntity::getCurrency));
        Map<String, List<VCardEntity>> cardMap = vCardDao.selectDayOfSub(today, subId)
                .stream().collect(Collectors.groupingBy(VCardEntity::getCurrency));
        Map<String, List<JAuthEntity>> authMap = jAuthDao.selectDayOfSub(today, subId)
                .stream().collect(Collectors.groupingBy(JAuthEntity::getCurrency));

        Map<String, List<JAllocateEntity>> collect = jAllocateDao.selectByDateOfSub(today, subId)
                .stream().collect(Collectors.groupingBy(JAllocateEntity::getType));
        Map<String, List<JAllocateEntity>> inMoneyMap = collect.getOrDefault("m2s", List.of())
                .stream().collect(Collectors.groupingBy(JAllocateEntity::getCurrency));
        Map<String, List<JAllocateEntity>> outMoneyMap = collect.getOrDefault("s2m", List.of())
                .stream().collect(Collectors.groupingBy(JAllocateEntity::getCurrency));

        Set<String> currencySet = new HashSet<>();
        currencySet.addAll(depositMap.keySet());
        currencySet.addAll(withdrawMap.keySet());
        currencySet.addAll(cardMap.keySet());

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
            List<JAllocateEntity> inEntities = inMoneyMap.get(currency);
            if (inEntities != null && inEntities.size() > 0) {
                JAllocateEntity jAllocateEntity = inEntities.get(0);
                item.setInMoney(jAllocateEntity.getAmount());
                item.setInMoneyCount(jAllocateEntity.getId());
            }

            // 出金数据
            List<JAllocateEntity> outEntities = outMoneyMap.get(currency);
            if (outEntities != null && outEntities.size() > 0) {
                JAllocateEntity jAllocateEntity = outEntities.get(0);
                item.setOutMoney(jAllocateEntity.getAmount());
                item.setOutMoneyCount(jAllocateEntity.getId());
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
    private Map<String, List<StatItem>> monthMap(Date today, Long subId) {
        Date beginDate = CommonUtils.dateSubtract(today, -30);
        Map<String, List<JStatEntity>> collect = jStatDao.selectLastMonthOfSub(beginDate, subId)
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

    // 过去30天统计
    private Map<String, List<InMoneyItem>> inMoneyMap(Date today, Long subId) {
        Date beginDate = CommonUtils.dateSubtract(today, -30);
        Map<String, List<JAllocateEntity>> collect = jAllocateDao.selectLatestOfSub(beginDate, subId)
                .stream()
                .collect(Collectors.groupingBy(JAllocateEntity::getCurrency));
        Map<String, List<InMoneyItem>> map = new HashMap<>();
        collect.forEach((currency, items) -> {
            List<InMoneyItem> inMoneyItems = new ArrayList<>();
            for (JAllocateEntity item : items) {
                InMoneyItem inMoneyItem = new InMoneyItem();
                inMoneyItem.setStatDate(item.getStatDate());
                inMoneyItem.setCount(item.getId());
                inMoneyItem.setAmount(item.getAmount());
                inMoneyItems.add(inMoneyItem);
            }
            map.put(currency, inMoneyItems);
        });
        return map;
    }

    // dashboard
    public Map<String, DashboardSubDTO> dashboard(Date today, Long subId) {
        // 账户情况
        CompletableFuture<Map<String, BalanceItem>> balanceMapFuture = CompletableFuture.supplyAsync(() -> {
            return balanceMap(subId);
        });

        // 清算情况
        CompletableFuture<Map<String, JStatEntity>> settleMapFuture = CompletableFuture.supplyAsync(() -> {
            return settleMap(subId);
        });

        // 当日情况
        CompletableFuture<Map<String, StatItem>> todayMapFuture = CompletableFuture.supplyAsync(() -> {
            return todayMap(today, subId);
        });

        // 当月情况
        CompletableFuture<Map<String, List<StatItem>>> monthMapFuture = CompletableFuture.supplyAsync(() -> {
            return monthMap(today, subId);
        });

        // 当月
        CompletableFuture<Map<String, List<InMoneyItem>>> inMoneyMapFuture = CompletableFuture.supplyAsync(() -> {
            return inMoneyMap(today, subId);
        });

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(balanceMapFuture, todayMapFuture, monthMapFuture, inMoneyMapFuture);
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
            // 清算情况
            Map<String, JStatEntity> settleMap = settleMapFuture.get();
            // 当日情况
            Map<String, StatItem> todayMap = todayMapFuture.get();
            // 当月情况
            Map<String, List<StatItem>> monthMap = monthMapFuture.get();
            // 当月情况
            Map<String, List<InMoneyItem>> inMoneyMap = inMoneyMapFuture.get();
            // 返回值
            Map<String, DashboardSubDTO> map = new HashMap<>();
            JSubEntity subEntity = jSubDao.selectById(subId);
            if (subEntity == null) {
                log.error("{} 子商户号不存在", subId);
                throw new RenException("非法用户");
            }
            JMerchantEntity merchant = jMerchantDao.selectById(subEntity.getMerchantId());
            String[] split = merchant.getCurrencyList().split(",");

            for (String currency : split) {
                DashboardSubDTO dto = new DashboardSubDTO();
                dto.setToday(today);
                dto.setName(subEntity.getCusname());

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

                dto.setMonthStat(monthMap.get(currency));
                dto.setTodayStat(todayMap.get(currency));
                dto.setMoneyStat(inMoneyMap.get(currency));
                if (dto.getTodayStat() == null) {
                    dto.setTodayStat(StatItem.zero(currency, today));
                }
                dto.setToday(today);
                map.put(currency, dto);
            }
            return map;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RenException("系统错误");
        }
    }
}