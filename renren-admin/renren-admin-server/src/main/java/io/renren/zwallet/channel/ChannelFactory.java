package io.renren.zwallet.channel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.renren.commons.tools.exception.RenException;
import io.renren.zadmin.dao.JPayChannelDao;
import io.renren.zadmin.entity.JPayChannelEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

@Service
public class ChannelFactory {

    @Resource
    private JPayChannelDao jPayChannelDao;

    private PayChannel createChannel(JPayChannelEntity channelEntity) {
        // 准备渠道上下文
        ChannelContext context = new ChannelContext(channelEntity);
        try {
            String className = "io.renren.zwallet.channel.impl." + channelEntity.getChannelCode();
            Class<?> aClass = Class.forName(className);
            PayChannel channel = (PayChannel) aClass.getDeclaredConstructor().newInstance();
            channel.setContext(context);
            return channel;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException |
                 InvocationTargetException e) {
            throw new RenException("创建渠道失败");
        }
    }

    public PayChannel getChannel(Long subId, String currency) {
        List<JPayChannelEntity> channels = jPayChannelDao.selectList(Wrappers.<JPayChannelEntity>lambdaQuery()
                .eq(JPayChannelEntity::getSubId, subId)
                .eq(JPayChannelEntity::getStlCurrency, currency)
                .eq(JPayChannelEntity::getEnabled, 1)
        );
        if (channels.size() == 0) {
            throw new RenException("无可用支付渠道");
        }
        int index = new Random().nextInt(channels.size());
        JPayChannelEntity jPayChannelEntity = channels.get(index);
        PayChannel channel = createChannel(jPayChannelEntity);
        return channel;
    }

    public PayChannel getChannel(Long channelId) {
        JPayChannelEntity channelEntity = jPayChannelDao.selectById(channelId);
        PayChannel channel = createChannel(channelEntity);
        return channel;
    }

}
