package com.nookure.sync.server;

import com.nookure.sync.protocol.login.ClientBoundIdentificationRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends ChannelInboundHandlerAdapter {
  Logger logger = LoggerFactory.getLogger(ServerHandler.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.info("Client connected: {}", ctx.channel().remoteAddress());

    var identificationRequest = new ClientBoundIdentificationRequestPacket();

    ctx.writeAndFlush(identificationRequest);
  }
}
