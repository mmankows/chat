package client.history;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import client.Message;

public abstract class Exporter {
	SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	abstract public String export(ArrayList<Message> messages);
}
