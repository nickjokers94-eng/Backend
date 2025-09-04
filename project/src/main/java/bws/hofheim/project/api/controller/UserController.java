package bws.hofheim.project.api.controller;

import bws.hofheim.project.api.model.User;
import bws.hofheim.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/user")
    public User getUser(@RequestParam String username){
        return userService.getUser(username);

    }

    //User registrieren
    @PostMapping("/user/register")
    public boolean registerUser(@RequestParam String username, @RequestParam String password){
        return userService.registerUser(username, password);

    }

    @PostMapping("/user/login")
    public User login(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username, password);
}


    @PutMapping("user/passwordChange")
    public boolean passwordChange(@RequestParam String username, @RequestParam String password){
        return userService.passwordChange(username, password);
    }



    // Admin-Funktionen:

    @PutMapping("user/unlockUser")
    public boolean unlockUser(@RequestParam String username){
        return userService.unlockUser(username);
    }

    @DeleteMapping("user/deleteUser")
    public boolean deleteUser(@RequestParam String username){
        return userService.deleteUser(username);
    }

    @PostMapping("user/createUser")
    public boolean createUser(@RequestParam String username, @RequestParam String password, @RequestParam String role){
        return userService.createUser(username, password, role);
    }
}
