package message;

import java.io.Serializable;

/**
 * Base class for handling messages sent between client and server. A message has a sender,
 * and an array of recipients, a time it is received on the server, and a time when the server
 * has delivered the message to the recipients.
 * @author ?
 *
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 4181292796261154482L;
	
	private String sender;
    private String[] recipients;
    private long timeReceived;
    private long timeDelivered;

    public Message(String sender, String[] recipients) {
        this.sender = sender;
        this.recipients = recipients;
    }

    public String getSender() {
        return sender;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public long getTimeReceived() {
        return timeReceived;
    }

    public long getTimeDelivered() {
        return timeDelivered;
    }

    public void setTimeReceived(long timeReceived) {
        this.timeReceived = timeReceived;
    }

    public void setTimeDelivered(long timeDelivered) {
        this.timeDelivered = timeDelivered;
    }

    public Message copy(String[] newRecipients) {
        return new Message(getSender(), newRecipients);
    }

}
