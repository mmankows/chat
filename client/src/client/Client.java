package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class Client implements MObservable {
	private Logger logger = Logger.getLogger("CLIENT LOGGER");

	private Socket socket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private Thread serverListenerThread = null;
	private ArrayList<User> userList = new ArrayList<User>();
	private ArrayList<Message> messageList = new ArrayList<Message>();
	private int lastCode;

	public String[] getUserList() {
		String[] usersListString = new String[userList.size()];
		for (int i = 0; i < usersListString.length; i++) {
			usersListString[i] = userList.get(i).getNick();
		}
		return usersListString;
	}
	
	public ArrayList<Message> getMessageList() {
		return messageList;
	}
	
	public String getUserName(int uid) {
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if(user.getUid() == uid) {
				return user.getNick();
			}
		}
		return null;
	}

	private class ServerListener implements Runnable {
		@Override
		public void run() {
			logger.setLevel(Level.ALL);
			while (true) {
				try {
					logger.info("Listening for message");
					String message = read();
					logger.info("Received message");
					logger.info(message);
					handleMessage(message);
					notifyObservers();
				} catch(Exception e) {
					e.printStackTrace();
					lastCode = MObservableNotification.CODE_CONNECTION_LOST;
					notifyObservers();
					Thread.currentThread().interrupt();
					return;
				}
			}
		}

		private String read() {
			byte[] buffer = new byte[5000];
			try {
				inStream.read(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new String(buffer);
		}

		void handleMessage(String msg) {
			JSONObject json;
			json = new JSONObject(msg);
			
			if(json.has("status") && json.getString("status").equals("authentication_problem")) {
				logger.info("Auth failed");
				lastCode = MObservableNotification.CODE_UNAUTHORIZED;
				return;
			}
			int type = json.getInt("type");
			logger.info("Message type: " + type);
			switch (type) {
			case 2:
				String action = json.getString("action");
				JSONArray usersItems = json.getJSONArray("nick_uid");
				//String nick = (String) userItem.keySet().toArray()[0];
				//int uid = userItem.getInt(nick);
				switch(action) {
				case "add":
					for (int i = 0 ; i < usersItems.length() ; i++) {
						JSONObject obj = usersItems.getJSONObject(i);
						int uid = obj.getInt("uid");
						String nick = obj.getString("nick");
						userList.add(new User(nick, uid)); 
					}
					break;
				case "del":
					JSONObject obj = usersItems.getJSONObject(0);
					int uid = obj.getInt("uid");
					String nick = obj.getString("nick");
					userList.add(new User(nick, uid));
					for (int i = 0; i < userList.size(); i++) {
						User user = userList.get(i);
						if(user.getUid() == uid && user.getNick().equals(nick)) {
							//System.out.println("REMOVE");
							userList.remove(i);
							break;
						}
					}
					break;
				}
				lastCode = MObservableNotification.CODE_REFRESHLIST;
				break;
			case 1:
				lastCode = MObservableNotification.CODE_MESSAGE;
				Message messageObject = new Message();
				messageObject.JSONDecode(msg, userList);
				messageList.add(messageObject);
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
			//System.out.println("WYSYLAM: " + message);
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
			
			observer.update(new MObservableNotification(lastCode, messageList.isEmpty() ? null : messageList.get(messageList.size() - 1)));
		}
	}
}
