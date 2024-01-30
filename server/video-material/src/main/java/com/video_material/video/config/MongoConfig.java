<<<<<<<< HEAD:server/video/video-material/src/main/java/ru/video_material/config/MongoConfig.java
package ru.video_material.config;
========
package com.video_material.video.config;
>>>>>>>> add-film-form-fix:server/video-material/src/main/java/com/video_material/video/config/MongoConfig.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
//@EnableMongoRepositories
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final MappingMongoConverter mongoConverter;

    @Autowired
    public MongoConfig(@Lazy MappingMongoConverter mongoConverter) {
        this.mongoConverter = mongoConverter;
    }

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return new GridFsTemplate(mongoDbFactory(), mongoConverter);
    }

    @Override
    protected String getDatabaseName() {
        return "video_storage_gridfs";
    }
}
