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
    private final static String UNIVERSITY_NAME= "대학교";
    private final static String HIGH_SCHOOL_NAME= "고등학교";
    private final static String MIDDLE_SCHOOL_NAME= "중학교";
    private final static String ELEMENTARY_SCHOOL_NAME= "초등학교";
    private final static String SPECIAL_SCHOOL_NAME= "특수학교";
    private final static String ETC_SCHOOL_NAME= "그외학교";

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
        Queue<SchoolModel> schoolModelServiceAllQueueType = schoolModelService.findAllQueueType();
        extractedSchoolNameFromComments(endPoint, comments, schoolModelServiceAllQueueType);

        comments.stream()
                .filter(comment -> comment.contains("학교"))
                .forEach(comment -> log.warn("Comment containing the school name is after processing, please check again : {}", comment));
        log.info("Valid School List was count by school from comments");
    }

    private void extractedSchoolNameFromComments(String endPoint, Queue<String> comments, Queue<SchoolModel> schoolModelQueue) {
        SchoolModel getSchoolModel = schoolModelQueue.poll();
        schoolModelQueue.add(SchoolModel.createSchool(endPoint));
        while(!comments.isEmpty()){
            String getComment = comments.poll();
            if(endPoint.equals(getComment)) {
                schoolModelQueue.add(getSchoolModel);
                getSchoolModel = schoolModelQueue.poll();
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
    }

    public boolean contains(String comment, String schoolName){
        if(comment.length() < 2){
            return false;
        }
        boolean isHighSchool = schoolName.endsWith("고");
        schoolName = schoolName.equals("명문고") ? "명문고등" : schoolName;


        String male = "남자";
        String female = "여자";

        boolean isOneGenderSchool = schoolName.contains(male) || schoolName.contains(female);

        boolean result = isContainsSchoolNameInComment(comment, schoolName);

        if(!result){
            result = isContainsSpaceInSchoolName(1, comment, schoolName);
        }
        if(!result && isOneGenderSchool){
            result = isContainsOneGenderSchoolInSchoolName(schoolName.contains(male), comment, schoolName);
        }

        if(isHighSchool){
            boolean isSpecialSchool = schoolName.contains("과학")
                    || schoolName.contains("예술")
                    || schoolName.contains("상업")
                    || schoolName.contains("국제")
                    || schoolName.contains("디자인")
                    || schoolName.contains("인터넷")
                    || schoolName.contains("외국어")
                    || schoolName.contains("사관")
                    || schoolName.contains("체육");
            if(!result && isSpecialSchool){
                result = isContainsSpecialSchoolInSchoolName(comment, schoolName);
            }
        }

        return result;
    }

    private boolean kmpSearch(String comment, String schoolName) {
        int[] lps = computeLPSArray(schoolName);
        int i = 0;
        int j = 0;

        while (i < comment.length()) {
            if (schoolName.charAt(j) == comment.charAt(i)) {
                i++;
                j++;
            }
            if (j == schoolName.length()) {
                return true;
            } else if (i < comment.length() && schoolName.charAt(j) != comment.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return false;
    }

    private int[] computeLPSArray(String schoolName) {
        int length = schoolName.length();
        int[] lps = new int[length];
        int len = 0;

        for (int i = 1; i < length; ) {
            if (schoolName.charAt(i) == schoolName.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }


    private boolean isContainsSpecialSchoolInSchoolName(String comment, String schoolName) {
        String science = "과학";
        String art = "예술";
        String commerce = "상업";
        String international = "국제";
        String design = "디자인";
        String internet = "인터넷";
        String foreign = "외국어";
        String officer = "사관";
        String athletic = "체육";
        String splitWord = schoolName.contains(science) ? science
                : schoolName.contains(art) ? art
                : schoolName.contains(commerce) ? commerce
                : schoolName.contains(international) ? international
                : schoolName.contains(design) ? design
                : schoolName.contains(internet) ? internet
                : schoolName.contains(foreign) ? foreign
                : schoolName.contains(officer) ? officer
                : schoolName.contains(athletic) ? athletic : "" ;
        String specialLetter =
                schoolName.contains(science) ? "과"
                        : schoolName.contains(art) ? "예"
                        : schoolName.contains(commerce) ? "상"
                        : schoolName.contains(international) ? "국"
                        : schoolName.contains(design) ? "디"
                        : schoolName.contains(internet) ? "인"
                        : schoolName.contains(foreign) ? "외"
                        : schoolName.contains(officer) ? "사"
                        : schoolName.contains(athletic) ? "체" : "" ;
        String[] splitStr =schoolName.split(splitWord);
        schoolName = splitStr[0] + specialLetter + splitStr[1];
        return isContainsSchoolNameInComment(comment, schoolName)
                || isContainsSpaceInSchoolName(1, comment, schoolName);
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
                || isContainsSpaceInSchoolName(1, comment, schoolName);
    }

    private boolean isContainsSchoolNameInComment(String comment, String schoolName){
        char space = ' ';
        if (kmpSearch(comment, schoolName)){
            int index = comment.indexOf(schoolName);
            boolean isNotUniversity = schoolName.endsWith("대") && comment.contains("고등");
            boolean isOtherMeanNotSchoolName = index >= 1 && comment.charAt(index-1)!=space && index >=2 && comment.charAt(index-2)==space;
            if(isNotUniversity) return false;
            return !isOtherMeanNotSchoolName;
        }
        return false;
    }

}
