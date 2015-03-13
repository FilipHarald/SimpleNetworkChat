package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.event.*;

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
	private boolean running;
	
	public ServerController(){
		this.sgui = new ServerGUI(this);
		
		startListeningLog();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.add(sgui);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				frame.setVisible(true);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						stopServer();
					}
				});
			}
		});
	}
	
	public void startServer(int port) {
		try {			
			server = new Server(port);
			server.start();
			startListeningServer();
			running = true;
		} catch (IOException e) {
			// Do nothing, error gets written to log
		}
	}

	public void stopServer() {
		running = false;
		if (server != null) {
			server.stopServer();
		}
	}

	public boolean isRunning() {
		return running;
	}
	
	public String getIP(){
		InetAddress localIp;
		try {
			localIp = InetAddress.getLocalHost();
			return localIp.getHostAddress().toString();
		} catch (UnknownHostException e) {
			return "UNKNOWN";
		}
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
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								if (running) {
									sgui.updateClientList(clientList);
								}
							}
						});
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
