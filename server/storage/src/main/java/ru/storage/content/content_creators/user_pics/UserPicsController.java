package ru.storage.content.content_creators.user_pics;

import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * @param id the ID of the user
     * @param multipartFile the multipart file containing the user pic
     * @return Response:
     * <ul>
     *     <li> 201 CREATED - User pic uploaded successfully </li>
     * </ul>
     */
    @PostMapping("user-pics/upload")
    public ResponseEntity<Void> uploadUserPic(@RequestParam String id, @RequestParam MultipartFile multipartFile) {
        userPicsService.uploadUserPic(id, multipartFile);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     *  Get images from S3 cloud storage by IDs.
     *
     * <p>This method supports both single and multiple content metadata IDs, separated by commas</p>.
     *
     * @param contentMetadataIds a comma-separated string of ids 
     * @return Response
     * <ul>
     *     <li>200 (OK). A list of byte arrays representing the images if successful.
     *     The values are in the same order as requested ids.</li>
     *     <li>400 (BAD_REQUEST). For inputs that are not string, also if number format is incorrect</li>
     *     <li>500 (INTERNAL_SERVER_ERROR). An empty list with an error message header if an error occurs </li>
     * </ul>
     */
    @GetMapping("user-pics/{ids}")
    public ResponseEntity<List<Pair<String, byte[]>>> getUserPic(@PathVariable String ids) {
        return new ResponseEntity<>(userPicsService.getUserPics(ids), HttpStatus.OK);
    }

    /**
     * Delete user pic.
     *
     * @param id the ID of the user pic
     * @return Response:
     * <ul>
     *     <li> 204 (No Content) </li>
     * </ul>
     */
    @DeleteMapping("user-pics/{id}")
    public ResponseEntity<Void> deleteUserPic(@PathVariable String id) {
        userPicsService.deleteUserPic(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
