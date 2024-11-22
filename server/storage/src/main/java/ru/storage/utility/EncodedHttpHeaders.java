package ru.storage.utility;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class needed to create transferable non ASCII text on http headers
 */
@EqualsAndHashCode(callSuper = true)
public class EncodedHttpHeaders extends HttpHeaders {

    private final HttpHeaders headers;

    /**
     * Constructor with "Message" header.
     *
     * @param message text content which will be encoded with URLEncoder class on UTF-8 charset
     */
    public EncodedHttpHeaders(String message) {
        this.headers = new HttpHeaders();
        writeEncodedMessageHeader(headers, message);
    }

    /**
     * Utility which helps to write encoded message into headers with UTF-8
     * support.
     *
     * @param headers - headers where message will be saved
     * @param message - UTF-8 message
     */
    public static void writeEncodedMessageHeader(HttpHeaders headers, String message) {
        headers.set("Message", URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
