package ru.video_material.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.service.PosterService;
import ru.video_material.util.PosterWithMetadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/posters")
public class PosterController {

    private final PosterService service;

    @Autowired
    public PosterController(PosterService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> savePoster(@RequestParam MultipartFile file) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        try {
            String savedPosterId = service.save(file);
            return new ResponseEntity<>(savedPosterId, httpHeaders, HttpStatus.OK);
        } catch (NullPointerException | IOException ex) {
            return new ResponseEntity<>(
                    "Impossible to read video file.", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(
                    "Couldn't save video. Video content is empty.", httpHeaders, HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping(value = "/get/byId/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPosterById(@PathVariable String id) {
        PosterWithMetadata data = service.getPosterWithMetadataById(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("image", "jpeg"));
        httpHeaders.set("id", data.getContentId());
        try {
            return new ResponseEntity<>(data.getData(), httpHeaders, HttpStatus.OK);
        } catch (NullPointerException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/get/byId/test/{id}/{secId}")
    public PosterWithMetadata[] somethingTEST(@PathVariable String id, @PathVariable String secId) {
        PosterWithMetadata data1 = service.getPosterWithMetadataById(id);
        PosterWithMetadata data2 = service.getPosterWithMetadataById(secId);
        try {
            return new PosterWithMetadata[]{
                    data1,
                    data2
            };
        } catch (NullPointerException ex) {
            throw new InternalError();
        }
    }

//    @GetMapping("/images")
//    public ResponseEntity<byte[]> getImages() throws IOException {
//        List<byte[]> imageBytesList = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            String filePath = "path/to/your/image" + i + ".jpg"; // Replace with your actual path
//            byte[] imageData = Files.readAllBytes(Paths.get(filePath));
//            imageBytesList.add(imageData);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_MIXED);
//        StringBuilder boundary = new StringBuilder("myboundary");
//
//        MultipartBodyWriter writer = new MultipartBodyWriter(boundary.toString());
//        try {
//            for (byte[] imageData : imageBytesList) {
//                MultiValueMap<String, String> partHeaders = new LinkedMultiValueMap<>();
//                partHeaders.add("Content-Type", "image/jpeg; filename=image" + imageBytesList.indexOf(imageData) + ".jpg");
//                writer.addPart("image", partHeaders, imageData);
//            }
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(writer.build());
//        } catch (MultipartException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }


    @DeleteMapping("/delete/byId/{id}")
    public ResponseEntity<String> deletePosterById(@PathVariable String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return service.deleteById(id) ?
                new ResponseEntity<>(STR."poster image with id \{id} successfully deleted.", httpHeaders, HttpStatus.OK) :
                ResponseEntity.notFound().build();
    }


}
