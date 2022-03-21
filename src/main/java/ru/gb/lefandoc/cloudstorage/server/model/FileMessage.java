package ru.gb.lefandoc.cloudstorage.server.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileMessage implements CloudMessage {

    private final byte[] bytes;
    private final long size;
    private final String name;

    public FileMessage(Path path) throws IOException {
        bytes = Files.readAllBytes(path);
        size = bytes.length;
        name = path.getFileName().toString();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE;
    }
}
