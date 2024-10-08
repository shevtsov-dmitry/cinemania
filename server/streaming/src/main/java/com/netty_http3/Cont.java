package com.netty_http3;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * Cont
 */
@RestController
@RequestMapping("/")
public class Cont {

    @GetMapping("/")
    public Mono<ResponseEntity<Object>> answer() {
        return Mono.just(new ResponseEntity<>("lovely", HttpStatus.OK));
    }

}
