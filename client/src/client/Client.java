package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;

public class Client {
	private Logger logger = Logger.getLogger("CLIENT LOGGER");
	
	private Socket socket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private Thread serverListenerThread = null;
	private ArrayList<User> userList = new ArrayList<User>();
	
	public ArrayList<User> getUserList() {
		return userList;
	}
	
	private class ServerListener implements Runnable {
		@Override
		public void run() {
			logger.setLevel(Level.OFF);
			byte[] buffer = new byte[1024];
			while(true) {
				logger.info("Listening for message");
				String message = read();
				logger.info("Received message");
				handleMessage(message);
				System.out.println("MSG: " + message);
			}
		}
		
		private String read() {
			byte[] buffer = new byte[1024];
			try {
				inStream.read(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new String(buffer);
		}
		
		void handleMessage(String msg) {
			JSONObject json = new JSONObject(msg);
			int type = json.getInt("type");
			logger.info("Message type: " + type);
			switch (type) {
			case 2:
				JSONArray uids = json.getJSONArray("nick_uid");
				for(int i = 0 ; i < uids.length() ; i++) {
					JSONObject item = uids.getJSONObject(i);
					String nick = (String) item.keySet().toArray()[0];
					int uid = item.getInt(nick);
					userList.add(new User(nick, uid));
				}
				break;
			default:
				logger.warning("Unknown message type " + type);
			}
		}
	}
	
	public boolean connect(String host, int port) {
		System.out.printf("Initializing connection to %s:%d... ", host, port);
		try {
			socket = new Socket(host, port);
			outStream = socket.getOutputStream();
			inStream = socket.getInputStream();
		} catch (IOException e) {
			System.out.println("FAILED");
			return false;
		}
		System.out.println("OK");
		serverListenerThread = new Thread(new ServerListener());
		serverListenerThread.start();
		return true;
	}
	
	public void sendMessage(String message) {
		logger.info("Sending message");
		try {
			outStream.write(message.getBytes());
			outStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
