package me.justin.modules.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvServiceTest {

    private final static String COMMENTS_CONTENT_1 = "";
    private final static String HIGH_SCHOOL_NAME = "국립국악고";
    private final static String MIDDLE_SCHOOL_NAME = "서울대학교사범대학부설중";

    private CsvService csvService;

    @BeforeEach
    void beforeAll(){
        csvService = CsvService.getInstance();
    }

    @DisplayName("Singleton 적용 여부 확인")
    @Test
    void getInstance() {
        CsvService otherCsvService = CsvService.getInstance();
        assertThat(true).isEqualTo(csvService == otherCsvService);
    }

    @DisplayName("Comments CSV 읽기 여부 확인 - 한글과 띄어 쓰기만 남김")
    @Test
    void createCommentsReader() {
        CsvReader csvReader = csvService.createCommentsReader();
        assertThat(COMMENTS_CONTENT_1).isEqualTo(csvReader.getReadCSV().get(0));
    }

    @DisplayName("High School List CSV 읽기 여부 확인 : 접미사 '등학교' 제거")
    @Test
    void createHighSchoolReader() {
        CsvReader csvReader = csvService.createHighSchoolReader();
        assertThat(HIGH_SCHOOL_NAME).isEqualTo(csvReader.getReadCSV().get(0));
    }

    @DisplayName("Middle School List CSV 읽기 여부 확인 : 접미사 '학교' 제거")
    @Test
    void createMiddleSchoolReader() {
        CsvReader csvReader = csvService.createMiddleSchoolReader();
        assertThat(MIDDLE_SCHOOL_NAME).isEqualTo(csvReader.getReadCSV().get(0));
    }
}