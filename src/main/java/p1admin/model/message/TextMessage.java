package p1admin.model.message;

import java.sql.Timestamp;

import p1admin.model.User;

public class TextMessage extends Message{
	
	public String content;

	public TextMessage(Timestamp sent, boolean readStatus, User sender,
			User receiver, String content) {
		super(sent, readStatus, sender, receiver);
		this.content = content;
	}

}
