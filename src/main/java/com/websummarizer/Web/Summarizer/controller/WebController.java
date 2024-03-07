package com.websummarizer.Web.Summarizer.controller;

import com.websummarizer.Web.Summarizer.bart.Bart;
import com.websummarizer.Web.Summarizer.model.User;
import com.websummarizer.Web.Summarizer.parsers.HTMLParser;
import com.websummarizer.Web.Summarizer.services.UserServiceImpl;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * Controller for web-related actions.
 */
@Controller
public class WebController {

    @Autowired
    private final Bart bart;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JavaMailSender emailSender;

    private static final Logger logger = Logger.getLogger(Bart.class.getName());

    /**
     * Constructor for WebController.
     *
     * @param bart The Bart instance to use.
     */
    public WebController(Bart bart) {
        this.bart = bart;
    }

    /**
     * Endpoint for user registration.
     *
     * @return The name of the view to render.
     */
    @GetMapping("/register")
    public String register() {
        return "index";
    }

    /**
     * Endpoint for user sign in.
     *
     * @return The name of the view to render.
     */
    @GetMapping("/signin")
    public String signIn() {
        return "index";
    }

    /**
     * Endpoint for getting a summary.
     *
     * @param input The input from the user.
     * @param model The model to use.
     * @return The name of the view to render.
     * @throws IOException If an I/O error occurs.
     */
    @PostMapping("/api/summary")
    public String getSummary(
            @RequestParam(value = "input") String input,
            Model model
    ) throws IOException {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd h:mm:ss a");

        String username = "You";
        String output;

        boolean isURL = isValidURL(input);

        String url = "";
        if (isURL) {
            url = input;
            input = HTMLParser.parser(input);
        }
        else {
            // Facebook needs a valid URL for the share function to work.
            // Without a valid URL, the facebook button will open a window giving the user an error.
            // So, this else statement will set a temp url if the user instead inputs a block of text to parse.
            // If you come up with a better solution or if this is unnecessary, please feel free to edit/remove
            url = "https://www.google.com/";
        }
        try {
            output = bart.queryModel(input);
        } catch (Exception e) {
            output = "Error Occured";
            System.out.println("catched");
        }

        model.addAttribute("date", dateFormat.format(date));
        model.addAttribute("user", username);
        model.addAttribute("input", input);
        model.addAttribute("output", output);

        // Share Button Attributes
        model.addAttribute("fb", "https://www.addtoany.com/add_to/facebook?linkurl="+url);
        model.addAttribute("twitter", "https://www.addtoany.com/add_to/x?linkurl="+url);
        model.addAttribute("email", "https://www.addtoany.com/add_to/email?linkurl="+url);

        return "api/summary";
    }

    /**
     * Endpoint for creating a user.
     *
     * @param user    The user to create.
     * @param session The current session.
     * @return The name of the view to render.
     */
    @PostMapping("/createUser")
    public String createUser(@ModelAttribute User user, HttpSession session) {
        session.setAttribute("msg", "");
        logger.info("Received user creation request: " + user);
        boolean bool = false;
        try {
            bool = userService.createUser(user) != null;
        } catch (Exception e) {
            session.setAttribute("msg", "Email already exists");
            logger.warning("User creation failed: " + e.getMessage());
        }
        if (bool) {
            session.setAttribute("msg", "Registered Successfully");
            logger.info("User created successfully: " + user);
        }
        return "redirect:/";
    }

    /**
     * Checks if a string is a valid URL.
     *
     * @param urlStr The string to check.
     * @return true if the string is a valid URL, false otherwise.
     */
    private static boolean isValidURL(String urlStr) {
        try {
            // Attempt to create a URL object
            new URL(urlStr).toURI();
            return true;
        } catch (Exception e) {
            // If an exception occurs, URL is not valid
            return false;
        }
    }

    // Temp: Reset Password
    /*
        TO-DO
        -Get email
        -Send email
     */
    @PostMapping("emailResetPW")
    public String resetPW(@ModelAttribute User user){
        /*  Check if the email address is valid
            Note: The email input box on the website strictly follows the email address convention.
            As a result, it would be fairly difficult for the user to input an invalid email address format.
            But, just in case, this is here as a backup.
        */
        boolean isEmailValid = EmailValidator.getInstance().isValid(user.getEmail());
        if (!isEmailValid){
            logger.info("Invalid Email: " + user.getEmail());
            return "redirect:/";
        }

        //  Create email body
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@test.com");
        message.setTo(user.getEmail());
        message.setSubject("Test Email");
        message.setText("This is a test for a school project. Please delete if you got this by accident.");

        //  Send email
        try{
            emailSender.send(message);
            logger.info("Email successfully sent to: " + user.getEmail());
        } catch (MailParseException m){
            logger.info("There was an error sending the email");
            logger.info("Error Message: " + m.getMessage());
            logger.info("Error Cause: " + m.getCause());
        }
        return "redirect:/";
    }






//    @GetMapping("/")
//    String index(Model model, @AuthenticationPrincipal OAuth2User principal) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        // If the user is actively logged in, automatically redirect to pro features site
//        // https://stackoverflow.com/questions/13131122/spring-security-redirect-if-already-logged-in
//        if (!(auth instanceof AnonymousAuthenticationToken)) {
//            model.addAttribute("loginText", "Logout"); // data to send to html page
//            model.addAttribute("loginURL", "/logout");
//            return "index";
//        }
//        model.addAttribute("loginText", "Login"); // data to send to html page
//        model.addAttribute("loginURL", "/login");
//        return "index"; // webpage name
//    }
}