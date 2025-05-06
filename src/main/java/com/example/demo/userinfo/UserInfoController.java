package com.example.demo.userinfo;

import com.example.demo.security.dto.AuthRequest;
import com.example.demo.security.jwt.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController view를 리턴하지 않아서 view를 위해 컨트롤러로 변경
@Controller
@RequestMapping("/users")
@Slf4j
public class UserInfoController{

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/home")
    public String welcome() {
        return "member/home";
    }

    @GetMapping(value = "/signUp")
    public String signUp(Model model){
        return "member/signUpForm";

    }

    @PostMapping("/signUp")
    //public String addNewUser(@RequestBody UserInfo userInfo){ RequestBody 사용으로 UTF8 not supported 에러 발생
    public String addNewUser(UserInfo userInfo){
        userInfo.setPassword(
                passwordEncoder.encode(userInfo.getPassword()));
        UserInfo savedUserInfo= repository.save(userInfo);
        //return savedUserInfo.getName() + " user added!!"; REST 일경우
        return "redirect:/users/signIn";
    }

    @GetMapping(value = "/signIn")
    public String signInForm(Model model){
        return "member/signInForm";

    }

    @PostMapping("/signIning")
    public String authenticateAndGetToken(AuthRequest authRequest, HttpServletResponse rs) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (authentication.isAuthenticated()) {

            String jwtTk = jwtService.generateToken(authRequest.getEmail());

            UserInfoUserDetails userDetails = (UserInfoUserDetails)authentication.getPrincipal();

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@TOKEN " + jwtTk);

        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }

        //return "redirect:/users/home";
        return this.welcome();
    }


}
