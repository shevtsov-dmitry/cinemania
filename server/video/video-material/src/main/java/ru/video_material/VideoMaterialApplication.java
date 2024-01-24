package ru.video_material;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"video_material"})
public class VideoMaterialApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoMaterialApplication.class, args);

    }
}
