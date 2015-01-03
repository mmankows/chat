package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App implements MObserver{
	private static Logger logger = Logger.getLogger("CLIENT LOGGER");
	static Client chatClient = new Client();
	
	public void run(String host, int port) throws InterruptedException, IOException {
		chatClient.addObserwer(this);
		logger.setLevel(Level.OFF);
		
		
		if(!chatClient.connect(host, port)) {
			return;
		}
		
		logger.info("AUTHENTICATION");
		chatClient.sendMessage("user1 haslo1");
		
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Users list: \\list\nSend Message: \\msg usernames(';' separated) message\nExit: vim style :)\n\n");
		
		while(true) {
			System.out.print("> ");
			System.out.flush();
			String command = console.readLine();
			logger.fine(String.format("TOOK COMMAND '%s'", command));
			handleCommand(command);
		}
	}
	
	public void handleCommand(String command) {
		if(command.equals(":q")) {
			System.out.println("Logout");
			System.exit(0);
		} else if(command.equals("\\list")) {
			printUsersList();
		} else if(command.substring(0, 4).equals("\\msg")) {
			handleMsgRequest(command);
		} else {
			System.out.printf("UNKNOWN COMMAND '%s'\n", command);
		}
	}
	
	private void printUsersList() {
		ArrayList<User> list = chatClient.getUserList();
		StringBuilder usersString = new StringBuilder();
		for (User user : list) {
			usersString.append(user.getNick() + '\n');
			
		}
		System.out.println(usersString.toString().trim());
	}
	
	private int getUserUid(String name) {
		ArrayList<User> list = chatClient.getUserList();
		for (User user : list) {
			if(user.getNick().equals(name)) {
				return user.getUid();
			}
		}
		return -1;
	}
	
	private String getUserNick(int uid) {
		ArrayList<User> list = chatClient.getUserList();
		for (User user : list) {
			if(user.getUid() == uid) {
				return user.getNick();
			}
		}
		return "UNKNOWN";
	}
	
	private void handleMsgRequest(String command) {
		String[] cmd = command.trim().split(" ");
		if(cmd.length != 3) {
			System.out.println("Wrong command format");
			return;
		}
		String[] userList = cmd[1].split(";");
		String message = cmd[2];
		
		ArrayList<Integer> uids = new ArrayList<Integer>();
		int uid;
		for (int i = 0; i < userList.length; i++) {
			if((uid = getUserUid(userList[i])) == -1) {
				logger.info(String.format("Unknown user '%s'\n", userList[i]));
			} else {
				uids.add(uid);
			}
		}
		System.out.println("SENDING MESSAGE TO: " + uids.toString());
		Message messageObj = new Message(message, uids);
		chatClient.sendMessage(messageObj.JSONEncode());
	}

	@Override
	public void update(Object message) {
		ArrayList<Integer> uids = ((Message) message).getUids();
		String content = ((Message) message).getMessage();
		StringBuilder uidsString = new StringBuilder();
		for (Integer uid : uids) {
			uidsString.append(getUserNick(uid) + ' ');
		}
		
		System.out.println("To: " + uidsString.toString());
		System.out.println("Message:\n" + content);
	}
}
