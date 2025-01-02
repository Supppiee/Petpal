package suppp.project.socailMedia.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class User {
	
	@Id
	@GeneratedValue
	private int id;
	@Size(min=3, max=15, message="First name should be between 3 to 15 characters")
	private String firstName;
	@Size(min=1, max=10, message="Last name should be between 1 to 10 characters")
	private String lastName;
	@Size(min=3, max=15, message="Username should be between 3 to 15 characters")
	private String userName;
	@Email
	private String email;
	@DecimalMin(value = "6000000000", message = "It should be proper mobile number")
	@DecimalMax(value = "9999999999", message = "It should be proper mobile number")
	private long mobile;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
			message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	private String password;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
			message = "It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
//	@Transient
	private String confirmPassword;
	@NotNull
	private String gender;
	private int otp;
	private boolean verified;
	
	
	
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public int getOtp() {
		return otp;
	}
	public void setOtp(int otp2) {
		this.otp = otp2;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getMobile() {
		return mobile;
	}
	public void setMobile(long mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
}
