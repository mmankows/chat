package client;

public class MObservableNotification {
	public final int code;
	public final Object message;
	
	final static int CODE_REFRESHLIST = 2;
	final static int CODE_MESSAGE = 1;
	
	public int getCode() {
		return code;
	}
	
	public Object getMessage() {
		return message;
	}
	
	public MObservableNotification(int code, Object message) {
		this.code = code;
		this.message = message;
	}
}
