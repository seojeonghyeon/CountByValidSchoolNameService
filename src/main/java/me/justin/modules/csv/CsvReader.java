package me.justin.modules.csv;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.justin.modules.comment.Comment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Getter @Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor @Builder
public class CsvReader {

    private String fileName;
    private int index;
    private List<String> readCSV;

    public static CsvReader createCsvReader(String fileName) {
        CsvReader csvReader = new CsvReader();
        csvReader.setFileName(fileName);
        csvReader.setReadCSV(new ArrayList<>());
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
        boolean isSchoolFile = fileName.endsWith("학교_학교명.csv");

        if(isSchoolFile){
            addLineContentsAboutSchool(bufferedReader);
            log.info("School List saved on CSV Reader: {}", this.fileName);
            return;
        }
        addLineContentsAboutComments(bufferedReader);
        log.info("Comments saved on CSV Reader: {}", this.fileName);
    }

    private void addLineContentsAboutSchool(BufferedReader bufferedReader) throws IOException{
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String replacement = "";
            String regex = fileName.endsWith("고등학교_학교명.csv") ? "등학교" : "학교";
            String lineContents = line.replaceAll(regex, replacement).replaceAll("[^가-힣]","");
            log.debug("Processing replace school's name - BEFORE SCHOOL NAME : {}, AFTER SCHOOL NAME : {}",line, lineContents);
            this.readCSV.add(lineContents);
        }
    }
    private void addLineContentsAboutComments(BufferedReader bufferedReader) throws IOException{
        Comment comment = Comment.createComment();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            log.debug("Processing replace comment - BEFORE COMMENT : {}",line);
            translateComments(line, comment);
        }
    }

    private void translateComments(String line, Comment comment){
        String replacement = "";
        String doubleQuotes = "\"";
        String regex = "[^가-힣\\s\"]";
        boolean isExistTwoDoubleQuotesInOneLine = line.contains("\"") && line.codePoints().filter(ch -> ch == doubleQuotes.codePointAt(0)).count() % 2 == 0;

        if(isExistTwoDoubleQuotesInOneLine){
            regex = "[^가-힣 ]";
        }
        String str = line.replaceAll(regex, replacement);

        if(isExistTwoDoubleQuotesInOneLine){
            this.readCSV.add(str);
            comment.initStringBuffer();
            comment.setFalseExistDoubleQuotes();
            log.debug("Processing replace comment - AFTER COMMENT : {}", str);
            return;
        }else if(str.contains(doubleQuotes) && !comment.isExistDoubleQuotes()){
            comment.getStringBuffer().append(str);
            comment.setTrueExistDoubleQuotes();
            return;
        }else if(str.contains(doubleQuotes) && comment.isExistDoubleQuotes()){
            comment.getStringBuffer().append(str);
            comment.setFalseExistDoubleQuotes();
            str = comment.getStringBuffer().toString();
            comment.initStringBuffer();
        }else if(!str.contains(doubleQuotes) && comment.isExistDoubleQuotes()){
            comment.getStringBuffer().append(str);
            return;
        }

        this.readCSV.add(str);
        log.debug("Processing replace comment - AFTER COMMENT : {}", str);
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
