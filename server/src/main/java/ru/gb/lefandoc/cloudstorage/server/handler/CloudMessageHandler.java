package ru.gb.lefandoc.cloudstorage.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.gb.lefandoc.cloudstorage.commons.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class CloudMessageHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir;
    private final JdbcHandler jdbc = new JdbcHandler();
    private boolean isAuth;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Files.createDirectories(Paths.get("serverDir"));
        serverDir = Paths.get("serverDir");
        if (isAuth) {
            ctx.writeAndFlush(new ListMessage(serverDir));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case FILE:
                if (isAuth) {
                    FileMessage fileMessage = (FileMessage) cloudMessage;
                    Files.write(serverDir.resolve(fileMessage.getName()), fileMessage.getBytes());
                    ctx.writeAndFlush(new ListMessage(serverDir));
                    log.info("File {} uploaded", fileMessage.getName());
                    break;
                }
            case FILE_REQUEST:
                if (isAuth) {
                    FileRequest fr = (FileRequest) cloudMessage;
                    ctx.writeAndFlush(new FileMessage(serverDir.resolve(fr.getName())));
                    log.info("File {} downloaded", fr.getName());
                }
                break;
            case AUTH:
                AuthMessage am = (AuthMessage) cloudMessage;
                log.info("User {} trying to login", am.getLogin());
                jdbc.connect();
                isAuth = jdbc.findUser(am.getLogin(), am.getPassword());
                if (isAuth) {
                    ctx.writeAndFlush(new AuthStatusMessage(true));
                    ctx.writeAndFlush(new ListMessage(serverDir));
                }
                break;
        }
    }
}
