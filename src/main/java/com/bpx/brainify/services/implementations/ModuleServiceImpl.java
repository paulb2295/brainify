package com.bpx.brainify.services.implementations;

import com.bpx.brainify.enums.GptActions;
import com.bpx.brainify.exceptions.ModuleNotFoundException;
import com.bpx.brainify.exceptions.NullRequiredValueException;
import com.bpx.brainify.exceptions.ResourceNotFoundException;
import com.bpx.brainify.exceptions.UserHasNoCoursesException;
import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;
import com.bpx.brainify.models.dtos.QuestionDTO;
import com.bpx.brainify.models.entities.Course;
import com.bpx.brainify.models.entities.Module;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.repositories.CourseRepository;
import com.bpx.brainify.repositories.ModuleRepository;
import com.bpx.brainify.services.interfaces.ChapterService;
import com.bpx.brainify.services.interfaces.ModuleService;
import com.bpx.brainify.services.interfaces.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ChapterService chapterService;
    private final QuestionService questionService;
    private final ObjectMapper objectMapper;

    @Override
    public ModuleDTO addModuleToCourse(ChapterDTO chapterDTO, Long courseId, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            if (verifyUserIdForCourse(course.getId(), user.getId())) {
                Module module = objectMapper.convertValue(createModule(chapterDTO), Module.class);
                module.setCourse(course);
                moduleRepository.save(module);
                return objectMapper.convertValue(module, ModuleDTO.class);
            } else {
                throw new UserHasNoCoursesException("Access denied, this user is not the course Instructor!");
            }
        } else {
            throw new ResourceNotFoundException("This course does not exist!");
        }
    }

    private ModuleDTO createModule(ChapterDTO chapterDTO) {
        ChapterDTO chapterDTOResponse = chapterService.createChapter(chapterDTO);
        Module module = Module.builder()
                .chapterDocumentId(chapterDTOResponse.getId())
                .moduleName(chapterDTOResponse.getTitle())
                .build();
        return objectMapper.convertValue(moduleRepository.save(module), ModuleDTO.class);
    }

    private boolean verifyUserIdForCourse(Long courseId, Long userId) {
        List<Long> usersIdsForCourse = courseRepository.userIdsForCourse(courseId);
        for (Long id : usersIdsForCourse) {
            if (id.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ModuleDTO> getModulesForCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course does not exist!");
        }
        List<Module> modules = moduleRepository.findModulesByCourseId(courseId);
        if (!modules.isEmpty()) {
            return modules.stream()
                    .map(module -> objectMapper.convertValue(module, ModuleDTO.class))
                    .toList();
        }
        throw new ModuleNotFoundException("This course has no modules yet!");
    }

    @Override
    public ChapterDTO getChapterForModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("The requested module does not exist!"));
        return chapterService.getChapter(module.getChapterDocumentId());
    }

    @Override
    public ModuleDTO editModule(ChapterDTO chapterDTO, Long moduleId, Long courseId, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent() && verifyCourseIdForModule(courseId, moduleId)) {
            Course course = courseOptional.get();
            if (verifyUserIdForCourse(course.getId(), user.getId())) {
                Module module = moduleRepository.findById(moduleId).orElseThrow(
                        () -> new ResourceNotFoundException("Module does not exist!")
                );
                if (chapterDTO.getTitle() != null && !chapterDTO.getTitle().isEmpty()) {
                    module.setModuleName(chapterDTO.getTitle());
                } else {
                    throw new NullRequiredValueException("Invalid title");
                }
                if (chapterDTO.getContent() != null && !chapterDTO.getContent().isEmpty()) {
                    ChapterDTO chapterDTOResponse = chapterService.createChapter(chapterDTO);
                    module.setChapterDocumentId(chapterDTOResponse.getId());
                } else {
                    throw new NullRequiredValueException("Invalid content");
                }
                return objectMapper.convertValue(module, ModuleDTO.class);

            } else {
                throw new UserHasNoCoursesException("Access denied, this user is not the course Instructor!");
            }
        } else {
            throw new ResourceNotFoundException("This course does not exist!");
        }
    }

    private boolean verifyCourseIdForModule(Long courseId, Long moduleId) {
        List<Long> moduleIdListFoCourse = moduleRepository.findModulesIdsForCourseId(courseId);
        for (Long id : moduleIdListFoCourse) {
            if (moduleId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String deleteModule(Long moduleId, Long courseId, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (verifyUserIdForCourse(courseId, user.getId())) {
            Optional<Module> moduleOptional = moduleRepository.findById(moduleId);
            if (moduleOptional.isPresent() && verifyCourseIdForModule(courseId, moduleId)) {
                Module module = moduleOptional.get();
                String message = chapterService.deleteChapter(module.getChapterDocumentId());
                moduleRepository.deleteById(moduleId);
                return message;
            }
            throw new ResourceNotFoundException("Module does not exist");
        }
        throw new UserHasNoCoursesException("User in not course's instructor!");
    }

    @Override
    public String generateQuestionsForModule(Long moduleId, Long courseId, Principal connectedUser, int numberOfQuestions) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (verifyUserIdForCourse(courseId, user.getId())) {
            Optional<Module> moduleOptional = moduleRepository.findById(moduleId);
            if (moduleOptional.isPresent() && verifyCourseIdForModule(courseId, moduleId)) {
                Module module = moduleOptional.get();
                GPTInputDTO gptInputDTO = new GPTInputDTO();
                gptInputDTO.setInput(chapterService.getChapter(module.getChapterDocumentId()).getContent());
                gptInputDTO.setAction(GptActions.QUESTIONS);
                gptInputDTO.setQuestionNumber(numberOfQuestions);
                return questionService.createQuestions(gptInputDTO, module.getChapterDocumentId());
            }
            throw new ResourceNotFoundException("Module does not exist");
        }
        throw new UserHasNoCoursesException("User in not course's instructor!");
    }

    @Override
    public List<QuestionDTO> getQuestionsForModule(Long moduleId, int numberOfQuestions) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("The requested module does not exist!"));
        return questionService.getQuestions(module.getChapterDocumentId(), numberOfQuestions);
    }
}