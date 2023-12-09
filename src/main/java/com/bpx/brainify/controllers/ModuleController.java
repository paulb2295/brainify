package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;
import com.bpx.brainify.models.dtos.QuestionDTO;
import com.bpx.brainify.services.interfaces.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping("courses/{courseId}")
    public ResponseEntity<List<ModuleDTO>> getAllModulesForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getModulesForCourse(courseId));
    }

    @GetMapping("{moduleId}/chapters")
    public ResponseEntity<ChapterDTO> getChapterForModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(moduleService.getChapterForModule(moduleId));
    }

    @GetMapping("/{moduleId}/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestionsForModule(@PathVariable Long moduleId,
                                                                   @RequestParam int numberOfQuestions) {
        return ResponseEntity.ok(moduleService.getQuestionsForModule(moduleId, numberOfQuestions));
    }

    @PostMapping("/adm/{courseId}")
    public ResponseEntity<ModuleDTO> createAndAddModuleToCourse(@RequestBody ChapterDTO chapterDTO,
                                                                @PathVariable Long courseId,
                                                                Principal connectedUser) {
        return ResponseEntity.ok(moduleService.addModuleToCourse(chapterDTO, courseId, connectedUser));
    }

    @PostMapping("/adm/questions/{moduleId}/{courseId}")
    public ResponseEntity<String> generateQuestionsForModule(@PathVariable Long moduleId,
                                                             @PathVariable Long courseId,
                                                             Principal connectedUser,
                                                             @RequestParam int questionNumber) {
        return ResponseEntity.ok(moduleService.generateQuestionsForModule(moduleId, courseId, connectedUser, questionNumber));
    }

    @PatchMapping("/adm/{moduleId}/{courseId}")
    public ResponseEntity<ModuleDTO> editModuleAndChapterContent(@RequestBody ChapterDTO chapterDTO,
                                                                 @PathVariable Long moduleId,
                                                                 @PathVariable Long courseId,
                                                                 Principal connectedUser) {
        return ResponseEntity.ok(moduleService.editModule(chapterDTO, moduleId, courseId, connectedUser));
    }

    @DeleteMapping("/adm/{moduleId}/{courseId}")
    public ResponseEntity<String> deleteModuleWithChapter(@PathVariable Long moduleId,
                                                          @PathVariable Long courseId,
                                                          Principal connectedUser) {
        return ResponseEntity.ok(moduleService.deleteModule(moduleId, courseId, connectedUser));
    }
}
