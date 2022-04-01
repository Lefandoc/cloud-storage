package ru.gb.lefandoc.cloudstorage.server.handler;

import lombok.extern.slf4j.Slf4j;
import ru.gb.lefandoc.cloudstorage.commons.model.CloudMessage;
import ru.gb.lefandoc.cloudstorage.commons.model.FileMessage;
import ru.gb.lefandoc.cloudstorage.commons.model.FileRequest;
import ru.gb.lefandoc.cloudstorage.commons.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class CloudMessageHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Files.createDirectories(Paths.get("serverDir"));
        serverDir = Paths.get("serverDir");
        ctx.writeAndFlush(new ListMessage(serverDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case FILE:
                FileMessage fileMessage = (FileMessage) cloudMessage;
                Files.write(serverDir.resolve(fileMessage.getName()), fileMessage.getBytes());
                ctx.writeAndFlush(new ListMessage(serverDir));
                log.info("File {} uploaded", fileMessage.getName());
                break;
            case FILE_REQUEST:
                FileRequest fr = (FileRequest) cloudMessage;
                ctx.writeAndFlush(new FileMessage(serverDir.resolve(fr.getName())));
                log.info("File {} downloaded", fr.getName());
                break;
        }
    }
}
