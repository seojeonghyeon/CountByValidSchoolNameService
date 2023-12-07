package me.justin.domain.text;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.justin.domain.school.School;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextFile {
    private String fileName;
    private String contents;

    public static TextFile createTextFile(String fileName, List<School> schoolList){
        TextFile textFile = new TextFile();
        textFile.setFileName(fileName);
        textFile.setContents(textFile.contents(schoolList));
        return textFile;
    }

    private String contents(List<School> schoolList){
        StringBuffer buffer = new StringBuffer();
        schoolList.forEach(school -> buffer.append(school.toString()).append("\n"));
        return buffer.toString();
    }

    public void writeTextFile(){
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(contents);
        } catch (IOException e) {
            log.error("Failed to process request. Exception: {}", e.getMessage(), e);
        }
    }
}
