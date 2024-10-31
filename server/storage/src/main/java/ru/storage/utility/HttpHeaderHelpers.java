package ru.storage.utility;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;

/**
 * HttpHeaderHelpers
 */
public class HttpHeaderHelpers {

    private HttpHeaderHelpers() {
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
