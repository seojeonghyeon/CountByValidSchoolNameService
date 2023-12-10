package me.justin.modules.comment;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.justin.modules.csv.CsvReader;
import me.justin.modules.csv.CsvService;
import me.justin.modules.schoolmodel.SchoolModel;
import me.justin.modules.schoolmodel.SchoolModelService;

import java.util.Queue;

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
        String endPoint = "종료 지점";

        CsvReader commentReader = csvService.createCommentsReader();
        Queue<String> comments = commentReader.getComments();
        Queue<SchoolModel> schoolModelList = schoolModelService.findAllQueueType();

        SchoolModel getSchoolModel = schoolModelList.poll();
        schoolModelList.add(SchoolModel.createSchool(endPoint));
        while(!comments.isEmpty()){
            String getComment = comments.poll();
            if(endPoint.equals(getComment)) {
                schoolModelList.add(getSchoolModel);
                getSchoolModel = schoolModelList.poll();
            }
            if(getSchoolModel != null && endPoint.equals(getSchoolModel.getName())){
                break;
            }
            if(getSchoolModel != null && contains(getComment, getSchoolModel.getName())){
                log.debug("Processing count up by school's name - SCHOOL NAME : {}, COMMENT : {}", getSchoolModel.getName(), getComment);
                getSchoolModel.addCount();
                continue;
            }
            comments.add(getComment);
        }
        comments.forEach(comment -> log.debug("Rest Comments : {}",comment));
        log.info("Valid School List was count by school from comments");
    }

    public boolean contains(String comment, String schoolName){
        if(comment.length() < 2){
            return false;
        }
        schoolName = schoolName.equals("명문고") ? "명문고등" : schoolName;

        String male = "남자";
        String female = "여자";
        boolean isOneGenderSchool = schoolName.contains(male) || schoolName.contains(female);
        boolean isSpecialSchool = schoolName.contains("과학")
                || schoolName.contains("상업")
                || schoolName.contains("국제")
                || schoolName.contains("디자인")
                || schoolName.contains("인터넷")
                || schoolName.contains("외국어")
                || schoolName.contains("사관")
                || schoolName.contains("체육");
        int minLength = isSpecialSchool ? 6 : 3;
        boolean result = isContainsSchoolNameInComment(comment, schoolName);
        boolean isSchoolNameLengthLongerThanThree = schoolName.length() > minLength;
        if(!result){
            result = isContainsSpaceInSchoolName(1, comment, schoolName);
        }
        if(!result && isOneGenderSchool){
            result = isContainsOneGenderSchoolInSchoolName(schoolName.contains(male), comment, schoolName);
        }
        if(!result && !isOneGenderSchool && isSchoolNameLengthLongerThanThree){
            result = isContainsWhenEraseFrontPoint(minLength, comment, schoolName);
        }
        return result;
    }

    private boolean isContainsWhenEraseFrontPoint(int minLength, String comment, String schoolName) {
        if(schoolName.length() <= minLength){
            return false;
        }
        String replaceSchoolName = removeCharacterAtIndex(schoolName, 0);
        return isContainsSchoolNameInComment(comment, replaceSchoolName) || isContainsWhenEraseFrontPoint(minLength, comment, replaceSchoolName);
    }

    public String removeCharacterAtIndex(String schoolName, int indexToRemove) {
        if (indexToRemove >= 0 && indexToRemove < schoolName.length()) {
            StringBuilder result = new StringBuilder(schoolName);
            result.deleteCharAt(indexToRemove);
            return result.toString();
        }
        return schoolName;
    }

    private boolean isContainsSpaceInSchoolName(int position, String comment, String schoolName) {
        if(position >= schoolName.length()){
            return false;
        }
        String replaceSchoolName = addSpaceAtPosition(schoolName, position);
        return isContainsSchoolNameInComment(comment, replaceSchoolName)
                || (isContainsSpaceInSchoolName(position + 1, comment, schoolName)
                || isContainsSpaceInSchoolName(position + 2, comment, replaceSchoolName));
    }

    public String addSpaceAtPosition(String schoolName, int position) {
        StringBuilder result = new StringBuilder(schoolName);
        if (position >= 0 && position <= schoolName.length()) {
            result.insert(position, ' ');
        }
        return result.toString();
    }

    private boolean isContainsOneGenderSchoolInSchoolName(boolean isMale, String comment, String schoolName){
        boolean isSchoolNameLengthLongerThanThree = schoolName.length() > 3;
        String gender = isMale ? "남" : "여";
        String[] splitStr =schoolName.split(isMale ? "남자" : "여자");
        schoolName = splitStr[0] + gender + splitStr[1];
        return isContainsSchoolNameInComment(comment, schoolName)
                || isContainsSpaceInSchoolName(1, comment, schoolName)
                || isContainsWhenEraseFrontPoint(5, comment, schoolName);
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
