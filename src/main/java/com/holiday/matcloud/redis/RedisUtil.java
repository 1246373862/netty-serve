package com.holiday.matcloud.redis;

import com.alibaba.fastjson.JSON;
import com.holiday.matcloud.entity.UserChannelInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public void pushObj(UserChannelInfo userChannelInfo) {
        redisTemplate.opsForHash().put("uav-flight-user",
                userChannelInfo.getChannelId(), JSON.toJSONString(userChannelInfo));
    }

    public List<UserChannelInfo> popList() {
        List<Object> values = redisTemplate.opsForHash().values("uav-flight-user");
        if (null == values) {
            return new ArrayList<>();
        }

        List<UserChannelInfo> userChannelInfoList = new ArrayList<>();

        for (Object strJson : values) {
            userChannelInfoList.add(JSON.parseObject(strJson.toString(), UserChannelInfo.class));
        }
        return userChannelInfoList;
    }

    public void remove(String channelId) {
        redisTemplate.opsForHash().delete("uav-flight-user", channelId);
    }

    public void clear() {
        redisTemplate.delete("uav-flight-user");
    }


    public void push(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);

    }


}
