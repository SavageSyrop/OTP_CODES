package ru.otp.controller;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otp.dto.UserDto;
import ru.otp.entities.Role;
import ru.otp.entities.User;
import ru.otp.service.UserService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private InfoCardsServiceImpl infoCardsService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public Boolean checkAdminRole() {
        return true;
    }
    @GetMapping("/getCardOwner")
    public UserDto getUserByCardUUID(@RequestParam String cardUUID) {
        InfoCard infoCard = this.infoCardsService.getByUUID(cardUUID);
        User owner = infoCard.getOwner();
        return convertUserToDto(owner);
    }

    @GetMapping("/getCardsByOwner")
    public List<InfoCardDto> getAllCardsByUserId(@RequestParam Long userId) {
        List<InfoCard> cards = this.infoCardsService.getAllCreatedInfoCardsByUserId(userId);
        List<InfoCardDto> dtos = new ArrayList<>();
        for (InfoCard card: cards) {
            dtos.add(convertCardToDto(card));
        }
        return dtos;
    }

    @PostMapping("/ban")
    public void banUser(@RequestParam Long userId) {
        User user = this.userService.getById(userId);
        user.setIsBanned(true);
        this.userService.update(user);
    }

    @PostMapping("/unban")
    public void unbanUser(@RequestParam Long userId) {
        User user = this.userService.getById(userId);
        user.setIsBanned(false);
        this.userService.update(user);
    }

    @DeleteMapping("/deleteCard")
    public void deleteCard(@RequestParam String cardUUID) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        this.infoCardsService.delete(this.infoCardsService.getByUUID(cardUUID));
    }

    private UserDto convertUserToDto(User user) throws ParseException {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        List<String> roleNames = new ArrayList<>();
        for (Role role: user.getRoles()) {
            roleNames.add(role.getName().name());
        }
        userDto.setRoles(roleNames);
        return userDto;
    }

    private InfoCardDto convertCardToDto(InfoCard infoCard) throws ParseException {
        return modelMapper.map(infoCard, InfoCardDto.class);
    }
}
