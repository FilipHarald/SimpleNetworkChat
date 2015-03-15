package message;

/**
 * DataMessage is currently only used to send updated clientlists to clients
 * from server whenever someone connects or disconnects
 * 
 * @author Albert
 *
 */
public class DataMessage extends Message {
	
	private String[] data;
	
	public DataMessage(String[] recipients, String[] data) {
		super(null, recipients);
		this.data = data;
	}

	public String[] getData() {
		return data;
	}

	public Message copy(String[] newRecipients) {
		return new DataMessage(newRecipients, getData());
	}	

}
