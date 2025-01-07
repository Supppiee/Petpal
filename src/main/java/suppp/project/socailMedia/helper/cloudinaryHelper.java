package suppp.project.socailMedia.helper;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class cloudinaryHelper {
	
	@Value("${CLOUDINARY_URL}")
	String url;
	
	public String saveImage(MultipartFile file) {
		Cloudinary cloudinary = new Cloudinary(url);
		Map map = null;
		try {
			map = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "petpal"));
		} catch (IOException e) {
		}
		return (String) map.get("url");
	}
}
