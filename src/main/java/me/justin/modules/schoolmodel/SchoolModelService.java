package me.justin.modules.schoolmodel;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.justin.modules.csv.CsvReader;
import me.justin.modules.csv.CsvService;

import java.util.List;
import java.util.Queue;

@Slf4j
@NoArgsConstructor
public class SchoolModelService {

    private final static String UNIVERSITY_NAME= "대학교";
    private final static String HIGH_SCHOOL_NAME= "고등학교";
    private final static String MIDDLE_SCHOOL_NAME= "중학교";
    private final static String ELEMENTARY_SCHOOL_NAME= "초등학교";
    private final static String SPECIAL_SCHOOL_NAME= "특수학교";
    private final static String ETC_SCHOOL_NAME= "그외학교";

    private final SchoolModelRepository schoolModelRepository = SchoolModelRepository.getInstance();
    private final CsvService csvService = CsvService.getInstance();

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

    public Queue<SchoolModel> findAllQueueType(){return schoolModelRepository.findAllQueueType();}

    public void clearStore(){
        schoolModelRepository.clearStore();
    }

    public void persistSchoolList(){

        CsvReader specialSchoolReader = csvService.createSpecialSchoolReader();
        CsvReader etcSchoolReader = csvService.createEtcSchoolReader();
        CsvReader universityReader = csvService.createUniversityReader();
        CsvReader highSchoolReader = csvService.createHighSchoolReader();
        CsvReader middleSchoolReader = csvService.createMiddleSchoolReader();
        CsvReader elementarySchoolReader = csvService.createElementarySchoolReader();


        persistSchoolList(universityReader.getStrSchoolList());
        persistSchoolList(highSchoolReader.getStrSchoolList());
        persistSchoolList(middleSchoolReader.getStrSchoolList());
        persistSchoolList(elementarySchoolReader.getStrSchoolList());
        persistSchoolList(specialSchoolReader.getStrSchoolList());
        persistSchoolList(etcSchoolReader.getStrSchoolList());

        log.info("School List saved on repository: {}", schoolModelRepository.getClass());
    }

    public void persistSchoolList(List<String> schoolList){
        schoolList.forEach(str -> {
            log.debug("Processing save school's name - SCHOOL NAME : {}",str);
            save(SchoolModel.createSchool(str));
        });
    }

}
