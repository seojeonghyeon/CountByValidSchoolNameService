package me.justin.modules;

import lombok.extern.slf4j.Slf4j;
import me.justin.modules.comment.CommentService;
import me.justin.modules.schoolmodel.SchoolModel;
import me.justin.modules.schoolmodel.SchoolModelService;
import me.justin.modules.text.TextService;

import java.util.List;

@Slf4j
public class MainController {

    private final CommentService commentService = CommentService.getInstance();
    private final SchoolModelService schoolModelService = SchoolModelService.getInstance();
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

        schoolModelService.persistSchoolList();
        commentService.extractValidSchoolNameFromCommentCSV();

        List<SchoolModel> schoolModelList = schoolModelService.findAllWithoutCountZero();
        log.info("School List found on service except of zero count: {}", schoolModelService.getClass());
        textService.writeTextFile(schoolModelList);

        log.info("Finished the process");
    }

}
