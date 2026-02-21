package com.ai.resumematch.util;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfParserUtil {
    private final Tika tika = new Tika();

    /**
     * Parses the uploaded PDF file into a plain text string.
     * Added TikaException to the throws clause to resolve the compilation error.
     */
    public String parse(MultipartFile file) throws IOException, TikaException {
        return tika.parseToString(file.getInputStream());
    }
}