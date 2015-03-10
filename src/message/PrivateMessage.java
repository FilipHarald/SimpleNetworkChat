package message;

import javax.swing.*;

public class PrivateMessage extends ChatMessage {

	private boolean senderCopy = false;
	private int group;
	
	public PrivateMessage(String sender, String[] recipients, String textMessage, ImageIcon image, int group) {
		super(sender, recipients, textMessage, image);
		this.group = group;
	}
	
	public void setSenderCopy(boolean value) {
		senderCopy = value;
	}
	
	public int getGroup() {
		return group;
	}

	public String toString() {
		if (senderCopy)
			return String.format("[%s] <PM to %s> %s", getDate(getTimeReceived()), getSender(), getTextMessage());
		else
			return String.format("[%s] <PM from %s> %s", getDate(getTimeReceived()), getRecipients()[0], getTextMessage());
    }

    @Override
    public Message copy(String[] newRecipients) {
        return new PrivateMessage(getSender(), newRecipients, getTextMessage(), getImage(), group);
    }
    
}
