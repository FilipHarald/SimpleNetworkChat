package server;

import server.gui.ServerGUI;
import server.log.Log;
import server.log.LogListener;

/**
 * 
 * @author Andreas
 *
 */
public class ServerController {
	private ServerGUI sgui;
	private Server server;
	
	public ServerController(ServerGUI sgui){
		this.sgui = sgui;
	}
	
	public void startServer(int port) {
		startListeningLog();
		this.server = new Server(port);
		startListeningServer();
	}
	
	public void startListeningLog() {
		Log.addListener(new LogListener(){
			public void onInit() {
				sgui.appendText("Log initiated");	
			}

			public void onWrite(String str) {
				sgui.appendText("Log initiated");
			}

			public void onWriteFailed(String str) {
				sgui.appendText("Log initiated");	
			}

			public void onClose() {
				sgui.appendText("Log closed");
			}
		});
	}
	
	public void startListeningServer(){
		
		server.addListener(new ServerListener(){
			@Override
			public void onStop() {
				sgui.appendText("Server stopped");	
			}
			@Override
			public void onClientListUpdated(String[] clientList) {
				sgui.updateClientList(clientList);
			}	
		});
	}
}
