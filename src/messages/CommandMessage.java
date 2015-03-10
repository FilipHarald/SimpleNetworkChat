package messages;

/**
 * @author nekosaur
 */
public class CommandMessage extends Message {

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

    /*
    public Message copy(String[] newRecipients) {
        return new CommandMessage(getSender(), newRecipients, getCommand(), getArguments());
    }*/

}
