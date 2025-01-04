package suppp.project.socailMedia.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import suppp.project.socailMedia.dto.User;
import suppp.project.socailMedia.helper.AES;
import suppp.project.socailMedia.helper.EmailSender;
import suppp.project.socailMedia.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository repository;
	
	@Autowired
	EmailSender emailSender;

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
		if(repository.existsByMobile(user.getMobile())) {
			result.rejectValue("mobile", "error.mobile", "Mobile number is already in use");
		}
		if(repository.existsByUserName(user.getUserName())) {
			result.rejectValue("userName", "error.userName", "User name is already in use");
		}
		if (result.hasErrors())
			return "register.html";
		else {
			user.setPassword(AES.encrypt(user.getPassword()));
			int otp = new Random().nextInt(100000,1000000);
			emailSender.sendOTP(otp, user.getEmail(), user.getUserName());
			System.out.println("OTP : "+otp);
			user.setOtp(otp);
			repository.save(user);
			return "redirect:/otp/"+user.getId();
		}
	}

	public String verifyOtp(int id, int otp, HttpSession session) {
		User user = repository.findById(id).get();
		if(user.getOtp() == otp){
			user.setVerified(true);
			user.setOtp(0);
			repository.save(user);
			session.setAttribute("pass", "Account created successfully");
			return "redirect:/login";
		}else {
			session.setAttribute("fail", "Invalid OTP, Try again");
			return "redirect:/otp/"+user.getId();
		}
	}

	public String resendOTP(int id) {
		User user = repository.findById(id).get();
		int otp = new Random().nextInt(100000,1000000);
		user.setOtp(otp);
		System.out.println(otp);
		emailSender.sendOTP(otp, user.getEmail(), user.getUserName());
		repository.save(user);
		return "redirect:/otp/"+user.getId();
	}
}
