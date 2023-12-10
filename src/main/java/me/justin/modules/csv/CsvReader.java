package me.justin.modules.csv;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.justin.modules.comment.Comment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


@Slf4j
@Getter @Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor @Builder
public class CsvReader {

    private String fileName;
    private int index;
    private List<String> strSchoolList;
    private Queue<String> comments;

    /**
     * CsvReader Object 해당 static Method 통해서 만 생성 되도록 하였다,
     *
     * @param fileName - CSV File 위치 + 파일명 (ex. /src/main/resources/csv/comments.csv)
     * @return 초기화 된 CsvReader Object
     * @throws IOException 발생(파일이 존재 하지 않는 경우)
     */
    public static CsvReader createCsvReader(String fileName) {
        CsvReader csvReader = new CsvReader();
        csvReader.setFileName(fileName);
        csvReader.setStrSchoolList(new ArrayList<>());
        csvReader.setComments(new ArrayDeque<>());
        try {
            csvReader.addCSVStringList();
        }catch (IOException e){
            log.error("Failed to process request. Exception: {}", e.getMessage(), e);
        }
        return csvReader;
    }

    /**
     * CSV File 대해 학교 파일 인지, 댓글 파일 인지 확인 하여 별개로 처리 한다.
     * @throws fileName이 존재 하지 않은 경우
     * @throws IOException 발생(파일이 존재 하지 않는 경우)
     */
    public void addCSVStringList() throws IOException{
        if(!hasText(fileName)){
            log.error("Failed to process CSV String list add. No exist file name");
            return;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), StandardCharsets.UTF_8));
        boolean isSchoolFile = fileName.endsWith("_학교명.csv");

        if(isSchoolFile){
            addLineContentsAboutSchool(bufferedReader);
            log.info("School List saved on CSV Reader: {}", this.fileName);
            return;
        }
        addLineContentsAboutComments(bufferedReader);
        log.info("Comments saved on CSV Reader: {}", this.fileName);
    }

    /**
     * 각 학교(대학교, 고등 학교, 중학교, 초등 학교, 기타)에 대해 전 처리를 진행 한다. (한글이 아닌 문자 제거, 접미사 줄이기 (ex. 원미고등학교 -> 원미고)
     * @param bufferedReader - 학교 CSV File 연결된 BufferedReader, UTF-8
     * @throws IOException 발생(파일이 존재 하지 않는 경우)
     */
    private void addLineContentsAboutSchool(BufferedReader bufferedReader) throws IOException{
        String originalSchoolName;
        String replaceBlank = "";
        String universitySuffix = "대학교";
        String highSchoolSuffix = "고등학교";
        String middleSchoolSuffix = "중학교";
        String elementarySchoolSuffix = "초등학교";
        String etcSchoolSuffix = "학교";
        String regexAllowKorean = "[^가-힣]";

        while ((originalSchoolName = bufferedReader.readLine()) != null) {
            boolean isUniversity = fileName.endsWith("대학교_학교명.csv");
            boolean isHighSchool = fileName.endsWith("고등학교_학교명.csv");
            boolean isMiddleSchool = fileName.endsWith("중학교_학교명.csv");
            boolean isElementarySchool = fileName.endsWith("초등학교_학교명.csv");
            String replaceLetter = isUniversity ? "대" : isHighSchool ? "고" : isMiddleSchool ? "중" : isElementarySchool ? "초" : "학교";
            String removeSuffix = isUniversity ? universitySuffix : isHighSchool ? highSchoolSuffix : isMiddleSchool ? middleSchoolSuffix : isElementarySchool ? elementarySchoolSuffix : etcSchoolSuffix;
            String replaceSchoolName = originalSchoolName.replaceAll(removeSuffix, replaceLetter).replaceAll(regexAllowKorean, replaceBlank);
            log.debug("Processing replace school's name - BEFORE SCHOOL NAME : {}, AFTER SCHOOL NAME : {}",originalSchoolName, replaceSchoolName);
            this.strSchoolList.add(replaceSchoolName);
        }
    }

    /**
     * 댓글에 대해 전 처리가 가능 하도록 한 줄 씩 가져와 translateComments Method를 호출한다.
     * Queue 사용 시, 마지막 종료 지점을 알 수 있도록 endPoint를 끝에 넣어주었다.
     * @param bufferedReader - 댓글 CSV File 연결된 BufferedReader, UTF-8
     * @throws IOException 발생(파일이 존재 하지 않는 경우)
     */
    private void addLineContentsAboutComments(BufferedReader bufferedReader) throws IOException{
        String endPoint = "종료 지점";

        Comment comment = Comment.createComment();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            log.debug("Processing replace comment - BEFORE COMMENT : {}",line);
            replaceComments(line, comment);
        }
        this.comments.add(endPoint);
    }

    /**
     * 댓글에 대한 전 처리를 진행 한다.
     * 가져온 1개 line 내 큰 따옴표(", DoubleQuotes)가 짝수 개 또는 0개로 존재 하는 지 확인(isExistTwoDoubleQuotesInOneLine) 한다.
     * 큰 따옴표가 짝수 개로 존재하면 한글과 띄어쓰기를 제외한 나머지를 없애고 comments(Queue<String>)에 넣는다.
     *
     * 큰따옴표가 replaceComment에 존재하는데, comment의 existDoubleQuotes false라는 이야기는 댓글의 시작 지점이라는 이므로("가나다라)
     * comment 내 buffer에 넣어주고 existDoubleQuotes를 true로 만든다.
     *
     * 큰따옴표가 replaceComment에 존재하는데, comment의 existDoubleQuotes true라는 이야기는 댓글의 끝 지점이라는 이므로(마바사아")
     * comment 내 buffer에 있는 내용을 Queue에 넣어주고 buffer와 existDoubleQuotes를 초기화한다.
     *
     * @param line 댓글 CSV File에서 가져온 다음 1개 line
     * @param comment 큰 따옴표 여부, Buffer를 가지고 있는 Object
     */
    private void replaceComments(String line, Comment comment){
        String replaceBlank = "";
        String doubleQuotes = "\"";
        String regexComment = "[^가-힣\\s\"]";
        boolean isExistTwoDoubleQuotesInOneLine = line.contains(doubleQuotes)
                && line.codePoints().filter(ch -> ch == doubleQuotes.codePointAt(0)).count() % 2 == 0;

        if(isExistTwoDoubleQuotesInOneLine){
            regexComment = "[^가-힣 ]";
        }
        String replaceComment = line.replaceAll(regexComment, replaceBlank);

        if(isExistTwoDoubleQuotesInOneLine){
            this.comments.add(replaceComment);
            comment.initStringBuffer();
            comment.setFalseExistDoubleQuotes();
            log.debug("Processing replace comment - AFTER COMMENT : {}", replaceComment);
            return;
        }else if(replaceComment.contains(doubleQuotes) && !comment.isExistDoubleQuotes()){
            comment.getBuffer().append(replaceComment);
            comment.setTrueExistDoubleQuotes();
            return;
        }else if(replaceComment.contains(doubleQuotes) && comment.isExistDoubleQuotes()){
            comment.getBuffer().append(replaceComment);
            comment.setFalseExistDoubleQuotes();
            replaceComment = comment.getBuffer().toString();
            comment.initStringBuffer();
        }else if(!replaceComment.contains(doubleQuotes) && comment.isExistDoubleQuotes()){
            comment.getBuffer().append(replaceComment);
            return;
        }

        this.comments.add(replaceComment);
        log.debug("Processing replace comment - AFTER COMMENT : {}", replaceComment);
        comment.initStringBuffer();
    }


    private static boolean hasText(String str){
        return str != null && !str.isEmpty() && containsText(str);
    }

    private static boolean containsText(CharSequence str){
        int strLen = str.length();

        for(int i=0; i<strLen; ++i){
            if(!Character.isWhitespace(str.charAt(i))){
                return true;
            }
        }
        return false;
    }
}
