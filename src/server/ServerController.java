package server;

import server.gui.ServerGUI;
import server.log.Log;
import server.log.LogListener;
import javax.swing.SwingUtilities;

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
		server.start();
		startListeningServer();
	}

	public void stopServer() {
		server.stopServer();
	}
	
	public void startListeningLog() {
		Log.addListener(new LogListener(){
			public void onInit() {
				sgui.appendText("[LOG] Started logging...\n");	
			}

			public void onWrite(String str) {
				sgui.appendText(str);
			}

			public void onWriteFailed(String str) {
				sgui.appendText(str);	
			}

			public void onClose() {
				sgui.appendText("[LOG] Stopped logging\n");
			}
		});
	}
	
	public void startListeningServer(){
		
		server.addListener(new ServerListener(){

			@Override
			public void onClientListUpdated(String[] clientList) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						sgui.updateClientList(clientList);
					}
				});
			}					
		});
	}
}
