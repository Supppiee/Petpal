package suppp.project.socailMedia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import suppp.project.socailMedia.dto.Post;
import suppp.project.socailMedia.dto.User;
import suppp.project.socailMedia.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService service;

	@GetMapping("/")
	public String loadDefault() {
		return "/default.html";
	}

	@GetMapping("/login")
	public String load() {
		return "/login.html";
	}

	@GetMapping("/contact")
	public String loadContactUs() {
		return "/contact.html";
	}

	@GetMapping("/errors")
	public String errors() {
		return "/handleError.html";
	}

	@GetMapping("/register")
	public String loadRegister(ModelMap map, User user) {
		return service.loadRegister(map, user);
	}

	@PostMapping("/register")
	public String register(@Valid User user, BindingResult result) {
		return service.loadRegister(user, result);
	}

	@GetMapping("/otp")
	public String otp() {
		return "/otp.html";
	}

	@GetMapping("/otp/{id}")
	public String loadOtpPage(@PathVariable int id, ModelMap map) {
		map.put("id", id);
		return "/otp.html";
	}

	@PostMapping("/verifyOtp")
	public String verifyOTP(@RequestParam int id, @RequestParam int otp, HttpSession session) {
		return service.verifyOtp(id, otp, session);
	}

	@GetMapping("/resendOTP/{id}")
	public String resendOTP(@PathVariable int id) {
		return service.resendOTP(id);
	}

	@PostMapping("/login")
	public String login(@RequestParam String userName, @RequestParam String password, HttpSession session) {
		return service.login(userName, password, session);
	}

	@GetMapping("/home")
	public String loadHome(HttpSession session) {
		return service.loadHome(session);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return service.logout(session);
	}

	@GetMapping("/profile")
	public String loadProfile(HttpSession session, ModelMap map) {
		return service.loadProfile(session, map);
	}

	@GetMapping("/editProfile")
	public String loadditProfile(HttpSession session) {
		return service.loadEditprofile(session);
	}

	@PostMapping("/editProfile")
	public String editProfile(@RequestParam MultipartFile image, @RequestParam String bio, HttpSession session) {
		return service.editprofile(image, bio, session);
	}

	@GetMapping("/post")
	public String postAdd(HttpSession session) {
		return service.postAdd(session);
	}

	@PostMapping("/post")
	public String addPosts(Post post, HttpSession session) {
		return service.addPosts(post, session);
	}

	@GetMapping("/profile/{id}")
	public String deletePost(@PathVariable int id, HttpSession session) {
		return service.deletePost(id, session);
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable int id, HttpSession session, ModelMap map) {
		return service.editPost(id, session, map);
	}
	@PostMapping("/updatePost")
	public String updatePost(Post post, HttpSession session) throws Exception {
		return service.updatePost(post, session);
	}

}
