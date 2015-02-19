package client.history;

import java.util.ArrayList;

import client.Message;

public class CSVImporter extends Importer {
	@Override
	public ArrayList<Message> importHistory(String toImport) {
		try {
			String[] lines = toImport.split("\n");
			ArrayList<Message> messages = new ArrayList<Message>();
			for(String line : lines) {
				String[] fields = line.split(";");
				messages.add(new Message(fields[2], fields[0], fields[1]));
			}
			return messages;
		} catch (Exception e) {
			return null;
		}
	}
}
