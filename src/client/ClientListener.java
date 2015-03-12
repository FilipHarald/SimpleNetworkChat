package client;

import message.Message;

public interface ClientListener {
    void onConnected(String host, int port);
    void onClientsUpdated(String[] clients);
    void onMessageReceived(Message message);
    void onDisconnected();
}
