package ru.storage.userpic;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.storage.utils.S3GeneralOperations;

@Service
public class UserPicsService {

    private static final String S3_FOLDER = "user_pics";

    public void uploadUserPic(String id, MultipartFile image) {
        S3GeneralOperations.uploadImage(S3_FOLDER, id, image);
    }
    
    public List<Pair<String, byte[]>> getUserPics(String ids) {
        return S3GeneralOperations.getItemsByIds(S3_FOLDER, ids);
    }
    
    public void deleteUserPic(String id) {
        S3GeneralOperations.deleteItems(S3_FOLDER, id);
    }

}
