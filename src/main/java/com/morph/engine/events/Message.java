package com.morph.engine.events;

import com.morph.engine.entities.Component;

/**
 * Created by Fernando on 1/12/2017.
 */
public class Message {
    private String msg;

    public Message(String msg) {
        this.msg = msg;
    }

    public String getContents() {
        return msg;
    }
}