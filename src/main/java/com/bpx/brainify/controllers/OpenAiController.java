package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.services.interfaces.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/openai")
@RestController
public class OpenAiController {
    private final OpenAiService openAiService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody GPTInputDTO gptInputDTO) {
        return ResponseEntity.ok(openAiService.chat(gptInputDTO));
    }

    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestBody GPTInputDTO gptInputDTO) {
        return ResponseEntity.ok(openAiService.chat(gptInputDTO));
    }
}
