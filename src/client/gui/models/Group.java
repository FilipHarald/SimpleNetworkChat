package client.gui.models;

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
