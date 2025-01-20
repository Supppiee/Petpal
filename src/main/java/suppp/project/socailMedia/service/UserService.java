package suppp.project.socailMedia.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import suppp.project.socailMedia.dto.Post;
import suppp.project.socailMedia.dto.User;
import suppp.project.socailMedia.helper.AES;
import suppp.project.socailMedia.helper.EmailSender;
import suppp.project.socailMedia.helper.cloudinaryHelper;
import suppp.project.socailMedia.repository.PostRepository;
import suppp.project.socailMedia.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository repository;

	@Autowired
	EmailSender emailSender;

	@Autowired
	cloudinaryHelper cloudinaryHelper;

	@Autowired
	PostRepository postRepository;

	public String loadRegister(ModelMap map, User user) {
		map.put("User", user);
		return "register.html";
	}

	public String loadRegister(@Valid User user, BindingResult result) {
		if (!user.getPassword().equals(user.getConfirmPassword())) {
			result.rejectValue("confirmpassword", "error.confirmpassword", "Passwords not Matching");
		}
		if (repository.existsByEmail(user.getEmail())) {
			result.rejectValue("email", "error.email", "Email is already in use");
		}
		if (repository.existsByMobile(user.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "Mobile number is already in use");
		}
		if (repository.existsByUserName(user.getUserName())) {
			result.rejectValue("userName", "error.userName", "User name is already in use");
		}
		if (result.hasErrors())
			return "register.html";
		else {
			user.setPassword(AES.encrypt(user.getPassword()));
			int otp = new Random().nextInt(100000, 1000000);
			//emailSender.sendOTP(otp, user.getEmail(), user.getUserName());
			System.out.println("OTP : " + otp);
			user.setOtp(otp);
			repository.save(user);
			return "redirect:/otp/" + user.getId();
		}
	}

	public String verifyOtp(int id, int otp, HttpSession session) {
		User user = repository.findById(id).get();
		if (user.getOtp() == otp) {
			user.setVerified(true);
			user.setOtp(0);
			repository.save(user);
			session.setAttribute("pass", "Account created successfully");
			return "redirect:/login";
		} else {
			session.setAttribute("fail", "Invalid OTP, Try again");
			return "redirect:/otp/" + user.getId();
		}
	}

	public String resendOTP(int id) {
		User user = repository.findById(id).get();
		int otp = new Random().nextInt(100000, 1000000);
		user.setOtp(otp);
		System.out.println(otp);
		// emailSender.sendOTP(otp, user.getEmail(), user.getUserName());
		repository.save(user);
		return "redirect:/otp/" + user.getId();
	}

	public String login(String userName, String password, HttpSession session) {
		User user = repository.findByUserName(userName);
		if (user == null) {
			session.setAttribute("fail", "user not found");
			return "redirect:/login";
		} else {
			if (AES.decrypt(user.getPassword()).equals(password)) {
				if (user.isVerified()) {
					session.setAttribute("user", user);
					session.setAttribute("pass", "Login successfull!");
					return "redirect:/home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					user.setOtp(otp);
					System.err.println(otp);
					// emailSender.sendOtp(user.getEmail(), otp, user.getFirstname());
					repository.save(user);
					session.setAttribute("pass", "Otp Sent Success, First Verify Email to Login");
					return "redirect:/otp/" + user.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect password");
				return "redirect:/login";
			}
		}
	}

	public String loadHome(HttpSession session,ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<User> users = user.getFollowing();
			List<Post> posts = postRepository.findByUserIn(users);
			if (!posts.isEmpty())
				map.put("posts", posts);
			return "home.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout successful");
		return "redirect:/login";
	}

	public String loadProfile(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<Post> posts = postRepository.findByUser(user);
			if (!posts.isEmpty()) {
				map.put("posts", posts);
			}
			return "profile.html";
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String loadEditprofile(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "/editprofile.html";
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String editprofile(MultipartFile image, String bio, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			if (bio != null)
				user.setBio(bio);
			user.setImageUrl(cloudinaryHelper.saveImage(image));
			repository.save(user);
			return "/profile";
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String postAdd(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "/postUpload.html";
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String addPosts(Post post, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			post.setPostUrl(cloudinaryHelper.uploadPosts(post.getImage()));
			post.setUser(user);
			postRepository.save(post);
			session.setAttribute("pass", "Posted successfully");
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid session");
			return "redirect:/login";
		}
	}

	public String deletePost(int id, HttpSession session) {
		Optional<Post> post = postRepository.findById(id);
		if (post.isPresent()) {
			postRepository.deleteById(id);
			session.setAttribute("pass", "Deleted successfully!");
		}
		return "redirect:/profile";
	}
	
	

	public String editPost(int id, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Post post = postRepository.findById(id).get();
			map.put("post", post);
			return "editPost.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String updatePost(Post post, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			if (!post.getImage().isEmpty())
				post.setPostUrl(cloudinaryHelper.saveImage(post.getImage()));
			else
				post.setPostUrl(postRepository.findById(post.getId()).get().getPostUrl());
			post.setUser(user);
			postRepository.save(post);

			session.setAttribute("pass", "Updated Success");
			return "redirect:/profile";

		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String viewSuggestions(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<User> suggestions = repository.findByVerifiedTrue();
			List<User> usersToRemove = new ArrayList<User>();

			for (User suggestion : suggestions) {
				if (suggestion.getId() == user.getId()) {
					usersToRemove.add(suggestion);
				}
				for (User followingUser : user.getFollowing()) {
					if (followingUser.getId() == suggestion.getId()) {
						usersToRemove.add(suggestion);
					}
				}
			}
			suggestions.removeAll(usersToRemove);
			if (suggestions.isEmpty()) {
				session.setAttribute("fail", "No Suggestions");
				return "redirect:/profile";
			} else {

				map.put("suggestions", suggestions);
				return "suggestions.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	

	public String followUser(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			User folllowedUser = repository.findById(id).get();

			user.getFollowing().add(folllowedUser);
			folllowedUser.getFollowing().add(user);
			repository.save(user);
			repository.save(folllowedUser);
			session.setAttribute("user", repository.findById(user.getId()).get());
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String getFollowers(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<User> followers = user.getFollowers();
			if (followers.isEmpty()) {
				session.setAttribute("fail", "No Followers");
				return "redirect:/profile";
			} else {
				map.put("followers", followers);
				return "followers.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String getFollowing(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			List<User> following = user.getFollowing();
			if (following.isEmpty()) {
				session.setAttribute("fail", "Not Following Anyone");
				return "redirect:/profile";
			} else {
				map.put("following", following);
				return "following.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String unfollow(HttpSession session, int id) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			User user2 = null;
			for (User user3 : user.getFollowing()) {
				if (id == user3.getId()) {
					user2 = user3;
					break;
				}
			}
			user.getFollowing().remove(user2);
			repository.save(user);
			User user3 = null;
			for (User user4 : user2.getFollowers()) {
				if (user.getId() == user4.getId()) {
					user3 = user4;
					break;
				}
			}
			user2.getFollowers().remove(user3);
			repository.save(user2);
			session.setAttribute("user", repository.findById(user.getId()).get());
			return "redirect:/profile";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String viewProfile(int id, HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			User checkedUser = repository.findById(id).get();
			List<Post> posts = postRepository.findByUser(checkedUser);
			if (!posts.isEmpty())
				map.put("posts", posts);
			map.put("user", checkedUser);
			return "viewProfile.html";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}
	
	public String likePost(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Post post = postRepository.findById(id).get();
			
			boolean flag=true;
			
			for (User likedUser : post.getLikedUsers()) {
				if (likedUser.getId() == user.getId()) {
					flag=false;
					break;
				}
			}
			if(flag) {
				post.getLikedUsers().add(user);
			}

			postRepository.save(post);
			return "redirect:/home";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

	public String dislikePost(int id, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			Post post = postRepository.findById(id).get();
			
			for (User likedUser : post.getLikedUsers()) {
				if (likedUser.getId() == user.getId()) {
					post.getLikedUsers().remove(likedUser);
					break;
				}
			}

			postRepository.save(post);
			return "redirect:/home";
		} else {
			session.setAttribute("fail", "Invalid Session");
			return "redirect:/login";
		}
	}

}
