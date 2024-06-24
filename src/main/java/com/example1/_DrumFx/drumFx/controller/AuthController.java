package com.example1._DrumFx.drumFx.controller;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.service.MidiTrackService;
import com.example1._DrumFx.drumFx.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MidiTrackService midiTrackService;


    @GetMapping("/")
    public String home(Model model) {
        List<MidiTrackDto> topTracksLastMonth = midiTrackService.findTop10TracksLastMonth();
        model.addAttribute("topTracksLastMonth", topTracksLastMonth);
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model) {
        UserDto existing = userService.findByEmail(user.getEmail());

        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login?success";
    }

}
