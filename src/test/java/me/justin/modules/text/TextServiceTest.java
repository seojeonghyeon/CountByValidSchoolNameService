package me.justin.modules.text;

import me.justin.modules.schoolmodel.SchoolModel;
import me.justin.modules.schoolmodel.SchoolModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TextServiceTest {

    private final static String HIGH_SCHOOL_NAME = "국립국악고";
    private final static String TEXT_FILE_NAME = "output/result.txt";

    private SchoolModelService schoolModelService;
    private TextService textService;
    @BeforeEach
    void beforeAll(){
        schoolModelService = SchoolModelService.getInstance();
        textService = TextService.getInstance();
    }

    @DisplayName("Singleton 적용 여부 확인")
    @Test
    void getInstance() {
        TextService oterTextService = TextService.getInstance();
        assertThat(true).isEqualTo(textService == oterTextService);
    }

    @DisplayName("파일 생성 여부 확인")
    @Test
    void writeTextFile() {
        List<SchoolModel> schoolModelList = new ArrayList<>();
        schoolModelList.add(SchoolModel.createSchool(HIGH_SCHOOL_NAME));
        textService.writeTextFile(schoolModelList);
        File file = new File(TEXT_FILE_NAME);
        assertThat(true).isEqualTo(file.exists());
    }
}