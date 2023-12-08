package me.justin.modules.comment;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.justin.modules.csv.CsvReader;
import me.justin.modules.csv.CsvService;
import me.justin.modules.schoolmodel.SchoolModel;
import me.justin.modules.schoolmodel.SchoolModelService;

import java.util.List;

@Slf4j
@NoArgsConstructor
public class CommentService {
    private final CsvService csvService = CsvService.getInstance();
    private final SchoolModelService schoolModelService = SchoolModelService.getInstance();


    private static class CommentServiceHelper {
        private static final CommentService COMMENT_SERVICE = new CommentService();
    }

    public static CommentService getInstance(){
        return CommentServiceHelper.COMMENT_SERVICE;
    }

    public void extractValidSchoolNameFromCommentCSV(){
        CsvReader commentReader = csvService.createCommentsReader();
        List<String> commentReaderReadCSV = commentReader.getReadCSV();
        List<SchoolModel> schoolModelList = schoolModelService.findAll();
        schoolModelList.forEach(
                school -> commentReaderReadCSV
                        .stream()
                        .filter(comment -> contains(comment, school.getName()))
                        .forEach(comment -> {
                            log.debug("Processing count up by school's name - SCHOOL NAME : {}, COMMENT : {}", school.getName(), comment);
                            school.addCount();
                        })
        );
        log.info("Valid School List was count by school from comments");
    }

    public boolean contains(String comment, String schoolName){
        String male = "남자";
        String female = "여자";
        boolean isOneGenderSchool = schoolName.contains(male) || schoolName.contains(female);
        boolean result = isContainsSchoolNameInComment(comment, schoolName);
        if(!result && isOneGenderSchool){
            result = isContainsOneGenderSchoolInComment(schoolName.contains(male), comment, schoolName);
        }
        return result;
    }
    private boolean isContainsOneGenderSchoolInComment(boolean isMale, String comment, String schoolName){
        String gender = isMale ? "남" : "여";
        String[] splitStr =schoolName.split(isMale ? "남자" : "여자");
        schoolName = splitStr[0] + gender + splitStr[1];
        return isContainsSchoolNameInComment(comment, schoolName);
    }

    private boolean isContainsSchoolNameInComment(String comment, String schoolName){
        char space = ' ';
        if (comment.contains(schoolName)){
            int index = comment.indexOf(schoolName);
            boolean isOtherMeanNotSchoolName = index >= 1 && comment.charAt(index-1)!=space && index >=2 && comment.charAt(index-2)==space;
            return !isOtherMeanNotSchoolName;
        }
        return false;
    }

}
