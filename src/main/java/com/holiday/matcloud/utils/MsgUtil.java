package com.holiday.matcloud.utils;

import com.alibaba.fastjson.JSON;
import com.holiday.matcloud.protocol.command.MessageRequestPacket;

public class MsgUtil {
    public static MessageRequestPacket buildMsg(String channelId, String content) {
        return new MessageRequestPacket(channelId, content);
    }

    public static MessageRequestPacket json2Obj(String objJsonStr) {
        return JSON.parseObject(objJsonStr, MessageRequestPacket.class);
    }

    public static String obj2Json(MessageRequestPacket msgAgreement) {
        return JSON.toJSONString(msgAgreement);
    }
}
