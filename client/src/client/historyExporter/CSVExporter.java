package client.historyExporter;

import java.util.ArrayList;

import client.Message;

public class CSVExporter extends Exporter {

	
	@Override
	public String export(ArrayList<Message> messages) {
		StringBuilder sb = new StringBuilder();
		for(Message message : messages) {
			sb.append(String.format("%s;%s;%s\n", dt.format(message.getCreated()), message.getFrom(), message.getMessage()));
		}
		return sb.toString();
	}

}
