package message;

/**
 * CommandMessage is used when clients send command instructions to
 * the server. These currently include /message, /msg, /whois, /kick
 * 
 * @author Albert
 */
public class CommandMessage extends Message {

	private static final long serialVersionUID = 5743122357918154904L;
	
	private String command;
    private String arguments;

    public CommandMessage(String sender, String command, String arguments) {
        super(sender, null);
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }
    
    public String toString() {
    	return String.format("%s %s", command, arguments);
    }

}
