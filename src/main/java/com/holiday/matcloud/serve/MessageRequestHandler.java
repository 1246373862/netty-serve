package com.holiday.matcloud.serve;

import java.nio.charset.Charset;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.holiday.matcloud.dto.User;
import com.holiday.matcloud.entity.UserChannelInfo;
import com.holiday.matcloud.protocol.command.MessageRequestPacket;
import com.holiday.matcloud.redis.RedisUtil;
import com.holiday.matcloud.utils.CacheUtil;
import com.holiday.matcloud.utils.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Sharable
@Slf4j
@Service
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket>{

	@Resource
	private RedisUtil redisUtil;

	private static MessageRequestHandler messageRequestHandler;

	@PostConstruct
	public void init() {
		messageRequestHandler = this;
	}

	public static MessageRequestHandler INSTANCE = new MessageRequestHandler();
	
	private MessageRequestHandler() {		
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket) throws Exception {
		// TODO Auto-generated method stub
		String message = messageRequestPacket.getMessage();
		log.info("服务端接收消息:"+messageRequestPacket);
		Channel toUserChannel = CacheUtil.userIdChannelMap.get(messageRequestPacket.getToUserId());
		if (toUserChannel != null) {
			String toUser = messageRequestPacket.getToUserId();
			String fileType = messageRequestPacket.getFileType();
			ByteBuf buf = getByteBuf(ctx, message, toUser, fileType);
			toUserChannel.writeAndFlush(new TextWebSocketFrame(buf));
		} else {
			log.info("消息接收方不在本服务器,push消息");
			messageRequestHandler.redisUtil.push("uav-flight-message", MsgUtil.obj2Json(messageRequestPacket));
		}
	}
	
	public ByteBuf getByteBuf(ChannelHandlerContext ctx, String message, String toUser, String fileType) {
		ByteBuf byteBuf = ctx.alloc().buffer();
		JSONObject data = new JSONObject();
		data.put("type", 2);
		data.put("status", 200);
		JSONObject params = new JSONObject();
		params.put("message", message);
		params.put("fileType", fileType);
		params.put("fromUser", CacheUtil.getUser());
		params.put("toUser", toUser);
		data.put("params", params);
		byte []bytes = data.toJSONString().getBytes(Charset.forName("utf-8"));
		byteBuf.writeBytes(bytes);
		return byteBuf;
	}

	/**
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
	 *
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("客户端断开链接" + ctx.channel().localAddress().toString());
		// 移除 redis  和 缓存
		messageRequestHandler.redisUtil.remove(ctx.channel().id().toString());
		CacheUtil.userIdChannelMap.remove(CacheUtil.getUser());
	}



}
