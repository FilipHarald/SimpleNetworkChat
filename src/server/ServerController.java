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
 * This class handles communication between server and serverGui. 
 * This is the starting class for the server.
 * @author Andreas
 *
 */
public class ServerController {
	private ServerGUI sgui;
	private Server server;
	private boolean running;
	
	/**
	 * Constructor who initiates a ServerGui
	 */
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
	
	/**
	 * This method starts the server and takes an int as parameter
	 * @param port the port the server uses
	 */
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

	/**
	 * Method for stopping the server
	 */
	public void stopServer() {
		running = false;
		if (server != null) {
			server.stopServer();
		}
	}

	/**
	 * Method for checking if server is running
	 * @return	if server is running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Method for getting the servers IP-address.
	 * @return servers IP-Address
	 */
	public String getIP(){
		InetAddress localIp;
		try {
			localIp = InetAddress.getLocalHost();
			return localIp.getHostAddress();
		} catch (UnknownHostException e) {
			return "UNKNOWN";
		}
	}
	
	/**
	 * Listening method for the Log
	 */
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
	
	/**
	 * Listening method for the server
	 */
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
