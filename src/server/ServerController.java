package server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import server.gui.ServerGUI;
import server.log.Log;
import server.log.LogListener;

import javax.swing.*;

/**
 * 
 * @author Andreas
 *
 */
public class ServerController {
	private ServerGUI sgui;
	private Server server;
	
	public ServerController(){
		this.sgui = new ServerGUI(this);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(sgui);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
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
	
	public String getIP(){
		InetAddress localIp;
		try {
			localIp = InetAddress.getLocalHost();
			return localIp.getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Your local IP is not available";
		
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
			
			public void onStop() {
				
			}
		});
	}

	public static void main(String[] args) {
		ServerController controller = new ServerController();
	}
}
