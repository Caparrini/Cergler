package p1admin.model.message;

import java.sql.Timestamp;

import p1admin.model.Pregunta;
import p1admin.model.User;

public class QuestionRequestMessage extends Message{
	
	private Pregunta question;

	public QuestionRequestMessage(Timestamp sent, boolean readStatus,
			User sender, User receiver, Pregunta question) {
		super(sent, readStatus, sender, receiver);
		this.question = question;
	}
	

}
