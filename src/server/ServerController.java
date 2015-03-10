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
	private Server server;
	private ServerGUI sgui;
	
	public ServerController(ServerGUI sgui, int port){
		server = new Server(port);
		this.sgui = sgui;
		startListening();
	}
	
	public void startListening(){
		
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
		
		Log.addListener(new LogListener(){
			@Override
			public void onInit() {
				sgui.appendText("Log initiated");	
			}
			@Override
			public void onWrite(String str) {
				sgui.appendText("Log initiated");
			}
			@Override
			public void onWriteFailed(String str) {
				sgui.appendText("Log initiated");	
			}
			@Override
			public void onClose() {
				sgui.appendText("Log closed");
			}
		});
	}
}
