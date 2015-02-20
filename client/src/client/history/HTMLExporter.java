package client.history;

import java.util.ArrayList;

import client.Message;

public class HTMLExporter extends Exporter {
	@Override
	public String export(ArrayList<Message> messages) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n<head>\n</head>\n<body>\n");
		for (Message message : messages) {
			sb.append(String.format("<h3>%s<span>[%s]</span></h3>\n<p>%s</p>\n",
					message.getFrom(), dt.format(message.getCreated()),
					message.getMessage()));
		}
		sb.append("</body>\n</html>\n");
		return sb.toString();
	}
}
