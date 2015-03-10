package messages;

import javax.swing.*;

public class ServerMessage extends ChatMessage {

    public ServerMessage(String[] recipients, String textMessage) {
        super("SERVER", recipients, textMessage, null);
    }

    public Message copy(String[] newRecipients) {
        return new ServerMessage(newRecipients, getTextMessage());
    }
}
