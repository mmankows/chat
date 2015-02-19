package client.history;

import java.util.ArrayList;

import client.Message;


public abstract class ExporterDecorator extends Exporter {
	Exporter toDecorate;

	public ExporterDecorator(Exporter toDecorate) {
		this.toDecorate = toDecorate;
	}
	
	@Override
	public String export(ArrayList<Message> messages) {
		return toDecorate.export(messages);
	}
}
