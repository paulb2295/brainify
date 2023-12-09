package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.models.dtos.QuestionDTO;

import java.util.List;

public interface QuestionService {
    String createQuestions(GPTInputDTO gptInputDTO, String chapterId);

    List<QuestionDTO> getQuestions(String chapterID, int numberOfQuestions);

    long returnNumberOfQuestionsFoChapter(String chapterId);
}
