package message;

import java.io.Serializable;

public class Message implements Serializable {
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
