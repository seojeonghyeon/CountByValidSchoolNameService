package me.justin.modules.school;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SchoolServiceTest {
    private final static String HIGH_SCHOOL_NAME = "국립국악고";

    private SchoolService schoolService;

    @BeforeEach
    void beforeAll(){
        schoolService = SchoolService.getInstance();
        schoolService.clearStore();
    }

    @DisplayName("Singleton 적용 여부 확인")
    @Test
    void getInstance() {
        SchoolService oterSchoolService = SchoolService.getInstance();
        assertThat(true).isEqualTo(schoolService == oterSchoolService);
    }

    @DisplayName("save")
    @Test
    void save() {
        School school = School.createSchool(HIGH_SCHOOL_NAME);
        School saveSchool = schoolService.save(school);
        assertThat(true).isEqualTo(school.getName().equals(saveSchool.getName()));
    }

    @DisplayName("existByName")
    @Test
    void existByName() {
        School school = School.createSchool(HIGH_SCHOOL_NAME);
        School saveSchool = schoolService.save(school);
        assertThat(true).isEqualTo(schoolService.existByName(HIGH_SCHOOL_NAME));
    }

    @DisplayName("findByName")
    @Test
    void findByName() {
        School school = School.createSchool(HIGH_SCHOOL_NAME);
        School saveSchool = schoolService.save(school);
        School findSchool = schoolService.findByName(HIGH_SCHOOL_NAME);
        assertThat(true).isEqualTo(saveSchool.getName().equals(findSchool.getName()));
    }

    @DisplayName("findAllWithoutCountZero")
    @Test
    void findAllWithoutCountZero() {
        School school = School.createSchool(HIGH_SCHOOL_NAME);
        school.addCount();
        School saveSchool = schoolService.save(school);
        List<School> schoolList = schoolService.findAllWithoutCountZero();
        assertThat(true).isEqualTo(schoolList.size() == 1);
    }

    @DisplayName("findAll")
    @Test
    void findAll() {
        School school = School.createSchool(HIGH_SCHOOL_NAME);
        School saveSchool = schoolService.save(school);
        List<School> schoolList = schoolService.findAll();
        assertThat(true).isEqualTo(schoolList.size() == 1);
    }

    @DisplayName("saveSchoolList")
    @Test
    void saveSchoolList() {
        schoolService.saveSchoolList();
        List<School> schoolList = schoolService.findAll();
        assertThat(true).isEqualTo(!schoolList.isEmpty());
    }

}