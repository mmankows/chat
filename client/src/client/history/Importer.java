package client.history;

import java.util.ArrayList;

import client.Message;

public abstract class Importer {
	Importer next = null;
	
	public void setNext(Importer next) {
		this.next = next;
	}
	
	public ArrayList<Message> process(String toProcess) {
		ArrayList<Message> result = importHistory(toProcess);
		return result != null ? result : next.process(toProcess);
	}
	
	abstract ArrayList<Message> importHistory(String toImport);
}
