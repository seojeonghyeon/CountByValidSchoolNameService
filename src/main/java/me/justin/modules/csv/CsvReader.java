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
    private void addLineContentsAboutComments(BufferedReader bufferedReader) throws IOException{
        String endPoint = "종료 지점";

        Comment comment = Comment.createComment();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            log.debug("Processing replace comment - BEFORE COMMENT : {}",line);
            translateComments(line, comment);
        }
        this.comments.add(endPoint);
    }

    private void translateComments(String line, Comment comment){
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
