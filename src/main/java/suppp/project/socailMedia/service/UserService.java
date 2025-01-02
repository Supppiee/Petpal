package suppp.project.socailMedia.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid;
import suppp.project.socailMedia.dto.User;
import suppp.project.socailMedia.helper.AES;
import suppp.project.socailMedia.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository repository;

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
			System.out.println("OTP : "+otp);
			user.setOtp(otp);
			repository.save(user);
			return "redirect:/otp/"+user.getId();
		}
	}

}
