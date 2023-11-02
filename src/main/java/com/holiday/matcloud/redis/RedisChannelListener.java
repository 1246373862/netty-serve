package com.holiday.matcloud.redis;

import com.alibaba.fastjson.JSON;
import com.holiday.matcloud.protocol.command.MessageRequestPacket;
import com.holiday.matcloud.utils.CacheUtil;
import com.holiday.matcloud.utils.MsgUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;

@Slf4j
public class RedisChannelListener implements MessageListener {
    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
        log.info("sub message :) channel[cleanNoStockCache] !");

        log.info("接收到PUSH消息：{}", message);
        MessageRequestPacket msgAgreement = JSON.parseObject(JSON.parse(message.getBody()).toString(), MessageRequestPacket.class);
        String toChannelId = msgAgreement.getToUserId();
        Channel channel = CacheUtil.userIdChannelMap.get(toChannelId);
        if (null == channel) {
            return;
        }
        // 发送消息
        channel.writeAndFlush(new TextWebSocketFrame(MsgUtil.obj2Json(msgAgreement) + "  redis listener lalalalala "));

    }

}
