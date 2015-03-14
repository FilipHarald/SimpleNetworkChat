package message;

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
