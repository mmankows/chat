import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import client.Client;
import client.User;


public class app {
	private static Logger logger = Logger.getLogger("CLIENT LOGGER");
	static Client chatClient = new Client();
	
	public static void main(String[] args) throws InterruptedException, IOException {
		logger.setLevel(Level.OFF);
		if(args.length < 2) {
			System.out.println("No host and port specified.");
			return;
		}
		
		if(!chatClient.connect(args[0], Integer.parseInt(args[1]))) {
			return;
		}
		
		logger.info("AUTHENTICATION");
		chatClient.sendMessage("user1 haslo1");
		
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Users list: \\list\nSend Message: \\msg username message\nExit: vim style :)\n\n");
		
		while(true) {
			System.out.print("> ");
			System.out.flush();
			String command = console.readLine();
			logger.fine(String.format("TOOK COMMAND '%s'", command));
			handleCommand(command);
		}
	}
	
	public static void handleCommand(String command) {
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
	
	private static void printUsersList() {
		ArrayList<User> list = chatClient.getUserList();
		StringBuilder usersString = new StringBuilder();
		for (User user : list) {
			usersString.append(user.getNick() + '\n');
			
		}
		System.out.println(usersString.toString().trim());
	}
	
	private static int getUserUid(String name) {
		ArrayList<User> list = chatClient.getUserList();
		for (User user : list) {
			if(user.getNick().equals(name)) {
				return user.getUid();
			}
		}
		return -1;
	}
	
	private static void handleMsgRequest(String command) {
		String[] cmd = command.trim().split(" ");
		if(cmd.length != 3) {
			System.out.println("Wrong command format");
			return;
		}
		String user = cmd[1];
		String message = cmd[2];
		int uid;
		if((uid = getUserUid(user)) == -1) {
			System.out.printf("Unknown user '%s'\n", user);
			return;
		}
		JSONObject json = new JSONObject();
		int[] uids = new int[1];
		uids[0] = uid;
		json.accumulate("type", 1);
		json.accumulate("to_id", uids);
		json.accumulate("content", message);
		System.out.println(json.toString());
		chatClient.sendMessage(json.toString());
	}
}
