package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;
import com.bpx.brainify.models.dtos.QuestionDTO;

import java.security.Principal;
import java.util.List;

public interface ModuleService {
    ModuleDTO addModuleToCourse(ChapterDTO chapterDTO, Long courseId, Principal connectedUser);


    List<ModuleDTO> getModulesForCourse(Long courseId);

    ChapterDTO getChapterForModule(Long moduleId);

    ModuleDTO editModule(ChapterDTO chapterDTO, Long moduleId, Long courseId, Principal connectedUser);

    String deleteModule(Long moduleId, Long courseId, Principal connectedUser);

    String generateQuestionsForModule(Long moduleId, Long courseId, Principal connectedUser, int numberOfQuestions);

    List<QuestionDTO> getQuestionsForModule(Long moduleId, int numberOfQuestions);
}
