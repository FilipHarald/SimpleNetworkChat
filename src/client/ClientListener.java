package client;

import messages.Message;

public interface ClientListener {
    void onConnected();
    void onClientsUpdated(String[] clients);
    void onMessageReceived(Message message);
    void onDisconnected();
}
