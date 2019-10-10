package com.imkiva.playground.factory;

/**
 * @author kiva
 * @date 2019-07-13
 */
public class MessageFactoryBuilder {
    public static ShutUpMessageFactory shutUp(Sender.Message msg) {
        return new ShutUpMessageFactory();
    }

    public static TextMessageFactory text(Sender.Message msg) {
        return new TextMessageFactory();
    }
}
