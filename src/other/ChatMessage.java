package other;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nekosaur
 */
public class ChatMessage extends Message {

    private String textMessage;
    private ImageIcon image;

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
        return image == null;
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - hh:mm:ss");
        return sdf.format(date);
    }

    public String toString() {
        return String.format("[%s] <%s> %s", getDate(getTimeReceived()), getSender(), getTextMessage());
    }

    @Override
    public Message copy(String[] newRecipients) {
        return new ChatMessage(getSender(), newRecipients, getTextMessage(), getImage());
    }
}
