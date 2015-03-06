package other;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Message implements Serializable {
	private String sender;
	private String [] recipients;
	private long timeReceived;
	private long timeDelivered;
	private String textMessage;
	private ImageIcon image;
	
	public Message(String sender, String[] recipients, String textMessage, ImageIcon image) {
		super();
		this.sender = sender;
		this.recipients = recipients;
		this.textMessage = textMessage;
		this.image = image;
		
	}

	public long getTimeReceived() {
		return timeReceived;
	}

	public void setTimeReceived(long timeReceived) {
		this.timeReceived = timeReceived;
	}

	public long getTimeDelivered() {
		return timeDelivered;
	}

	public void setTimeDelivered(long timeDelivered) {
		this.timeDelivered = timeDelivered;
	}

	public String getSender() {
		return sender;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public ImageIcon getImage() {
		return image;
	}
	
	public boolean hasImage() {
		return image != null;
	}
	
	public String toString() {
		return "[" + timeReceived + "] <" + sender + ">" + "textMessage";
	}
	

}
