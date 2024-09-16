package model;

import java.sql.Blob;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;

import glaciar.annotations.PenguinAttribute;
import glaciar.annotations.PenguinEntity;

@PenguinEntity(name="Users")
public class User 
{	
	@PenguinAttribute(penguinKey = true, autoIncrement = true)
	private int userId;
	private String name;
	private LocalDateTime registration_date;
	private String email;
	private String password;
	private byte[] profile_image;
	
	public User() {}
	public User(int userId, String name, LocalDateTime registration_date, String email, String password, byte[] profile_image) {
		super();
		this.userId = userId;
		this.name = name;
		this.registration_date = registration_date;
		this.email = email;
		this.password = password;
		this.profile_image = profile_image;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDateTime getRegistration_date() {
		return registration_date;
	}
	public void setRegistration_date(LocalDateTime registration_date) {
		this.registration_date = registration_date;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public byte[] getProfile_image() {
		return profile_image;
	}
	public void setProfile_image(byte[] profile_image) {
		this.profile_image = profile_image;
	}
	
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User {").append('\n')
          .append("  userId=").append(userId).append('\n')
          .append("  name='").append(name).append('\'').append('\n')
          .append("  registration_date=").append(registration_date).append('\n')
          .append("  email='").append(email).append('\'').append('\n')
          .append("  password='").append(password).append('\'').append('\n')
          //.append("  profile_image=").append(Arrays.toString(profile_image)).append('\n')
          .append('}');
        return sb.toString();
    }
	

}
