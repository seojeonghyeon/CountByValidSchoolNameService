package me.justin.domain.comment;

import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private boolean existDoubleQuotes;
    private StringBuffer buffer;

    public static Comment createComment(){
        return Comment.builder()
                .existDoubleQuotes(false)
                .buffer(new StringBuffer())
                .build();
    }

    public void initStringBuffer(){
        buffer = new StringBuffer();
    }
    public void setFalseExistDoubleQuotes(){
        existDoubleQuotes = false;
    }

    public void setTrueExistDoubleQuotes(){
        existDoubleQuotes = true;
    }
}
