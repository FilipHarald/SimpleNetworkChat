package server;

import server.gui.ServerGUI;

/**
 * 
 * @author Andreas
 *
 */
public class ServerController {
	private Server server;
	private ServerGUI sgui;
	
	
	
	public void addListener(){
		server.addListener(new ServerListener(){

			@Override
			public void onClientAdded() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub
				
			}
			
		});
		Log.addListener(new LogListener(){

			@Override
			public void onInit() {
				sgui.appendText("Log initiated");
				
			}

			@Override
			public void onWrite(String str) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onWriteFailed(String str) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onClose() {
				sgui.appendText("Log closed");
				
			}
			
		});
	}
}
