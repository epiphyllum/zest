package io.renren.zwallet.channel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JPayChannelDao;
import io.renren.zadmin.dao.JWalletConfigDao;
import io.renren.zadmin.dao.JWalletDao;
import io.renren.zadmin.dao.JWalletTxnDao;
import io.renren.zadmin.entity.JPayChannelEntity;
import io.renren.zwallet.scan.TronApi;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

@Service
public class ChannelFactory {

    @Resource
    private JPayChannelDao jPayChannelDao;
    @Resource
    private JWalletTxnDao jWalletTxnDao;
    @Resource
    private JWalletConfigDao jwalletConfigDao;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private TransactionTemplate tx;
    @Resource
    private JWalletDao jWalletDao;
    @Resource
    private TronApi tronApi;

    private ChannelContext channelContext;

    @PostConstruct
    public void init() {
        // 准备渠道上下文
        channelContext = new ChannelContext(
                jWalletDao,
                jwalletConfigDao,
                restTemplate,
                objectMapper,
                jWalletTxnDao,
                tronApi,
                tx
        );
    }

    // 创建支付渠道对象
    private SwapChannel createChannel(JPayChannelEntity channelEntity) {
        try {
            String className = "io.renren.zwallet.channel.impl." + channelEntity.getChannelCode();
            Class<?> aClass = Class.forName(className);
            SwapChannel channel = (SwapChannel) aClass.getDeclaredConstructor().newInstance();
            channel.setContext(channelContext);
            channel.setConfig(channelEntity);
            return channel;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException |
                 InvocationTargetException e) {
            throw new RenException("创建渠道失败");
        }
    }

    // 选择支付渠道
    public SwapChannel getChannel(Long subId, String payCurrency, String stlCurrency) {
        List<JPayChannelEntity> channels = jPayChannelDao.selectList(Wrappers.<JPayChannelEntity>lambdaQuery()
                .eq(JPayChannelEntity::getSubId, subId)
                .eq(JPayChannelEntity::getPayCurrency, payCurrency)
                .eq(JPayChannelEntity::getStlCurrency, stlCurrency)
                .eq(JPayChannelEntity::getEnabled, 1)
        );
        if (channels.size() == 0) {
            throw new RenException("无可用支付渠道, payCurrency:" + payCurrency + ", stlCurrency:" + stlCurrency);
        }
        int index = new Random().nextInt(channels.size());
        JPayChannelEntity jPayChannelEntity = channels.get(index);
        SwapChannel channel = createChannel(jPayChannelEntity);
        return channel;
    }

    // 根据渠道ID获取支付渠道
    public SwapChannel getChannel(Long channelId) {
        JPayChannelEntity channelEntity = jPayChannelDao.selectById(channelId);
        SwapChannel channel = createChannel(channelEntity);
        return channel;
    }

}
