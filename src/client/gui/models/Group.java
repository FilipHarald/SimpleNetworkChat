package client.gui.models;

public class Group {

	private String name;
	private String[] users;
	private int tabIndex;
	
	public Group(String name, String[] users) {
		this(name, users, -1);
	}
	
	public Group(String name, String[] users, int tabIndex) {
		this.name = name;
		this.users = users;
		this.tabIndex = tabIndex;
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
