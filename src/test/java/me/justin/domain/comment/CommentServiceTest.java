package me.justin.domain.comment;

import me.justin.domain.csv.CsvService;
import me.justin.domain.school.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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