package me.justin.modules.text;


import lombok.extern.slf4j.Slf4j;
import me.justin.modules.schoolmodel.SchoolModel;

import java.util.List;

@Slf4j
public class TextService {

    private final static String TEXT_FILE_NAME = "output/result.txt";

    private TextService(){}

    private static class TextServiceHelper {
        private static final TextService TEXT_SERVICE = new TextService();
    }

    public static TextService getInstance(){
        return TextServiceHelper.TEXT_SERVICE;
    }

    public void writeTextFile(List<SchoolModel> schoolModelList){
        TextFile textFile = TextFile.createTextFile(TEXT_FILE_NAME, schoolModelList);
        log.debug("Created TextFile Object for writing the text file - TEXT FILE CONTENTS \n{}", textFile.getContents());
        textFile.writeTextFile();
        log.info("School List is written the text file except of zero count - TEXT FILE NAME : {}", textFile.getFileName());
    }

}
