package ru.gb.lefandoc.cloudstorage.server.model;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    MessageType getMessageType();
}
