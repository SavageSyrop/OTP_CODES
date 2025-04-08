package ru.otp.controller;

import com.google.zxing.WriterException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.expression.ParseException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ru.otp.entities.UserPrincipal;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/v1/cards")
public class InfoCardsController {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private InfoCardsService infoCardsService;

    @Autowired
    private QRCodeService qrCodeService;

    @PostAuthorize("returnObject.requiredScope=='PUBLIC' || hasAuthority(returnObject.requiredScope) || returnObject.currentUserIsOwner")
    @GetMapping("/{cardUUID}")
    public InfoCardDto getInfoCardByUUID(@PathVariable String cardUUID) {
        return convertToDto(infoCardsService.getByUUID(cardUUID));
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping()
    public List<InfoCardDto> getAllCreatedInfoCardsByCurrentUser() {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<InfoCard> infoCards = infoCardsService.getAllCreatedInfoCardsByUserId(currentUser.getUser().getId());
        List<InfoCardDto> infoCardDtos = new ArrayList<>();
        for (InfoCard infoCard : infoCards) {
            infoCardDtos.add(convertToDto(infoCard));
        }
        return infoCardDtos;
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping()
    public InfoCardDto createInfoCard(@RequestBody InfoCardDto infoCardDto) {
        InfoCard infoCard = convertToEntity(infoCardDto);
        infoCard.setOwner(((UserPrincipal)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUser());
        return convertToDto(infoCardsService.create(infoCard));
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{cardUUID}")
    public void deleteInfoCard(@PathVariable String cardUUID) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InfoCard infoCard = infoCardsService.getByUUID(cardUUID);
        infoCardsService.delete(infoCard);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{cardUUID}")
    public InfoCardDto updateInfoCard(@RequestBody InfoCardDto infoCardDto, @PathVariable String cardUUID) {
        InfoCard infoCardFromDB = infoCardsService.getByUUID(cardUUID);
        InfoCard infoCard = convertToEntity(infoCardDto);
        infoCard.setUniqueCode(cardUUID);
        infoCard.setOwner(infoCardFromDB.getOwner());
        infoCard.setInfoCardImages(infoCardFromDB.getInfoCardImages());
        return convertToDto(infoCardsService.update(infoCard));
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/{cardUUID}/image", consumes = {MULTIPART_FORM_DATA_VALUE})
    public void uploadImageToInfoCard(@PathVariable String cardUUID,
                                      @RequestPart("file") MultipartFile file) {
        InfoCard infoCard = infoCardsService.getByUUID(cardUUID);
        infoCardsService.uploadImageToInfoCard(infoCard, file);
    }

    @GetMapping("/{cardUUID}/image")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String cardUUID, @RequestParam String imageName, HttpServletResponse response) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InfoCard infoCard = infoCardsService.getByUUID(cardUUID);

        InputStream inputStream = infoCardsService.downloadImageFromInfoCard(infoCard, imageName);
        String extension = imageName.substring(imageName.lastIndexOf(".") + 1);
        MediaType contentType = MediaType.ALL;
        switch (extension) {
            case "jpg", "jpeg" -> contentType = MediaType.IMAGE_JPEG;
            case "png" -> contentType = MediaType.IMAGE_PNG;
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/{cardUUID}/qr")
    public ResponseEntity<InputStreamResource> createQR(@PathVariable String cardUUID) throws IOException, WriterException {
        infoCardsService.getByUUID(cardUUID);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(qrCodeService.createMarker(cardUUID)));
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{cardUUID}/image")
    public void deleteImage(@PathVariable String cardUUID, @RequestParam String imageName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        InfoCard infoCard = infoCardsService.getByUUID(cardUUID);
        infoCardsService.deleteInfoCardImage(infoCard, imageName);
    }

    private InfoCardDto convertToDto(InfoCard infoCard) throws ParseException {
        InfoCardDto infoCardDto = modelMapper.map(infoCard, InfoCardDto.class);
        infoCardDto.setOwnerId(infoCard.getOwner().getId());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean currentUserIsOwner = false;
        if (principal instanceof UserPrincipal) {
            currentUserIsOwner = Objects.equals(((UserPrincipal)(principal)).getUser().getId(), infoCard.getOwner().getId());
        }
        infoCardDto.setCurrentUserIsOwner(currentUserIsOwner);
        if (infoCard.getInfoCardImages() != null) {
            infoCardDto.setCardImagePaths(infoCard.getInfoCardImages().stream()
                    .map(InfoCardImage::getImagePath).collect(Collectors.toList()));
        } else {
            infoCardDto.setCardImagePaths(new ArrayList<>());
        }
        return infoCardDto;
    }

    private InfoCard convertToEntity(InfoCardDto infoCardDto) throws ParseException {
        return modelMapper.map(infoCardDto, InfoCard.class);
    }
}
