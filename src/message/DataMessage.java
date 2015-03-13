package message;

public class DataMessage extends Message {
	
	private Object data;
	
	public DataMessage(String[] recipients, Object data) {
		super(null, recipients);
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public Message copy(String[] newRecipients) {
		return new DataMessage(newRecipients, getData());
	}	

}
