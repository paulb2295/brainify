package com.bpx.brainify.services.implementations;

import com.bpx.brainify.exceptions.NullRequiredValueException;
import com.bpx.brainify.exceptions.ResourceNotFoundException;
import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.GPTInputDTO;
import com.bpx.brainify.models.entities.Chapter;
import com.bpx.brainify.repositories.ChapterRepository;
import com.bpx.brainify.services.interfaces.ChapterService;
import com.bpx.brainify.services.interfaces.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final OpenAiService openAiService;

    @Override
    public ChapterDTO createChapter(ChapterDTO chapterDTO) {
        if ((chapterDTO.getTitle() == null || chapterDTO.getTitle().isEmpty())
                || chapterDTO.getContent() == null || chapterDTO.getContent().isEmpty()) {
            throw new NullRequiredValueException("Invalid input!");
        }
        Chapter chapter = objectMapper.convertValue(chapterDTO, Chapter.class);
        Chapter chapterResponse = chapterRepository.save(chapter);
        return objectMapper.convertValue(chapterResponse, ChapterDTO.class);
    }

    @Override
    public ChapterDTO getChapter(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The requested chapter does not exist!"));
        return objectMapper.convertValue(chapter, ChapterDTO.class);
    }


    @Override
    public ChapterDTO editChapter(String id, ChapterDTO chapterDTO) {
        if ((chapterDTO.getTitle() == null || chapterDTO.getTitle().isEmpty())
                || chapterDTO.getContent() == null || chapterDTO.getContent().isEmpty()) {
            throw new NullRequiredValueException("Invalid input!");
        }
        if (chapterRepository.existsById(id)) {
            chapterRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("The requested chapter does not exist!");
        }
        return createChapter(chapterDTO);
    }

    @Override
    public String deleteChapter(String id) {
        if (chapterRepository.existsById(id)) {
            chapterRepository.deleteById(id);
            return "Chapter deleted successfully!";
        }
        throw new ResourceNotFoundException("The requested chapter does not exist!");
    }

    @Override
    public List<ChapterDTO> findSimilarDocuments(GPTInputDTO gptInputDTO) {
        List<Double> embedding = openAiService.getEmbedding(gptInputDTO);
        AggregationOperation vectorSearchOperation = context -> context.getMappedObject(
                Document.parse("{ $vectorSearch: { queryVector: " + embedding + ", path: 'content_embedding', numCandidates: 100, limit: 2, index: 'chaptersContentIndex' } }")
        );
        Aggregation aggregation = Aggregation.newAggregation(vectorSearchOperation);
        AggregationResults<ChapterDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "chapters", ChapterDTO.class);
        return aggregationResults.getMappedResults();
    }

}
