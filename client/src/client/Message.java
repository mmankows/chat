package client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message {
	ArrayList<Integer> to = new ArrayList<Integer>();
	String from = null;
	String message = null;
	Date created = new Date();
	SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public Message() {

	}

	public Message(String message, String created, String from)
			throws ParseException {
		this.message = message;
		this.created = dt.parse(created);
		this.from = from;
	}

	public Message(String message, ArrayList<Integer> uids) {
		this.message = message;
		this.to = new ArrayList<Integer>(uids);
	}

	public void JSONDecode(String json, ArrayList<User> userList) {
		JSONObject jsonOBJ = new JSONObject(json);
		message = jsonOBJ.getString("content");

		JSONArray jsonUids = jsonOBJ.getJSONArray("to_id");
		for (int i = 0; i < jsonUids.length(); i++) {
			to.add(jsonUids.getInt(i));
		}

		int fromUID = jsonOBJ.getInt("from_id");
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.getUid() == fromUID) {
				from = user.getNick();
				return;
			}
		}
	}

	public String JSONEncode() {
		JSONObject json = new JSONObject();
		json.accumulate("type", 1);
		json.accumulate("to_id", to);
		json.accumulate("content", message);
		return json.toString();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreated() {
		return created;
	}

	public ArrayList<Integer> getTo() {
		return to;
	}
}
