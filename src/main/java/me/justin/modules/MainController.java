package me.justin.modules;

import lombok.extern.slf4j.Slf4j;
import me.justin.modules.comment.CommentService;
import me.justin.modules.school.School;
import me.justin.modules.school.SchoolService;
import me.justin.modules.text.TextService;

import java.util.List;

@Slf4j
public class MainController {

    private final CommentService commentService = CommentService.getInstance();
    private final SchoolService schoolService = SchoolService.getInstance();
    private final TextService textService = TextService.getInstance();

    private MainController(){}

    private static class MainControllerHelper {
        private static final MainController MAIN_CONTROLLER = new MainController();
    }

    public static MainController getInstance(){
        return MainControllerHelper.MAIN_CONTROLLER;
    }

    public void writeTextFile(){
        log.info("Beginning the process");

        schoolService.saveSchoolList();
        commentService.extractValidSchoolNameFromCommentCSV();

        List<School> schoolList = schoolService.findAllWithoutCountZero();
        log.info("School List found on service except of zero count: {}", schoolService.getClass());
        textService.writeTextFile(schoolList);

        log.info("Finished the process");
    }

}
