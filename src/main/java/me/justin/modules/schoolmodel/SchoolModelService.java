package me.justin.modules.schoolmodel;

import lombok.extern.slf4j.Slf4j;
import me.justin.modules.csv.CsvReader;
import me.justin.modules.csv.CsvService;

import java.util.List;

@Slf4j
public class SchoolModelService {
    private final SchoolModelRepository schoolModelRepository = SchoolModelRepository.getInstance();
    private final CsvService csvService = CsvService.getInstance();

    private SchoolModelService(){}

    private static class SchoolServiceHelper {
        private static final SchoolModelService SCHOOL_SERVICE = new SchoolModelService();
    }

    public static SchoolModelService getInstance(){
        return SchoolServiceHelper.SCHOOL_SERVICE;
    }

    public SchoolModel save(SchoolModel schoolModel){
        return schoolModelRepository.save(schoolModel);
    }

    public boolean existByName(String name){
        return schoolModelRepository.existByName(name);
    }

    public SchoolModel findByName(String name){
        return schoolModelRepository.findByName(name).orElse(null);
    }

    public List<SchoolModel> findAllWithoutCountZero(){
        return schoolModelRepository.findAllWithoutCountZero();
    }

    public List<SchoolModel> findAll(){return schoolModelRepository.findAll();}

    public void clearStore(){
        schoolModelRepository.clearStore();
    }

    public void persistSchoolList(){
        CsvReader highSchoolReader = csvService.createHighSchoolReader();
        CsvReader middleSchoolReader = csvService.createMiddleSchoolReader();

        persistSchoolList(highSchoolReader.getReadCSV());
        persistSchoolList(middleSchoolReader.getReadCSV());

        log.info("School List saved on repository: {}", schoolModelRepository.getClass());
    }

    public void persistSchoolList(List<String> schoolList){
        schoolList.forEach(str -> {
            log.debug("Processing save school's name - SCHOOL NAME : {}",str);
            save(SchoolModel.createSchool(str));
        });
    }

}
