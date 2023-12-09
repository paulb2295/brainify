package com.bpx.brainify.services.implementations;

import com.bpx.brainify.exceptions.InvalidNumberOfQuestionsException;
import com.bpx.brainify.exceptions.NoQuestionsForChapterException;
import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.models.dtos.QuestionDTO;
import com.bpx.brainify.models.entities.Question;
import com.bpx.brainify.repositories.QuestionRepository;
import com.bpx.brainify.services.interfaces.OpenAiService;
import com.bpx.brainify.services.interfaces.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    @Override
    public String createQuestions(GPTInputDTO gptInputDTO, String chapterId) {
        String gptOutput = openAiService.chat(gptInputDTO);
        List<List<String>> questions2DList = parseLargeString(gptOutput);
        List<QuestionDTO> questionList = parseQuestions(questions2DList);
        for (QuestionDTO questionDTO : questionList) {
            Question question = Question.builder()
                    .chapterId(chapterId)
                    .questionBody(questionDTO.getQuestionBody())
                    .answerA(questionDTO.getAnswerA())
                    .answerB(questionDTO.getAnswerB())
                    .answerC(questionDTO.getAnswerC())
                    .answerD(questionDTO.getAnswerD())
                    .answerE(questionDTO.getAnswerE())
                    .correctAnswers(questionDTO.getCorrectAnswers())
                    .build();
            questionRepository.save(question);
        }
        return "Questions successfully added!";
    }


    private List<String> parseString(String text) {
        StringBuilder textBuilder = new StringBuilder(text);
        List<String> stringList = new ArrayList<>();
        while (textBuilder.indexOf("[") != -1 && textBuilder.indexOf("]") != -1) {
            stringList.add(textBuilder.substring(textBuilder.indexOf("[") + 1, textBuilder.indexOf("]")));
            textBuilder.delete(0, textBuilder.indexOf("]") + 1);
        }
        return stringList;
    }

    private List<List<String>> parseLargeString(String input) {
        StringBuilder textBuilder = new StringBuilder(input);
        List<List<String>> stringList = new ArrayList<>();
        while (textBuilder.indexOf("$") != -1) {
            List<String> subList = parseString(textBuilder.substring(0, textBuilder.indexOf("$")));
            stringList.add(subList);
            textBuilder.delete(0, textBuilder.indexOf("$") + 1);
        }
        return stringList;
    }

    private List<QuestionDTO> parseQuestions(List<List<String>> questionsList) {
        List<QuestionDTO> resultQuestionsList = new ArrayList<>();
        for (List<String> innerList : questionsList) {
            QuestionDTO questionDTO = QuestionDTO.builder()
                    .questionBody(innerList.get(0))
                    .answerA(innerList.get(1))
                    .answerB(innerList.get(2))
                    .answerC(innerList.get(3))
                    .answerD(innerList.get(4))
                    .answerE(innerList.get(5))
                    .correctAnswers(correctAnswers(innerList.get(6)))
                    .build();
            resultQuestionsList.add(questionDTO);
        }
        return resultQuestionsList;
    }

    private List<Character> correctAnswers(String answers) {
        char[] array = answers.toCharArray();
        List<Character> corectAnswersList = new ArrayList<>();
        for (char answer : array) {
            corectAnswersList.add(answer);
        }
        return corectAnswersList;
    }


    @Override
    public List<QuestionDTO> getQuestions(String chapterID, int numberOfQuestions) {
        List<Question> questions = new ArrayList<>();
        long actualNumberOfQuestions = questionRepository.countByChapterId(chapterID);
        if (actualNumberOfQuestions <= 0) {
            throw new NoQuestionsForChapterException("This chapter has no questions yet!");
        }
        if (numberOfQuestions >= actualNumberOfQuestions) {
            questions = questionRepository.findByChapterId(chapterID);
        } else if (numberOfQuestions > 0 && numberOfQuestions < actualNumberOfQuestions) {
            Pageable pageable = PageRequest.of(0, numberOfQuestions);
            questions = questionRepository.findByChapterId(chapterID, pageable);
        } else if (numberOfQuestions <= 0) {
            throw new InvalidNumberOfQuestionsException("Invalid number of questions requested!");
        }
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDTO = QuestionDTO.builder()
                    .questionBody(question.getQuestionBody())
                    .answerA(question.getAnswerA())
                    .answerB(question.getAnswerB())
                    .answerC(question.getAnswerC())
                    .answerD(question.getAnswerD())
                    .answerE(question.getAnswerE())
                    .correctAnswers(question.getCorrectAnswers())
                    .build();
            questionDTOS.add(questionDTO);
        }
        return questionDTOS;
    }

    @Override
    public long returnNumberOfQuestionsFoChapter(String chapterId) {
        return questionRepository.countByChapterId(chapterId);
    }

}
