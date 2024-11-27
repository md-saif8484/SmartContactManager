package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contacts;
import com.smart.entities.Users;
import com.smart.helper.Message;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void commonAttribute(Model m,Principal p)
	{
		String username = p.getName();
		Users u = userRepository.getUserByUsername(username);
		m.addAttribute("user", u);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model m, Principal p)
	{
		
		m.addAttribute("title","Dashboard");
		return "normal/user_dashboard";
	}
	
	@RequestMapping("/addcontact")
	public String addContact(Model m)
	{
		m.addAttribute("title","Add Contacts");
		m.addAttribute("contact",new Contacts());
		return "normal/add_contact";
	}
	
//	process add-contact handler
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contacts contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal p,Model m)
	{
		try {
			System.out.println(contact);
			String username = p.getName();
			Users u = userRepository.getUserByUsername(username);
			
			
			if(file.isEmpty())
			{
				System.out.println("FIle is empty");
				contact.setImage("contact.png");
			}
			else
			{
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("uploaded image");
			}
			
			contact.setUser(u);
			u.getContacts().add(contact);
			userRepository.save(u);
			m.addAttribute("message",new Message("Contact added successfully!!", "success"));
		}catch(Exception e)
		{
			System.out.println("Error "+ e.getMessage());
			e.printStackTrace();
			m.addAttribute("message",new Message("Something went wrong!!", "danger"));
		}
		return "normal/add_contact";
	}
	
	
	/* show all contact detail handler */
	
	@GetMapping("/show-contact/{page}")
	public String viewContact(@PathVariable("page") Integer page, Model m,Principal p)
	{	
			Pageable of  = PageRequest.of(page, 5);
			String username = p.getName();	
			Users user = this.userRepository.getUserByUsername(username);
			Page<Contacts> contacts = this.contactRepository.findContactByUser(user.getId(),of);
			m.addAttribute("title","View Contact");
			m.addAttribute("contacts",contacts);
			m.addAttribute("currentPage",page);
			m.addAttribute("totalPage",contacts.getTotalPages());
			return "normal/view_contact";
	}
	
//	show contact detail of particulalr person handler
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model m,Principal p)
	{
		String userName = p.getName();
		Users user = this.userRepository.getUserByUsername(userName);
		Optional<Contacts> byId = this.contactRepository.findById(cId);
		if(byId.isEmpty())
		{
			return "normal/contact_detail";
		}
		Contacts contacts = byId.get();
		m.addAttribute("title","contact detail");
		if(user.getId()==contacts.getUser().getId())
			m.addAttribute("contact",contacts);
		return "normal/contact_detail";
	}
	
//	delete handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, RedirectAttributes redirectAttributes, Principal p) {
	    String userName = p.getName();
	    Users user = this.userRepository.getUserByUsername(userName);
	    Optional<Contacts> byId = this.contactRepository.findById(cId);

	    if (byId.isEmpty()) {
	        redirectAttributes.addFlashAttribute("message", new Message("Unauthorized user!!", "danger"));
	        return "redirect:/user/show-contact/0";
	    }

	    Contacts contact = byId.get();

	    if (user.getId() == contact.getUser().getId()) {
	        // Check if the image is not the default image "contact.png"
	        String imageName = contact.getImage(); // Assuming 'image' is the field holding the image name
	        if (!imageName.equals("contact.png")) {
	            // Define the path to the image directory
	            File saveFile;
				try {
						saveFile = new ClassPathResource("static/image").getFile();
					
			            Path path = Paths.get(saveFile.getAbsolutePath(), imageName);
		
			            // Check if the image exists and delete it
			            File imageFile = path.toFile();
			            if (imageFile.exists() && imageFile.isFile()) {
			                if (imageFile.delete()) {
			                    System.out.println("Image file deleted successfully: " + imageName);
			                } else {
			                    System.out.println("Failed to delete the image file: " + imageName);
			                }
			            } else {
			                System.out.println("Image file not found: " + imageName);
			            }
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

	        // Delete the contact from the database
	        this.contactRepository.delete(contact);

	        // Add a flash attribute for the message
	        redirectAttributes.addFlashAttribute("message", new Message("Contact deleted successfully!!", "success"));
	    } else {
	        redirectAttributes.addFlashAttribute("message", new Message("Unauthorized user!!", "danger"));
	    }

	    return "redirect:/user/show-contact/0";
	}
	
//	update contact handler
	@PostMapping("/update-contact/{cId}")
	public String updateHandler(@PathVariable("cId") Integer cId,Principal p,Model m)
	{
		m.addAttribute("title","Update contact");
		Contacts contacts = this.contactRepository.findById(cId).get();
		m.addAttribute("contact",contacts); 
		return "normal/update_form";
	}
	
//	process update contact handler
	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contacts contact,
			@RequestParam("cId") Integer cId,
			@RequestParam("profileImage") MultipartFile file, 
			Model m,Principal p,
			RedirectAttributes redirectAttributes)
	{
		Contacts oldContacts = this.contactRepository.findById(cId).get();
		String userName = p.getName();
		Users user = this.userRepository.getUserByUsername(userName);
		
		try {
			if(!file.isEmpty())
			{
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/image").getFile();
				
//				delete old image
				String imageName = oldContacts.getImage();
				if(!imageName.equals("contact.png"))
				{
					Path path = Paths.get(saveFile.getAbsolutePath(), imageName);
					
		            // Check if the image exists and delete it
		            File imageFile = path.toFile();
		            if (imageFile.exists() && imageFile.isFile()) {
		                if (imageFile.delete()) {
		                    System.out.println("Image file deleted successfully: " + imageName);
		                } else {
		                    System.out.println("Failed to delete the image file: " + imageName);
		                }
		            } else {
		                System.out.println("Image file not found: " + imageName);
		            }
				}
				
//				set new image
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("uploaded image");
				
				
			}else {
				contact.setImage(oldContacts.getImage());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		contact.setUser(user);
		this.contactRepository.save(contact);
		redirectAttributes.addFlashAttribute("message", new Message("Your contact updated successfully","success"));
		System.out.println("Flash attribute set: " + redirectAttributes);
		return "redirect:/user/" + contact.getcId() + "/contact";

	}
	
	
	/* User profile handler*/
	@RequestMapping("/profile")
	public String userProfile(Model m, Principal p)
	{
		String username = p.getName();
		Users user = this.userRepository.getUserByUsername(username);
		m.addAttribute("user",user);
		return "normal/user_profile";
	}
	
	/* setting profile handler */
	@RequestMapping("/setting")
	public String passSetting()
	{
		
		return "normal/setting";
	}
	
	/* Process setting profile handler */
	@PostMapping("/process-setting")
	public String processPass(@RequestParam("oldpassword") String oldpassword, 
			@RequestParam("newpassword") String newpassword, Principal p,
			RedirectAttributes redirectAttributes)
	{
		String username = p.getName();
		Users user = this.userRepository.getUserByUsername(username);
		String originalPassword = user.getPassword();
		if(bCryptPasswordEncoder.matches(oldpassword, originalPassword))
		{
			user.setPassword(bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(user);
			redirectAttributes.addFlashAttribute("message", new Message("Your password updated successfully","success"));
			return "redirect:/user/index";
		}
		else {
			redirectAttributes.addFlashAttribute("message", new Message("Your password does not match","danger"));
			return "redirect:/user/setting";
		}
	}
	

}
