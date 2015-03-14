package client;

import message.Message;

/**
 * Interface used by Client to notify listeners of events 
 * 
 * @author Albert & Henrik
 *
 */
public interface ClientListener {
	/**
	 * Fired when client list should be updated
	 * 
	 * @param clients Array of clients
	 */
    void onClientsUpdated(String[] clients);
    
    /**
     * Fired when a message has been received
     * 
     * @param message Message that was received
     */
    void onMessageReceived(Message message);
    
    /**
     * Fired when client has been disconnected from server
     */
    void onDisconnected();
}
