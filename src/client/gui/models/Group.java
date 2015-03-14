package client.gui.models;

/**
 * Model used in Client GUI to represent a user created group of clients
 * to send private messages to
 * 
 * @author Albert
 *
 */
public class Group {

	private String name;
	private String[] users;
	
	public Group(String name, String[] users) {
		this.name = name;
		this.users = users;
	}
	
	public String[] getUsers() {
		return users;
	}
	
	public void setUsers(String[] users) {
		this.users = users;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getName();
	}
}
