package com.holiday.matcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserChannelInfo {
    //服务端：IP
    private String ip;

    //服务端：port
    private int port;

    //channelId
    private String channelId;

    //链接时间
    private Date linkDate;

}
