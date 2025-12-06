package com.hossainrion.ReactSocial;

import io.micrometer.common.util.StringUtils;

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
    private static final String FILE_PATH = "/home/hossain/Desktop/";

    /**
     * @param fileBase64 base64 file
     * @return The filename
     */
    public static String savePicture(String fileBase64) {
        if (StringUtils.isNotBlank(fileBase64)) {
            String fileName = UUID.randomUUID().toString() + ".png";
            try {
                writeBase64ToFile(fileBase64, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return fileName;
        }
        return null;
    }

    public static void deleteFile(String fileName) {
        try {
            Files.delete(Paths.get(FILE_PATH + fileName));
            System.out.println("File deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMedia(String fileName) {
        try {
            if (fileName.endsWith(".png")) {
                Files.delete(Paths.get(FILE_PATH + "photos/" + fileName));
            } else if (fileName.endsWith(".mp4")) {
                Files.delete(Paths.get(FILE_PATH + "videos/" + fileName));
            }
            System.out.println("File deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeBase64ToFile(String base64Data, String fileName) throws IOException {

        // Decode Base64 string to byte array
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);

        // Write byte array to file
        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH+fileName)) {
            fileOutputStream.write(fileBytes);
        }
    }

    public static String imageUrlToBase64(String fileName) {
        try (InputStream in = new URL("file://" + FILE_PATH + fileName).openStream()) {
            byte[] bytes = in.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read image: " + e.getMessage(), e);
        }
    }

    public static Boolean pictureExists(String fileName) {
        File file = new File(FILE_PATH + fileName);
        return file.exists();
    }
}
