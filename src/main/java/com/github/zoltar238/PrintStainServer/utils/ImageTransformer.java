package com.github.zoltar238.PrintStainServer.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@UtilityClass
public class ImageTransformer {
    public static String transformImageToBase64(String path) throws IOException {
        Path imagePath = Paths.get(path);
        byte[] imageByte = Files.readAllBytes(imagePath);
        return Base64.getEncoder().encodeToString(imageByte);
    }
}
