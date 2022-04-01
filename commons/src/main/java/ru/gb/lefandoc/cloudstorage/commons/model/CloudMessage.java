package ru.gb.lefandoc.cloudstorage.commons.model;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    MessageType getMessageType();
}
