package com.example1._DrumFx.drumFx.controller;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.service.MidiTrackService;
import com.example1._DrumFx.drumFx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MidiTrackService midiTrackService;

    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        UserDto user = userService.findByEmail(principal.getName());
        List<MidiTrackDto> tracks = midiTrackService.findByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("tracks", tracks);
        return "myProfile";
    }

    @PostMapping("/upload")
    public String uploadTrack(@RequestParam("file") MultipartFile file, Principal principal) {
        UserDto user = userService.findByEmail(principal.getName());
        midiTrackService.saveTrack(file, user);
        return "redirect:/profile";
    }

    @PostMapping("/uploadProfilePicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file, Model model, Principal principal) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload");
            return "redirect:profile";
        }

        try {
            byte[] bytes = file.getBytes();
            UserDto userDto = userService.findByEmail(principal.getName());
            userDto.setProfilePicture(bytes);
            userService.updateUser(userDto);
            model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/profile";
    }

    @PostMapping("/likeTrack")
    public String likeTrack(@RequestParam Long trackId, Principal principal) {
        midiTrackService.likeTrack(trackId, principal.getName());
        return "redirect:/home";  // Adjust the redirect URL as needed
    }


    @GetMapping("/profile/{username}")
    public String viewUserProfile(@PathVariable String username, Model model) {
        UserDto user = userService.findByUsername(username);
        List<MidiTrackDto> tracks = midiTrackService.findByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("tracks", tracks);
        return "userProfile";
    }
}
