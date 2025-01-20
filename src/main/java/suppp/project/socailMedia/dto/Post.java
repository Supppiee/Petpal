package suppp.project.socailMedia.dto;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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

	@ManyToMany(fetch = FetchType.EAGER)
	List<User> likedUsers = new ArrayList<User>();
	
	public boolean hasLiked(int id) {
		for (User likedUser : likedUsers) {
			if (likedUser.getId() == id) {
				return true;
			}
		}
		return false;
	}
	

	public List<User> getLikedUsers() {
		return likedUsers;
	}


	public void setLikedUsers(List<User> likedUsers) {
		this.likedUsers = likedUsers;
	}


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
