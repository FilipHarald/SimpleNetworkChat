package server;

public interface ServerListener {
	void onStop();
	void onClientListUpdated(String [] clientList);
}
