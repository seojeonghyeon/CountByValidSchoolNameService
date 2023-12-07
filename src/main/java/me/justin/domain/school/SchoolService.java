package me.justin.domain.school;

import lombok.extern.slf4j.Slf4j;
import me.justin.domain.csv.CsvReader;
import me.justin.domain.csv.CsvService;

import java.util.List;

@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository = SchoolRepository.getInstance();
    private final CsvService csvService = CsvService.getInstance();

    private SchoolService(){}

    private static class SchoolServiceHelper {
        private static final SchoolService SCHOOL_SERVICE = new SchoolService();
    }

    public static SchoolService getInstance(){
        return SchoolServiceHelper.SCHOOL_SERVICE;
    }

    public School save(School school){
        return schoolRepository.save(school);
    }

    public boolean existByName(String name){
        return schoolRepository.existByName(name);
    }

    public School findByName(String name){
        return schoolRepository.findByName(name).orElse(null);
    }

    public List<School> findAllWithoutCountZero(){
        return schoolRepository.findAllWithoutCountZero();
    }

    public List<School> findAll(){return schoolRepository.findAll();}

    public void saveSchoolList(){
        CsvReader highSchoolReader = csvService.createHighSchoolReader();
        CsvReader middleSchoolReader = csvService.createMiddleSchoolReader();

        saveSchoolList(highSchoolReader.getReadCSV());
        saveSchoolList(middleSchoolReader.getReadCSV());

        log.info("School List saved on repository: {}", schoolRepository.getClass());
    }

    public void saveSchoolList(List<String> schoolList){
        schoolList.forEach(str -> {
            log.debug("Processing save school's name - SCHOOL NAME : {}",str);
            save(School.createSchool(str));
        });
    }

}
