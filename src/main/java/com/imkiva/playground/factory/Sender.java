package com.imkiva.playground.factory;

/**
 * @author kiva
 * @date 2019-07-13
 */
public class Sender {
    class Message {
        public boolean isFuckingRobot() {
            return false;
        }
    }

    public void onMessage(Message msg) {
        MessageFactory factory = null;
        if (msg.isFuckingRobot()) {
            ShutUpMessageFactory shut = MessageFactoryBuilder.shutUp(msg);
            shut.shutTime = 100000;
            factory = shut;
        } else {
            TextMessageFactory text = MessageFactoryBuilder.text(msg);
            text.content = "已执行";
            factory = text;
        }

        sendMessage(factory);
    }

    private void sendMessage(MessageFactory m) {
        // .....
    }
}
