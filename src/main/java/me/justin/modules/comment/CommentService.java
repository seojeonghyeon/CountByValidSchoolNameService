package me.justin.modules.comment;


import lombok.extern.slf4j.Slf4j;
import me.justin.modules.csv.CsvReader;
import me.justin.modules.csv.CsvService;
import me.justin.modules.school.School;
import me.justin.modules.school.SchoolService;

import java.util.List;

@Slf4j
public class CommentService {
    private final CsvService csvService = CsvService.getInstance();
    private final SchoolService schoolService = SchoolService.getInstance();

    private CommentService(){}

    private static class CommentServiceHelper {
        private static final CommentService COMMENT_SERVICE = new CommentService();
    }

    public static CommentService getInstance(){
        return CommentServiceHelper.COMMENT_SERVICE;
    }

    public void extractSchoolNameFromCSV(){
        CsvReader commentReader = csvService.createCommentsReader();
        List<String> commentReaderReadCSV = commentReader.getReadCSV();
        List<School> schoolList = schoolService.findAll();
        schoolList.forEach(
                school -> commentReaderReadCSV
                        .stream()
                        .filter(str -> isEqualsToSchoolName(str, school.getName()))
                        .forEach(str -> {
                            log.debug("Processing count up by school's name - SCHOOL NAME : {}, COMMENT : {}", school.getName(), str);
                            school.addCount();
                        })
        );
        log.info("Valid School List was count by school from comments");
    }

    public boolean isEqualsToSchoolName(String str, String schoolName){
        if (str.contains(schoolName)){
            int index = str.indexOf(schoolName);
            boolean isOtherMean = index >= 1 && str.charAt(index-1)!=' ' && index >=2 && str.charAt(index-2)==' ';
            return !isOtherMean;
        }
        return false;
    }
}
