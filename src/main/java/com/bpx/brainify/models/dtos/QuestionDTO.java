package com.bpx.brainify.models.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String questionBody;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String answerE;
    private List<Character> correctAnswers;
}
