package client;

import message.Message;

public interface ClientListener {
    void onClientsUpdated(String[] clients);
    void onMessageReceived(Message message);
    void onDisconnected();
}
