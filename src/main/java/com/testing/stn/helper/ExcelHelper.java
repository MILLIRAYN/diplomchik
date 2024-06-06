package com.testing.stn.helper;

import com.testing.stn.model.TempQuestion;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Question", "Option1", "Option2", "Option3", "Option4", "CorrectAnswer" };
    static String SHEET = "Tutorials";

    public static boolean hasExcelFormat(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            return fileName.endsWith(".xlsx");
        }
        return false;
    }

    public static List<TempQuestion> excelToTempQuestions(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<TempQuestion> tempQuestions = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                TempQuestion tempQuestion = new TempQuestion();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            tempQuestion.setId((long) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            tempQuestion.setQuestion(getCellValueAsString(currentCell));
                            break;
                        case 2:
                            tempQuestion.setOption1(getCellValueAsString(currentCell));
                            break;
                        case 3:
                            tempQuestion.setOption2(getCellValueAsString(currentCell));
                            break;
                        case 4:
                            tempQuestion.setOption3(getCellValueAsString(currentCell));
                            break;
                        case 5:
                            tempQuestion.setOption4(getCellValueAsString(currentCell));
                            break;
                        case 6:
                            tempQuestion.setCorrectAnswer(getCellValueAsString(currentCell));
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                tempQuestions.add(tempQuestion);
            }

            workbook.close();
            return tempQuestions;
        } catch (IOException e) {
            log.error("Error parsing Excel file", e);
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
