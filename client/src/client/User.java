package client;

public class User {
	private String nick;
	private int uid;
	
	User(String nick, int uid) {
		this.nick = nick;
		this.uid = uid;
	}
	
	public String getNick() {
		return nick;
	}
	
	public int getUid() {
		return uid;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%d", nick, uid);
	}
}
