package ru.github.musiccrossing.storage.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.github.musiccrossing.storage.exception.FailedEncodeImageException;
import ru.github.musiccrossing.storage.exception.FileIsNotValidImageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageConvertService {

    public byte[] convertToWebp(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new FileIsNotValidImageException();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            boolean success = ImageIO.write(image, "webp", baos);

            if (!success) {
                throw new FailedEncodeImageException();
            }

            return baos.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Ошибка при чтении файла");
        }
    }
}
