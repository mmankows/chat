package client.history;

import java.util.ArrayList;

import client.Message;


public class RedStyleDecorator extends ExporterDecorator {

	public RedStyleDecorator(Exporter toDecorate) {
		super(toDecorate);
	}
	
	public String export(ArrayList<Message> messages) {
		String oldHTML = toDecorate.export(messages);
		int index;
		if((index = oldHTML.indexOf("<head>")) == -1)
			return oldHTML;
		String styles = "body {background-color: grey} h3 {background-color: #ff0000;} h3 span {color: #555; font-size: 0.8em;} p { color: white; font-weight: bold;}";
		return String.format("%s<style>%s</style>%s", oldHTML.substring(0, index + 6),styles, oldHTML.substring(index+6));		
	}

}
