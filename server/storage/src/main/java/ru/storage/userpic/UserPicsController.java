package ru.storage.userpic;

import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v0/content-creators/user-pics")
public class UserPicsController {

    private final UserPicsService userPicsService;

    public UserPicsController(UserPicsService userPicsService) {
        this.userPicsService = userPicsService;
    }
    
    /**
     * Upload or update the user pic.
     * 
     * @param picCategory the category of the user pic (e.g., USER)
     * @param image the multipart file containing the user pic
     * @return Response:
     * <ul>
     *     <li> 201 CREATED - User pic uploaded successfully with saved metadata.</li>
     * </ul>
     */
    @PostMapping("upload")
    public ResponseEntity<UserPic> uploadUserPic(@RequestParam("picCategory") PicCategory picCategory, @RequestParam MultipartFile image) {
        var userPic = new UserPic(null, image.getContentType(), image.getOriginalFilename(), picCategory);
        var savedUserPicDetails = userPicsService.saveImageMetadata(userPic);
        userPicsService.upload(savedUserPicDetails , image);
        return new ResponseEntity<>(savedUserPicDetails, HttpStatus.CREATED);
    }

    /**
     *  Get images from S3 cloud storage by IDs.
     *
     * <p>This method supports both single and multiple content metadata IDs, separated by commas</p>.
     *
    * @param picCategory the category of the user pic (e.g., USER)
     * @param ids a comma-separated string of ids 
     * @return Response
     * <ul>
     *     <li>200 (OK). A list of byte arrays representing the images if successful.
     *     The values are in the same order as requested ids.</li>
     *     <li>400 (BAD_REQUEST). For inputs that are not string, also if number format is incorrect</li>
     *     <li>500 (INTERNAL_SERVER_ERROR). An empty list with an error message header if an error occurs </li>
     * </ul>
     */
    @GetMapping("{picCategory}/{ids}")
    public ResponseEntity<List<Pair<String, byte[]>>> getUserPic(@PathVariable PicCategory picCategory, @PathVariable String ids) {
        // TODO 
        // return new ResponseEntity<>(userPicsService.getInPairs(picCategory, ids), HttpStatus.OK);
        return null;
    }

    /**
     * Delete user pic.
     *
     * @param picCategory the category of the user pic (e.g., USER)
     * @param id the ID of the user pic
     * @return Response:
     * <ul>
     *     <li> 204 (No Content) </li>
     * </ul>
     */
    @DeleteMapping("{picCategory}/{ids}")
    public ResponseEntity<Void> deleteUserPic(@PathVariable PicCategory picCategory, @PathVariable String ids) {
        userPicsService.delete(picCategory, ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
