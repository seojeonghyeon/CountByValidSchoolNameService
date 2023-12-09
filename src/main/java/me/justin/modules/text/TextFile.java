package me.justin.modules.text;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.justin.modules.schoolmodel.SchoolModel;

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

    public static TextFile createTextFile(String fileName, List<SchoolModel> schoolModelList){
        TextFile textFile = new TextFile();
        textFile.setFileName(fileName);
        textFile.setContents(textFile.contents(schoolModelList));
        return textFile;
    }

    private String contents(List<SchoolModel> schoolModelList){
        StringBuffer buffer = new StringBuffer();
        schoolModelList.forEach(school -> buffer.append(school.toString()).append("\n"));
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
