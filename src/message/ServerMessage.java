package message;

/**
 * ServerMessage is a simple wrapper for ChatMessage to make it easier to create
 * server broadcast messages.
 * 
 * @author Albert
 *
 */
public class ServerMessage extends ChatMessage {

	private static final long serialVersionUID = 6105841349713205296L;

	public ServerMessage(String[] recipients, String textMessage) {
        super("SERVER", recipients, textMessage, null);
    }

    public Message copy(String[] newRecipients) {
        return new ServerMessage(newRecipients, getTextMessage());
    }
}
