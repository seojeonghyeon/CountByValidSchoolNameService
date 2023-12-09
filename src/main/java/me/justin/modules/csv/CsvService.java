package me.justin.modules.csv;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class CsvService {
    private static final String COMMENTS_CSV_FILE_NAME = "src/main/resources/csv/comments.csv";
    private static final String HIGH_SCHOOL_LIST_FILE_NAME = "src/main/resources/csv/2023년도_전국고등학교_학교명.csv";
    private static final String MIDDLE_SCHOOL_LIST_FILE_NAME = "src/main/resources/csv/2023년도_전국중학교_학교명.csv";

    private static class CsvServiceHelper {
        private static final CsvService CSV_SERVICE = new CsvService();
    }

    public static CsvService getInstance(){
        return CsvServiceHelper.CSV_SERVICE;
    }

    public CsvReader createCommentsReader(){
        return CsvReader.createCsvReader(COMMENTS_CSV_FILE_NAME);
    }

    public CsvReader createHighSchoolReader(){
        log.debug("Processing create High School CSV Reader - File Name: {}", HIGH_SCHOOL_LIST_FILE_NAME);
        return CsvReader.createCsvReader(HIGH_SCHOOL_LIST_FILE_NAME);
    }

    public CsvReader createMiddleSchoolReader(){
        log.debug("Processing create Middle School CSV Reader - File Name: {}", MIDDLE_SCHOOL_LIST_FILE_NAME);
        return CsvReader.createCsvReader(MIDDLE_SCHOOL_LIST_FILE_NAME);
    }

}
