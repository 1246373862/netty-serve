package com.holiday.matcloud.serve;

import com.holiday.matcloud.dto.User;
import com.holiday.matcloud.entity.UserChannelInfo;
import com.holiday.matcloud.protocol.command.RegisterRequestPacket;
import com.holiday.matcloud.redis.RedisUtil;
import com.holiday.matcloud.utils.CacheUtil;
import com.holiday.matcloud.utils.MsgUtil;
import com.holiday.matcloud.utils.SpringUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * 注册 绑定用户channel
 */
@Sharable
@Component
public class RegisterRequestHandler extends SimpleChannelInboundHandler<RegisterRequestPacket> {

    @Resource
    private RedisUtil redisUtil;

    private static RegisterRequestHandler registerRequestHandler;

    @PostConstruct
    public void init() {
        registerRequestHandler = this;
    }

    public static RegisterRequestHandler INSTANCE = new RegisterRequestHandler();

    private RegisterRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterRequestPacket registerRequestPacket) throws Exception {
        User loginUser = registerRequestPacket.getUser();
        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println("客户端上线");
        System.out.println("客户端信息：有一客户端链接到本服务端。channelId：" + channel.id());
        System.out.println("客户端IP:" + channel.localAddress().getHostString());
        System.out.println("客户端Port:" + channel.localAddress().getPort());
        System.out.println("客户端信息完毕");
        //保存用户信息
        UserChannelInfo userChannelInfo = new UserChannelInfo(channel.localAddress().getHostString(),
                channel.localAddress().getPort(), channel.id().toString(), new Date());
        // 放入 redis 和 缓存
        registerRequestHandler.redisUtil.pushObj(userChannelInfo);
        //通知客户端链接建立成功
        String str = "通知客户端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
        ctx.writeAndFlush(MsgUtil.buildMsg(channel.id().toString(), str));
		CacheUtil.setUser(loginUser.getUserId());
        CacheUtil.userIdChannelMap.put(loginUser.getUserId(), ctx.channel());
    }

}
