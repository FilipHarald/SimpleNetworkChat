package client;

import other.Message;
import client.gui.*;

/**
 * 
 * @author Andreas
 *
 */
public class ClientController {
	
	private ClientGUI cgui;
	private Client client;
	
	public ClientController() {
		cgui = new ClientGUI(this);
	}
	
	public void setClient(Client client) {
		this.client = client;
		client.addListener(new ClientListener() {
			public void onConnected() {
				
			}

			public void onClientsUpdated(String[] clients) {
				cgui.setUsers(clients);
			}

			public void onMessageReceived(Message message) {
				cgui.append(message);
			}

			public void onDisconnected() {
				
			}
		});
	}
	
	public void sendMessage() {
		
	}
}
