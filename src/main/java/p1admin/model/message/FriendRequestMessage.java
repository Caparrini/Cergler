package p1admin.model.message;

import java.sql.Timestamp;

import p1admin.model.User;

public class FriendRequestMessage extends Message{
    private boolean accepted;

    public FriendRequestMessage(Timestamp sent, boolean readStatus, User sender, User receiver) {
        super(sent, readStatus, sender, receiver);
    }

    public void accept(){
        this.accepted = true;
    }

    public void deny(){
        this.accepted = false;
    }

    public boolean isAccepted(){
        return accepted;
    }
}
