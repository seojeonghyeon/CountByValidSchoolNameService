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

    /**
     * 유효한 학교 이름을 댓글에서 추출 한다.
     * 댓글과 학교 이름을 Queue로 받아 extractedSchoolNameFromComments Method에서 처리 한다.
     *
     * 처리 이후 남은 댓글 중 '학교'가 들어간 처리 되지 않은 댓글에 대해 개발자가 이후에 처리 가능 하도록 WARN LEVEL로 남긴다.
     * (ex. CSV 파일 내 없는 학교(폐교 되어 없는 학교 이거나 오타이거나)
     */
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

    /**
     * 유효한 학교 이름을 댓글에서 추출 한다.
     * 추출된 데이터는 SchoolModel 내 count parameter로 카운트 된다.
     *
     * @param endPoint "종료 지점"을 알려 주는 String
     * @param comments 댓글 Queue
     * @param schoolModelQueue 학교 Queue
     */
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

    /**
     * 댓글에 학교 이름이 포함 되어 있는 지 검사 한다.
     * @param comment 현재 확인이 진행 중인 댓글
     * @param schoolName 현재 확인이 진행 중인 학교 이름
     * @return 학교 이름이 포함 되어 있는지 여부
     */
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

    /**
     * KMP 알고리즘
     * 긴 문자열에 대해서는 String contains에 비해 효율적,
     * 짧은 문자열은 String contains가 더 효율적
     *
     * @param comment 댓글
     * @param schoolName 학교 이름
     * @return 댓글 내 학교 이름 포함 여부 확인
     */
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


    /**
     * 특수 학교 이름을 줄여서 댓글에 표기했는지 여부를 확인한다.(ex. 경기예술고 -> 경기예고)
     * @param comment 댓글
     * @param schoolName 학교이름
     * @return 학교이름이 댓글에 포함되어 있는 지 여부
     */
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

    /**
     * 학교 이름 내 공백이 들어가 있는 지 확인한다.
     * @param position 공백 주입 위치
     * @param comment 댓글
     * @param schoolName 학교 이름
     * @return 공백을 주입했을 때 댓글 내 공백을 주입한 학교 이름이 존재하는 지 여부
     */
    private boolean isContainsSpaceInSchoolName(int position, String comment, String schoolName) {
        if(position >= schoolName.length()){
            return false;
        }
        String replaceSchoolName = addSpaceAtPosition(schoolName, position);
        return isContainsSchoolNameInComment(comment, replaceSchoolName)
                || (isContainsSpaceInSchoolName(position + 1, comment, schoolName)
                || isContainsSpaceInSchoolName(position + 2, comment, replaceSchoolName));
    }

    /**
     * 특정 위치에 공백을 주입한다.
     * @param schoolName 학교 이름
     * @param position 특정 위치
     * @return 공백을 주입한 학교 이름
     */
    public String addSpaceAtPosition(String schoolName, int position) {
        StringBuilder result = new StringBuilder(schoolName);
        if (position >= 0 && position <= schoolName.length()) {
            result.insert(position, ' ');
        }
        return result.toString();
    }

    /**
     * 남자 학교 or 여자 학교 인 경우 줄여서 댓글을 썼는지 확인한다. (ex. 소명여자고 -> 소명여고)
     * @param isMale - true : 남자, false : 여자
     * @param comment - 댓글
     * @param schoolName - 학교 이름
     * @return 줄여서 댓글에 학교 이름을 기입했는 지 여부(공백 주입 포함)
     */
    private boolean isContainsOneGenderSchoolInSchoolName(boolean isMale, String comment, String schoolName){
        String gender = isMale ? "남" : "여";
        String[] splitStr =schoolName.split(isMale ? "남자" : "여자");
        schoolName = splitStr[0] + gender + splitStr[1];
        return isContainsSchoolNameInComment(comment, schoolName)
                || isContainsSpaceInSchoolName(1, comment, schoolName);
    }

    /**
     * 댓글 내 학교 이름이 포함되어 있는 지 여부를 확인한다.
     * KMP 알고리즘으로 비교를 진행한다.
     * @param comment 확인하고자 하는 댓글
     * @param schoolName 학교 이름
     * @return 포함 여부
     */
    private boolean isContainsSchoolNameInComment(String comment, String schoolName){
        char space = ' ';
        if (kmpSearch(comment, schoolName)){
            int index = comment.indexOf(schoolName);
            boolean isOtherMeanNotSchoolName = index >= 1 && comment.charAt(index-1)!=space && index >=2 && comment.charAt(index-2)==space;
            return !isOtherMeanNotSchoolName;
        }
        return false;
    }

}
