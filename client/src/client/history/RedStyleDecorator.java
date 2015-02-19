package client.history;

import java.util.ArrayList;

import client.Message;


public class RedStyleDecorator extends ExporterDecorator {

	public RedStyleDecorator(Exporter toDecorate) {
		super(toDecorate);
		// TODO Auto-generated constructor stub
	}
	
	public String export(ArrayList<Message> messages) {
		String oldHTML = toDecorate.export(messages);
		int index;
		if((index = oldHTML.indexOf("<head>")) == -1)
			return oldHTML;
		String styles = "h1 { color: red }";
		return String.format("%s<style>%s</style>%s", oldHTML.substring(0, index + 6),styles, oldHTML.substring(index+6));		
	}

}
