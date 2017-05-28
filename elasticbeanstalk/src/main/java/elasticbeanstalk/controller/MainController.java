package elasticbeanstalk.controller;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import elasticbeanstalk.dao.PostRepository;
import elasticbeanstalk.model.Post;
import elasticbeanstalk.service.PostService;
import elasticbeanstalk.storage.StorageFileNotFoundException;
import elasticbeanstalk.storage.StorageService;
import elasticbeanstalk.service.UploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

@Controller
public class MainController {

	@Autowired
	private PostService postService;

	@GetMapping("/")
	public String home(HttpServletRequest request) {
		request.setAttribute("mode", "MODE_HOME");
		return "index";
	}

	@GetMapping("/all-post")
	public String allPost(HttpServletRequest request) {
		request.setAttribute("lstPost", postService.findAll());
		request.setAttribute("mode", "MODE_ALL");
		return "index";
	}

	@GetMapping("/add-post")
	public String addPost(HttpServletRequest request) {
		request.setAttribute("mode", "MODE_ADD");
		return "index";
	}

	@PostMapping("/save-post")
	public String savePost(@RequestParam("url") MultipartFile file, @ModelAttribute Post post,
			BindingResult bindingResult, HttpServletRequest request) {
		try {
			if (file != null) {
				File convFile = ConvertMultipartFileToFile(file);
				
				UploadService service = new UploadService();
				String url = service.upload(convFile);
				
				post.setUrl(url);
			}
		} catch (Exception e) {
			System.out.println("Save file error: " + e);
			Post _postOld = postService.findPost(post.getId());
			if (_postOld != null) {
				post.setUrl(_postOld.getUrl());
			}
		}
		postService.save(post);
		request.setAttribute("lstPost", postService.findAll());
		request.setAttribute("mode", "MODE_ALL");
		return "redirect:/all-post";

	}
	private static File ConvertMultipartFileToFile(final MultipartFile file)
	{
		File convFile = null;
		try
		{
			convFile = new File(file.getOriginalFilename());
			convFile.createNewFile(); 
			FileOutputStream fos = new FileOutputStream(convFile); 
			fos.write(file.getBytes());
			fos.close();
		}
		catch(Exception e)
		{
			
		}
	    return convFile;
	}
	@GetMapping("/update-post")
	public String updatePost(@RequestParam int id, HttpServletRequest request) {
		request.setAttribute("post", postService.findPost(id));
		request.setAttribute("mode", "MODE_UPDATE");
		return "index";
	}

	@GetMapping("/delete-post")
	public String deletePost(@RequestParam int id, HttpServletRequest request) {
		try
		{
			Post post = postService.findPost(id);
			if(post.getUrl() != null && !post.getUrl().isEmpty())
				(new UploadService()).deleteFile(post.getUrl()); //Xóa file lưu trên server
		}
		catch (Exception e) {
		}
				
		postService.delete(id);
		request.setAttribute("lstPost", postService.findAll());
		request.setAttribute("mode", "MODE_ALL");
		return "redirect:/all-post";
	}
}
