package com.bpx.brainify.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document("chapters")
public class Chapter {
    @Id
    private String id;
    private String title;
    private String content;
    @Field("content_embedding")
    private Double[] contentEmbedding;

    public Chapter(String id, String title, String content) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
