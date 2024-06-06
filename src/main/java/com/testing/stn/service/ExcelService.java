package com.testing.stn.service;

import com.testing.stn.helper.ExcelHelper;
import com.testing.stn.model.Test;
import com.testing.stn.model.Question;
import com.testing.stn.model.TempQuestion;
import com.testing.stn.repository.TestRepository;
import com.testing.stn.repository.QuestionRepository;
import com.testing.stn.repository.TempQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ExcelService {

    @Autowired
    TestRepository testRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TempQuestionRepository tempQuestionRepository;

    public void save(MultipartFile file, Test testDetails) {
        try {
            log.info("Starting to parse the file: {}", file.getOriginalFilename());
            List<TempQuestion> tempQuestions = ExcelHelper.excelToTempQuestions(file.getInputStream());
            log.info("Parsed {} questions from the file", tempQuestions.size());

            // Сохраняем временные вопросы в базу данных
            tempQuestionRepository.saveAll(tempQuestions);

            // Перемещаем данные из временной таблицы в основную
            List<Question> questions = tempQuestions.stream().map(tempQuestion -> {
                Question question = new Question();
                question.setQuestion(tempQuestion.getQuestion());
                question.setOption1(tempQuestion.getOption1());
                question.setOption2(tempQuestion.getOption2());
                question.setOption3(tempQuestion.getOption3());
                question.setOption4(tempQuestion.getOption4());
                question.setCorrectAnswer(tempQuestion.getCorrectAnswer());
                question.setTest(testDetails);
                return question;
            }).collect(Collectors.toList());

            // Устанавливаем тест для каждого вопроса
            questions.forEach(question -> question.setTest(testDetails));

            // Сохраняем тест и его вопросы
            testRepository.save(testDetails);
            questionRepository.saveAll(questions);

            // Очищаем временную таблицу
            tempQuestionRepository.deleteAll();

            log.info("Successfully saved test and questions to the database");
        } catch (IOException e) {
            log.error("Failed to store excel data", e);
            throw new RuntimeException("Fail to store excel data: " + e.getMessage());
        }
    }

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public List<Question> getQuestionsByTestId(Long testId) {
        return questionRepository.findByTestId(testId);
    }
}
