package other;

public class ServerMessage extends Message {
	
	public ServerMessage(String[] recipients, String string){
		super("SERVER", recipients, string);
	}
}
