package p1admin.model;
import java.io.File;
import java.util.Date;
import java.util.ArrayList;

import p1admin.model.message.Message;

public class User {
	//Clase usuario de la aplicación
	
	private enum Gender{
		MALE,
		FEMALE
	}
	
	private enum OtherGender{

		MALE,
		FEMALE,
		BOTH
	}
	
	
	private String email;
	private String password;
	private String name;
	
	private Gender gender;
	private OtherGender otherGender;
	
	private Date birthDate;
	private String description;
	private Coordinates coordinates;
	
	private ArrayList<String> interests;
	private ArrayList<User> friends;
	
	private ArrayList<Message> received;
	private ArrayList<Message> sended;

	
	private File profileImage;

	
	//TODO Array de mensajes
	
	
	public User(String email,String password){
		this.email = email;
		this.password = password;
	}
	
	
	
	

}
