package com.example.oop.methods.team.controller;

import com.example.oop.methods.team.model.User;
import com.example.oop.methods.team.model.UserRole;
import com.example.oop.methods.team.repository.LibraryRepository;
import com.example.oop.methods.team.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final LibraryRepository libraryRepository;

    @Autowired
    public UserController(UserRepository userRepository, LibraryRepository libraryRepository) {
        this.userRepository = userRepository;
        this.libraryRepository = libraryRepository;
    }

    @RequestMapping(path = "/user/login", method = RequestMethod.GET)
    public String userForm(Model model) {
        model.addAttribute("user", new User());
        return "login_page";
    }

    @RequestMapping(path = "/user/login/submit", method = RequestMethod.POST)
    public String userSubmit(
            @ModelAttribute
            User user,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            if (this.userRepository.approveLogin(user.getUsername(), user.getPassword())) {
                User dbUser = this.userRepository.getUserByUsername(user.getUsername());
                return "redirect:/user/dashboard/" + dbUser.getId();
            } else {
                throw new Exception("Could not approve login: wrong password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/user/dashboard/{user_id}", method = RequestMethod.GET)
    public String userDashboard(
            @PathVariable("user_id") Long userId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", this.userRepository.getUserById(userId));
            return "user_page";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/user/signup", method = RequestMethod.GET)
    public String userSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_page";
    }

    @RequestMapping(path = "/user/signup/submit", method = RequestMethod.POST)
    public String userSignupSubmitForm(
            @ModelAttribute User user,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Long userId = this.userRepository.createUser(user.getUsername(), user.getPassword(), UserRole.READER);
            this.libraryRepository.createLibrary(this.userRepository.getUserById(userId));
            return "redirect:/user/dashboard/" + userId;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

}
