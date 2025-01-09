package suppp.project.socailMedia.dto;

import java.time.LocalTime;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String postUrl;
	private String caption;
	@Transient
	private MultipartFile image;
	@UpdateTimestamp
	private LocalTime time;

	@ManyToOne
	private User user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
