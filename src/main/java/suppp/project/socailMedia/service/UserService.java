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
			emailSender.sendOTP(otp, user.getEmail(), user.getUserName());
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

	public String loadHome(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return "/home.html";
		} else {
			session.setAttribute("fail", "Invalid session");
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
	
}
