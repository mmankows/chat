package client.history;

import java.util.ArrayList;

import client.Message;

public class BlueStyleDecorator extends ExporterDecorator {

	public BlueStyleDecorator(Exporter toDecorate) {
		super(toDecorate);
	}

	public String export(ArrayList<Message> messages) {
		String oldHTML = toDecorate.export(messages);
		int index;
		if ((index = oldHTML.indexOf("<head>")) == -1)
			return oldHTML;
		String styles = "body {background-color: #bbbbbb} h3 {background-color: #888888; color: blue;} h3 span {color: #00aaDD; font-size: 0.8em; font-style: italic} p {font-style: italic; border: groove; background-color: dddddd; border-radius: 10px;}";
		return String.format("%s<style>%s</style>%s",
				oldHTML.substring(0, index + 6), styles,
				oldHTML.substring(index + 6));
	}

}
