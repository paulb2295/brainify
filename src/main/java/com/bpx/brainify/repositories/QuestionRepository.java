package com.bpx.brainify.repositories;

import com.bpx.brainify.models.entities.Chapter;
import com.bpx.brainify.models.entities.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    long countByChapterId(String chapterId);

    @Query("{'chapter_id': ?0}")
    List<Question> findByChapterId(String chapterId);

    @Query("{'chapter_id': ?0}")
    List<Question> findByChapterId(String chapterId, Pageable pageable);

    public long count();
}
