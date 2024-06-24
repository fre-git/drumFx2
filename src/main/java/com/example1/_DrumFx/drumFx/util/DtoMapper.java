package com.example1._DrumFx.drumFx.util;

import com.example1._DrumFx.drumFx.dto.MidiTrackDto;
import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.MidiTrack;
import com.example1._DrumFx.drumFx.model.User;

import java.util.ArrayList;
import java.util.List;

public class DtoMapper {

    //User mappers
    public static UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        if(user.getProfilePicture() != null){
            userDto.setProfilePicture(user.getProfilePicture());
            String base64Avatar = ImageConverter.convertByteArrayToBase64(user.getProfilePicture());
            userDto.setPicture(base64Avatar);
        }
        return userDto;
    }

    public static User mapToUserModel(UserDto userDto){
        User user = new User();
        if(userDto.getId() != null){
           user.setId(userDto.getId());
        }
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
//        if(userDto.getPassword() != null){
//            user.setPassword(userDto.getPassword());
//        }
        user.setRoles(userDto.getRoles());
        return user;
    }



    //MidiTrack mappers
    public static MidiTrack mapToMidiTrackModel(MidiTrackDto midiTrackDto){
        MidiTrack midiTrack = new MidiTrack();
        if(midiTrackDto.getId() != null){
            midiTrack.setId(midiTrackDto.getId());
        }
        midiTrack.setTitle(midiTrackDto.getTitle());
        midiTrack.setDescription(midiTrackDto.getDescription());
        midiTrack.setFileData(midiTrackDto.getFileData());
        midiTrack.setUser(mapToUserModel(midiTrackDto.getUser()));
        midiTrack.setPopularity(midiTrackDto.getPopularity());
        midiTrack.setLikes(midiTrackDto.getLikes());
        midiTrack.setUploadDate(midiTrackDto.getUploadDate());
        return midiTrack;
    }

    public static MidiTrackDto mapToMidiTrackDto(MidiTrack midiTrack){
        MidiTrackDto midiTrackDto = new MidiTrackDto();
        midiTrackDto.setId(midiTrack.getId());
        midiTrackDto.setTitle(midiTrack.getTitle());
        midiTrackDto.setDescription(midiTrack.getDescription());
        midiTrackDto.setFileData(midiTrack.getFileData());
        midiTrackDto.setUser(mapToUserDto(midiTrack.getUser()));
        midiTrackDto.setPopularity(midiTrack.getPopularity());
        midiTrackDto.setLikes(midiTrack.getLikes());
        midiTrackDto.setUploadDate(midiTrack.getUploadDate());
        return midiTrackDto;
    }

    public static List<MidiTrackDto> mapToMidiTrackDtoList(List<MidiTrack> tracks){
        List<MidiTrackDto> midiTrackDtoList = new ArrayList<>();
        for(MidiTrack midiTrack: tracks){
            midiTrackDtoList.add(mapToMidiTrackDto(midiTrack));
        }
        return midiTrackDtoList;
    }
}
