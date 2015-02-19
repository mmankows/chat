package client.history;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import client.Message;

public class JSONImporter extends Importer {
	@Override
	public ArrayList<Message> importHistory(String toImport) {
		try {
			JSONArray json = new JSONArray(toImport);
			String created, from, message;
			ArrayList<Message> messages = new ArrayList<Message>();
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObj = json.getJSONObject(i);
				created = jsonObj.getString("date");
				message = jsonObj.getString("message");
				from = jsonObj.getString("from");
				messages.add(new Message(message, created, from));
			}
			return messages;
		} catch (Exception e) {
			return null;
		}
	}
}
