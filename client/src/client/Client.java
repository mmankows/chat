package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class Client implements MObservable {
	private Logger logger = Logger.getLogger("CLIENT LOGGER");

	private Socket socket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private Thread serverListenerThread = null;
	private ArrayList<User> userList = new ArrayList<User>();
	private Message lastMessage = null;
	private int lastCode;

	public String[] getUserList() {
		String[] usersListString = new String[userList.size()];
		for (int i = 0; i < usersListString.length; i++) {
			usersListString[i] = userList.get(i).getNick();
		}
		return usersListString;
	}

	private class ServerListener implements Runnable {
		@Override
		public void run() {
			logger.setLevel(Level.ALL);
			while (true) {
				logger.info("Listening for message");
				String message = read();
				logger.info("Received message");
				handleMessage(message);
				notifyObservers();
				// System.out.println("MSG!!: " + message);
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
				String action = json.getString("action");
				JSONObject userItem = json.getJSONArray("nick_uid").getJSONObject(0);
				String nick = (String) userItem.keySet().toArray()[0];
				int uid = userItem.getInt(nick);
				switch(action) {
				case "add":
					userList.add(new User(nick, uid)); 
					break;
				case "del":
					for (int i = 0; i < userList.size(); i++) {
						User user = userList.get(i);
						if(user.getUid() == uid && user.getNick().equals(nick)) {
							userList.remove(i);
							break;
						}
					}
					break;
				}
//				for (int i = 0; i < uids.length(); i++) {
//					JSONObject item = uids.getJSONObject(i);
//					String nick = (String) item.keySet().toArray()[0];
//					int uid = item.getInt(nick);
//					userList.add(new User(nick, uid));
//				}
				lastCode = MObservableNotification.CODE_REFRESHLIST;
				break;
			case 1:
				lastCode = MObservableNotification.CODE_MESSAGE;
				Message messageObject = new Message();
				messageObject.JSONDecode(msg);
				lastMessage = messageObject;
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

	public Boolean login(String username, String password) {
		send(String.format("%s %s", username, password));
		return true;
	}

	public void sendMessage(String username, String message) {
		sendMessage(new String[] { username }, message);
	}

	public void sendMessage(String[] usernames, String message) {
		ArrayList<Integer> usersUids = new ArrayList<Integer>();
		int uid;
		for (String username : usernames) {
			if ((uid = getUserUid(username)) != -1) {
				usersUids.add(uid);
			}
		}

		if (usersUids.size() != 0) {
			Message messageObj = new Message(message, usersUids);
			send(messageObj.JSONEncode());
		}
	}

	private void send(String message) {
		logger.info("Sending message");
		try {
			System.out.println("WYSYLAM: " + message);
			outStream.write(message.getBytes());
			outStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getUserUid(String username) {
		for (User user : userList) {
			if (user.getNick().equals(username)) {
				return user.getUid();
			}
		}
		return -1;
	}

	private ArrayList<MObserver> observers = new ArrayList<MObserver>();

	@Override
	public void addObserwer(MObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(MObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers() {
		for (MObserver observer : observers) {
			observer.update(new MObservableNotification(lastCode, lastMessage));
		}
	}
}
