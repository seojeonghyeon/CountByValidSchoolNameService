package me.justin.modules.comment;

import me.justin.modules.csv.CsvReader;
import me.justin.modules.csv.CsvService;
import me.justin.modules.school.School;
import me.justin.modules.school.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class CommentServiceTest {
    private CsvService csvService;
    private SchoolService schoolService;
    private CommentService commentService;

    @BeforeEach
    void beforeAll(){
        csvService = CsvService.getInstance();
        schoolService = SchoolService.getInstance();
        commentService = CommentService.getInstance();
    }

    @DisplayName("Singleton 적용 여부 확인")
    @Test
    void getInstance() {
        CommentService oterCommentService = CommentService.getInstance();
        assertThat(true).isEqualTo(commentService == oterCommentService);
    }


}