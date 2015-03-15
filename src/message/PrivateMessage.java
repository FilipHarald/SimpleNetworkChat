package message;

import javax.swing.*;

/**
 * PrivateMessage is a ChatMessage with added functionality to handle private messages
 * between clients.
 * 
 * @author Albert
 *
 */
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
			return String.format("[%s] <%s> %s", getDate(getTimeReceived()), getSender(), getTextMessage());
		else
			return String.format("[%s] <PM from %s> %s", getDate(getTimeReceived()), getSender(), getTextMessage());
    }

    @Override
    public Message copy(String[] newRecipients) {
        return new PrivateMessage(getSender(), newRecipients, getTextMessage(), getImage(), getGroup());
    }
    
}
