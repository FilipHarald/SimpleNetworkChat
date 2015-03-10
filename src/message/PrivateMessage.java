package message;

import javax.swing.*;

public class PrivateMessage extends ChatMessage {

	public PrivateMessage(String sender, String[] recipients, String textMessage, ImageIcon image) {
		super(sender, recipients, textMessage, image);
	}

	public String toString() {
        return String.format("[%s] <PM from %s to %s> %s", getDate(getTimeReceived()), getSender(), getRecipients()[1], getTextMessage());
    }

    @Override
    public Message copy(String[] newRecipients) {
        return new PrivateMessage(getSender(), newRecipients, getTextMessage(), getImage());
    }
    
}
