package com.hossainrion.ReactSocial.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hossainrion.ReactSocial.messaging.SessionManager;
import io.micrometer.common.util.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

public class Util {
    private static final String PROFILE_PICTURE_PATH = "/home/hossain/Desktop/profile_pictures/";
    private static final String MEDIA_PHOTOS_PATH = "/home/hossain/Desktop/photos/";
    private static final String MEDIA_VIDEOS_PATH = "/home/hossain/Desktop/videos/";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * @param fileBase64 base64 file
     * @return The filename
     */
    public static String saveProfilePicture(String fileBase64) {
        if (StringUtils.isNotBlank(fileBase64)) {
            String fileName = UUID.randomUUID() + ".png";
            try {
                writeBase64ToFile(fileBase64, fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }
        return null;
    }

    public static void deleteProfilePicture(String fileName) {
        if (fileName != null && !fileName.isEmpty() && new File(PROFILE_PICTURE_PATH + fileName).exists()) {
            try {
                Files.delete(Paths.get(PROFILE_PICTURE_PATH + fileName));
                System.out.println("File deleted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteMedia(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                if (fileName.endsWith(".png") && new File(MEDIA_PHOTOS_PATH + fileName).exists()) {
                    Files.delete(Paths.get(MEDIA_PHOTOS_PATH + fileName));
                }
                if (fileName.endsWith(".mp4") && new File(MEDIA_VIDEOS_PATH + fileName).exists()) {
                    Files.delete(Paths.get(MEDIA_VIDEOS_PATH + fileName));
                }
                System.out.println("File deleted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeBase64ToFile(String base64Data, String fileName) throws IOException {

        // Decode Base64 string to byte array
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);

        // Write byte array to file
        try (FileOutputStream fileOutputStream = new FileOutputStream(PROFILE_PICTURE_PATH+fileName)) {
            fileOutputStream.write(fileBytes);
        }
    }

    public static String imageUrlToBase64(String fileName) {
        if (fileName != null && !fileName.isEmpty() && new File(PROFILE_PICTURE_PATH + fileName).exists()) {
            try (InputStream in = new URL("file://" + PROFILE_PICTURE_PATH+ fileName).openStream()) {
                byte[] bytes = in.readAllBytes();
                return Base64.getEncoder().encodeToString(bytes);
            } catch (Exception e) {
                System.out.println("Failed to read image: " + e.getMessage());
                return "";
            }
        }
        return "";
    }

    public static String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T toObject(String jsonString, Class<T> clazz) {
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

}
