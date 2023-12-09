package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.GPTInputDTO;

import java.util.List;

public interface ChapterService {
    ChapterDTO createChapter(ChapterDTO chapterDTO);

    ChapterDTO getChapter(String id);

    ChapterDTO editChapter(String id, ChapterDTO chapterDTO);

    String deleteChapter(String id);

    List<ChapterDTO> findSimilarDocuments(GPTInputDTO gptInputDTO);
}
