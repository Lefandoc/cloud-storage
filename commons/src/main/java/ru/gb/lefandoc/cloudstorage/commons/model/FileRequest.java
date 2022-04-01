package ru.gb.lefandoc.cloudstorage.commons.model;

import lombok.Data;

@Data
public class FileRequest implements CloudMessage {

    private final String name;

    public FileRequest(String name) {
        this.name = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}
