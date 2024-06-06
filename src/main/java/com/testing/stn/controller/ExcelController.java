package com.testing.stn.controller;

import com.testing.stn.helper.ExcelHelper;
import com.testing.stn.message.ResponseMessage;
import com.testing.stn.model.Test;
import com.testing.stn.model.Question;
import com.testing.stn.service.ExcelService;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
@Log4j2
public class ExcelController {

    @Autowired
    ExcelService fileService;

    @GetMapping("/upload")
    public ModelAndView showUploadForm() {
        return new ModelAndView("upload");
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("deadline") String deadline,
            @RequestParam("duration") int duration) {

        System.out.println(file.getOriginalFilename());
        String message = "";
        log.info("TYPE: {}", file.getContentType());

        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                Test testDetails = new Test();
                testDetails.setName(name);
                testDetails.setDeadline(LocalDate.parse(deadline));
                testDetails.setDuration(duration);

                fileService.save(file, testDetails);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                log.error("Error uploading file", e);
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @GetMapping("/tests")
    public ResponseEntity<List<Test>> getAllTests() {
        try {
            List<Test> tests = fileService.getAllTests();
            if (tests.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/questions/{testId}")
    public ResponseEntity<List<Question>> getQuestionsByTestId(@PathVariable Long testId) {
        try {
            List<Question> questions = fileService.getQuestionsByTestId(testId);
            if (questions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
