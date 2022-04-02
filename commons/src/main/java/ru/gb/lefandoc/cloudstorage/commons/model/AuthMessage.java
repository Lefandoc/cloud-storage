package ru.gb.lefandoc.cloudstorage.commons.model;

import lombok.Data;

@Data
public class AuthMessage implements CloudMessage {

    private final String login;
    private final String password;

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH;
    }
}
