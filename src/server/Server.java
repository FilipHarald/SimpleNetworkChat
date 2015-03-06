package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

/**
 * 
 * @author Filip & Jimmy
 *
 */
public class Server extends Observable implements Runnable {
	private ServerSocket serverSocket;

	// private Log log;

	public Server(int port, Log log) {
		// this.log = log;
		// För tillfället så är log observer till servern och därför behöver
		// inte servern ha referens till loggen.
//		addObserver(log);
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
//		notifyObservers("Server is running");
		while(true){
			try{
				Socket socket = serverSocket.accept();
				System.out.println("Client connected");
				new ClientHandler(socket, this).start();
			}catch (IOException e){
//				notifyObservers(e);
				e.printStackTrace();
			}
		}
	}

}
