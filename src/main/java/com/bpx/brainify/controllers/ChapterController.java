package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.services.interfaces.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/chapters")
@RestController
public class ChapterController {
    private final ChapterService chapterService;

    @PostMapping("/similar")
    public ResponseEntity<List<ChapterDTO>> findDocumentsBasedOnEmbedding(@RequestBody GPTInputDTO gptInputDTO) {
        return ResponseEntity.ok(chapterService.findSimilarDocuments(gptInputDTO));
    }
}
