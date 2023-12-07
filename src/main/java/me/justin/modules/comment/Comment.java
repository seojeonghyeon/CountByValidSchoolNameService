package me.justin.modules.comment;

import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private boolean existDoubleQuotes;
    private StringBuffer stringBuffer;

    public static Comment createComment(){
        return Comment.builder()
                .existDoubleQuotes(false)
                .stringBuffer(new StringBuffer())
                .build();
    }

    public void initStringBuffer(){
        stringBuffer = new StringBuffer();
    }
    public void setFalseExistDoubleQuotes(){
        existDoubleQuotes = false;
    }

    public void setTrueExistDoubleQuotes(){
        existDoubleQuotes = true;
    }
}
