package me.justin.modules.schoolmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SchoolModelServiceTest {
    private final static String HIGH_SCHOOL_NAME = "국립국악고";

    private SchoolModelService schoolModelService;

    @BeforeEach
    void beforeAll(){
        schoolModelService = SchoolModelService.getInstance();
        schoolModelService.clearStore();
    }

    @DisplayName("Singleton 적용 여부 확인")
    @Test
    void getInstance() {
        SchoolModelService oterSchoolModelService = SchoolModelService.getInstance();
        assertThat(true).isEqualTo(schoolModelService == oterSchoolModelService);
    }

    @DisplayName("save")
    @Test
    void save() {
        SchoolModel schoolModel = SchoolModel.createSchool(HIGH_SCHOOL_NAME);
        SchoolModel saveSchoolModel = schoolModelService.save(schoolModel);
        assertThat(true).isEqualTo(schoolModel.getName().equals(saveSchoolModel.getName()));
    }

    @DisplayName("existByName")
    @Test
    void existByName() {
        SchoolModel schoolModel = SchoolModel.createSchool(HIGH_SCHOOL_NAME);
        SchoolModel saveSchoolModel = schoolModelService.save(schoolModel);
        assertThat(true).isEqualTo(schoolModelService.existByName(HIGH_SCHOOL_NAME));
    }

    @DisplayName("findByName")
    @Test
    void findByName() {
        SchoolModel schoolModel = SchoolModel.createSchool(HIGH_SCHOOL_NAME);
        SchoolModel saveSchoolModel = schoolModelService.save(schoolModel);
        SchoolModel findSchoolModel = schoolModelService.findByName(HIGH_SCHOOL_NAME);
        assertThat(true).isEqualTo(saveSchoolModel.getName().equals(findSchoolModel.getName()));
    }

    @DisplayName("findAllWithoutCountZero")
    @Test
    void findAllWithoutCountZero() {
        SchoolModel schoolModel = SchoolModel.createSchool(HIGH_SCHOOL_NAME);
        schoolModel.addCount();
        SchoolModel saveSchoolModel = schoolModelService.save(schoolModel);
        List<SchoolModel> schoolModelList = schoolModelService.findAllWithoutCountZero();
        assertThat(true).isEqualTo(schoolModelList.size() == 1);
    }

    @DisplayName("findAll")
    @Test
    void findAll() {
        SchoolModel schoolModel = SchoolModel.createSchool(HIGH_SCHOOL_NAME);
        SchoolModel saveSchoolModel = schoolModelService.save(schoolModel);
        List<SchoolModel> schoolModelList = schoolModelService.findAll();
        assertThat(true).isEqualTo(schoolModelList.size() == 1);
    }

    @DisplayName("saveSchoolList")
    @Test
    void saveSchoolList() {
        schoolModelService.persistSchoolList();
        List<SchoolModel> schoolModelList = schoolModelService.findAll();
        assertThat(true).isEqualTo(!schoolModelList.isEmpty());
    }

}