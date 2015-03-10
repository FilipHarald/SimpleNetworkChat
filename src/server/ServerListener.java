package server;

public interface ServerListener {
	void onClientAdded();
	void onStart();
	void onStop();
}
