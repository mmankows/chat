package client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Message implements JSONSerializable {
	ArrayList<Integer> uids = new ArrayList<Integer>();
	String message = null;

	public Message() {
		
	}
	public Message(String message, ArrayList<Integer> uids) {
		this.message = message;
		this.uids = new ArrayList<Integer>(uids);
	}
	
	@Override
	public void JSONDecode(String json) {
		JSONObject jsonOBJ = new JSONObject(json);
		message = jsonOBJ.getString("content");
		uids = new ArrayList<Integer>();
		JSONArray jsonUids = jsonOBJ.getJSONArray("to_id");
		for (int i = 0; i < jsonUids.length(); i++) {
			uids.add(jsonUids.getInt(i));
		}
	}

	@Override
	public String JSONEncode() {
		JSONObject json = new JSONObject();
		json.accumulate("type", 1);
		json.accumulate("to_id", uids);
		json.accumulate("content", message);
		return json.toString();
	}
	
	public String getMessage() {
		return message;
	}
	
	public ArrayList<Integer> getUids() {
		return uids;
	}
}
