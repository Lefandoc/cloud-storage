package ru.gb.lefandoc.cloudstorage.commons.model;

import lombok.Data;

@Data
public class AuthStatusMessage implements CloudMessage {

    private Boolean isAuth;

    public AuthStatusMessage(Boolean isAuth) {
        this.isAuth = isAuth;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH_STATUS;
    }
}
