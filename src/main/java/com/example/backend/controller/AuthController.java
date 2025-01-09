package com.example.backend.controller;

import com.example.backend.config.JwtProvider;
import com.example.backend.event.RegistrationCompleteEvent;
import com.example.backend.event.listener.RegistrationCompleteEventListener;
import com.example.backend.model.Cart;
import com.example.backend.model.USER_ROLE;
import com.example.backend.model.User;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.request.LoginRequest;
import com.example.backend.response.AuthResponse;
import com.example.backend.service.CustomerUserDetailsService;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private CustomerUserDetailsService customerUserDetailsService;
    private CartRepository cartRepository;

    private  RegistrationCompleteEventListener eventListener;
    private  HttpServletRequest request;
    private  ApplicationEventPublisher eventPublisher;
    private UserService userService;

    @Autowired
    public AuthController(CartRepository cartRepository, CustomerUserDetailsService customerUserDetailsService, RegistrationCompleteEventListener eventListener, ApplicationEventPublisher eventPublisher, JwtProvider jwtProvider, PasswordEncoder passwordEncoder, HttpServletRequest request, UserRepository userRepository, UserService userService) {
        this.cartRepository = cartRepository;
        this.customerUserDetailsService = customerUserDetailsService;
        this.eventListener = eventListener;
        this.eventPublisher = eventPublisher;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.request = request;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler (@RequestBody User user, final HttpServletRequest request) throws Exception{
        User isEmailExist = userRepository.findByEmail(user.getEmail());
        if(isEmailExist != null){
            throw new Exception("Email is already used !!");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        newUser.setRole(user.getRole());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(newUser);

        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cartRepository.save(cart);



        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Register successfully");
        authResponse.setRole(savedUser.getRole());

        eventPublisher.publishEvent(new RegistrationCompleteEvent(applicationUrl(request), savedUser,authResponse));


        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        try {

            log.info("Received token: {}", token);

            String email = jwtProvider.getEmailFromJwtToken(token);
            log.info("Received email: {}", email);
            User user = userRepository.findByEmail(email);

            if (user == null) {
                return "User not found";
            }

            if (user.isEnabled()) {
                return "Account is already verified";
            }

            String verificationResult = userService.validateToken(token);

            if ("valid".equalsIgnoreCase(verificationResult)) {
                user.setEnabled(true);
                userRepository.save(user);
                return "Account is verified. Login now!";
            }

            return "Invalid token";
        } catch (Exception e) {
            return "Error verifying token: " + e.getMessage();
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin (@RequestBody LoginRequest req) {
        String username = req.getEmail();
        String password = req.getPassword();

        Authentication authentication = authenticate(username, password);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();


        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login successfully");
        authResponse.setRole(USER_ROLE.valueOf(role));

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("Invalid username...");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Wrong password...");
        }
       return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
