package com.bpx.brainify.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("questions")
public class Question {
    @Id
    private String id;

    @Field("chapter_id")
    private String chapterId;

    @Field("question_body")
    private String questionBody;

    @Field("answer_a")
    private String answerA;

    @Field(name = "answer_b")
    private String answerB;

    @Field(name = "answer_c")
    private String answerC;

    @Field(name = "answer_d")
    private String answerD;

    @Field(name = "answer_e")
    private String answerE;

    @Field(name = "correct_answers")
    private List<Character> correctAnswers;

}
