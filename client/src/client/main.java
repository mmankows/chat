package client;

import java.io.IOException;

public class main {
	public static void main(String[] args) throws NumberFormatException, InterruptedException, IOException {
		if(args.length < 2) {
			System.out.println("No host and port specified.");
			return;
		}
		App app = new App();
		app.run(args[0], Integer.parseInt(args[1]));
	}
}
