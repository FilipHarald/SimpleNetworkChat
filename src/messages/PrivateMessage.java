package messages;

import javax.swing.*;

public class PrivateMessage extends ChatMessage {

	public PrivateMessage(String sender, String[] recipients, String textMessage, ImageIcon image) {
		super(sender, recipients, textMessage, image);
	}

	public String toString() {
        return String.format("[%s] <PM from %s> %s", getDate(getTimeReceived()), getSender(), getTextMessage());
    }

    @Override
    public Message copy(String[] newRecipients) {
        return new PrivateMessage(getSender(), newRecipients, getTextMessage(), getImage());
    }
    
}
