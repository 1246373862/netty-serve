package com.holiday.matcloud.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

@Configuration
public class RedisConfig {

    @Resource
    private RedisConnectionFactory connectionFactory;


    /**
     *  序列化 配置
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        //Jackson2JsonRedisSerializer 序列化方式，取出来的是map
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        //设置key的序列化，让其没有 ""
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * 监听器配置  pus sub
     */

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter(), channelTopic());
        return container;
    }

    @Bean
    public MessageListener messageListenerAdapter() {
        return new MessageListenerAdapter(redisChannelListener());
    }

    @Bean
    public RedisChannelListener redisChannelListener() {
        return new RedisChannelListener();
    }

    @Bean
    ChannelTopic channelTopic() {
        return new ChannelTopic("uav-flight-message");
    }

}
