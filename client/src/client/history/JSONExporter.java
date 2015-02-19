package client.history;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import client.Message;

public class JSONExporter extends Exporter {
	@Override
	public String export(ArrayList<Message> messages) {
		JSONArray json = new JSONArray();
		for (Message message : messages) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.accumulate("date", dt.format(message.getCreated()));
			jsonObj.accumulate("from", message.getFrom());
			jsonObj.accumulate("message", message.getMessage());
			json.put(jsonObj);
		}
		return json.toString();
	}

}
