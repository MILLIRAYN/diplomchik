package com.testing.stn.repository;

import com.testing.stn.model.TempQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempQuestionRepository extends JpaRepository<TempQuestion, Long> {
}
