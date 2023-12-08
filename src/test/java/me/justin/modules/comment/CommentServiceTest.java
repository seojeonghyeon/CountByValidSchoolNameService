package me.justin.modules.comment;

import me.justin.modules.schoolmodel.SchoolModel;
import me.justin.modules.schoolmodel.SchoolModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentServiceTest {

    private static final String COMMENT =
            "?경북 경산, 하양여자중학교?" +
            "이 글 보시는 하양여중 학생 분들, 공감되시는 분은" +
            "이 글 복사해서 배달의민족 게시물에 붙여넣기 해 주세요!!" +
            "먼저 신고 좀 하지 말아주세요!" +
            "까칠하고 배고픈 중학생 입니다. 저흰 잘못 안 했어요." +
            "안녕하세요 배달의 민족님" +
            "구구절절 저희 반 사연부터 말씀드릴게요" +
            "저희 반 선생님 께서는 짜장면을 사 주신다고 몇 달 전부터 말씀 하셨습니다. 그런데 그 몇 달이 6개월이 지났네요... 그래도 선생님은 벼르지도 않으십니다.. 저희 반 마른 아이들은 하루 하루가 지날 수록 말라가고 빈약해져 갑니다." +
            "어떻게 생각하시나요?" +
            "한창 많이 먹고 크게 자라나야 할 아이들이" +
            "먹고싶은 거 하나 못 먹고 이렇게 굶주린 학생들이 되어갑니다......" +
            "우리는 짜장면이 먹고싶습니다." +
            "그 검은 면발에 고소하고 짭조름한 그 양념을, 기름기는 있지만 목넘김이 스무스한 그런. 짜장면이 먹고싶습니다." +
            "어젠 급식 스파게티에서 노랑 검정 콜라보 애벌레가 나왔구요." +
            "학교가 산 꼭대기고 걸어서 가지도 못 해서 짜장면 먹기도 힘듭니다." +
            "저흴 위해 항상 고생하시는 선생님들을 위해." +
            "저희 학교는 전교생 1000명도 안 되는 조그만 학교에 많은 재능을 기부 해 주시는 선생님들을 위해서라도" +
            "짜장면 1000그릇 꼭 먹고 싶습니다." +
            "저와 함께 하는 동지 입니숙희숙민경민다연다지민지지영지손예진예진예진 @정다연 @백민지" +
            "모두가 마르고 굶주린 가엾은 아이들입니다." +
            "저희 학교 전체가 짜장면을 갈구 합니다." +
            "그리고 욕 좀 하지 마삼" +
            "사랑합니다. 배달의 민족 ❤";

    private static final String SCHOOL_NAME_1 = "하양여자중";
    private static final String SCHOOL_NAME_2 = "하양여중";
    private static final String SCHOOL_NAME_3 = "원미고";
    private static final String SCHOOL_NAME_4 = "소하고";
    private static final String SCHOOL_NAME_5 = "창현고";

    private SchoolModelService schoolModelService;
    private CommentService commentService;

    @BeforeEach
    void beforeAll(){
        schoolModelService = SchoolModelService.getInstance();
        commentService = CommentService.getInstance();
    }

    @DisplayName("Singleton 적용 여부 확인")
    @Test
    void getInstance() {
        CommentService oterCommentService = CommentService.getInstance();
        assertThat(true).isEqualTo(commentService == oterCommentService);
    }

    @DisplayName("Comment CSV에서 유효한 학교 이름 추출")
    @Test
    void extractSchoolNameFromCSV(){
        schoolModelService.persistSchoolList();
        commentService.extractValidSchoolNameFromCommentCSV();
        SchoolModel schoolModel = schoolModelService.findByName(SCHOOL_NAME_5);
        assertThat(true).isEqualTo(schoolModel.getCount() == 1);
    }

    @DisplayName("학교 이름이 포함 되어 있는 지 확인 - 정상")
    @Test
    void isEqualsToSchoolName_With_Correct_Value(){
        boolean result = commentService.contains(COMMENT, SCHOOL_NAME_1);
        assertThat(true).isEqualTo(result);
    }

    @DisplayName("학교 이름이 포함 되어 있는 지 확인 - 정상(남고, 여고에 대한 처리)")
    @Test
    void isEqualsToSchoolName_With_Correct_Value_About_One_Gender_School_Name(){
        boolean result = commentService.contains(COMMENT, SCHOOL_NAME_2);
        assertThat(true).isEqualTo(result);
    }

    @DisplayName("학교 이름이 포함 되어 있는 지 확인 - 오류(다른 값)")
    @Test
    void isEqualsToSchoolName_With_Wrong_Value(){
        boolean result = commentService.contains(COMMENT, SCHOOL_NAME_3);
        assertThat(false).isEqualTo(result);
    }

    @DisplayName("학교 이름이 포함 되어 있는 지 확인 - 오류(유사 값)")
    @Test
    void isEqualsToSchoolName_With_Similar_Value(){
        boolean result = commentService.contains(COMMENT, SCHOOL_NAME_4);
        assertThat(false).isEqualTo(result);
    }

}