package messages;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nekosaur
 */
public class ChatMessage extends Message {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    private String textMessage = "";
    private ImageIcon image = null;

    public ChatMessage(String sender, String[] recipients, String textMessage, ImageIcon image) {
        super(sender, recipients);
        this.textMessage = textMessage;
        this.image = image;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public ImageIcon getImage() {
        return image;
    }

    public boolean hasImage() {
        return (image != null);
    }

    protected String getDate(long time) {
        return SIMPLE_DATE_FORMAT.format(new Date(time));
    }

    public String toString() {
        return String.format("[%s] <%s> %s", getDate(getTimeReceived()), getSender(), getTextMessage());
    }

    @Override
    public Message copy(String[] newRecipients) {
        return new ChatMessage(getSender(), newRecipients, getTextMessage(), getImage());
    }
}
