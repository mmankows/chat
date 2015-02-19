package client.historyExporter;

import java.util.ArrayList;

import client.Message;


public class BlueStyleDecorator extends ExporterDecorator {

	public BlueStyleDecorator(Exporter toDecorate) {
		super(toDecorate);
		// TODO Auto-generated constructor stub
	}
	
	public String export(ArrayList<Message> messages) {
		String oldHTML = toDecorate.export(messages);
		int index;
		if((index = oldHTML.indexOf("<head>")) == -1)
			return oldHTML;
		String styles = "h1 { color: blue }";
		return String.format("%s<style>%s</style>%s", oldHTML.substring(0, index + 6),styles, oldHTML.substring(index+6));		
	}

}
