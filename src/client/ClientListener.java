package client;

import message.Message;

public interface ClientListener {
    void onConnected();
    void onClientsUpdated(String[] clients);
    void onMessageReceived(Message message);
    void onDisconnected();
}
