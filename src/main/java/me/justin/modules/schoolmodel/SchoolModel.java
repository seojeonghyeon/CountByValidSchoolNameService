package me.justin.modules.schoolmodel;

import lombok.*;

@Builder @AllArgsConstructor
@Getter @Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class SchoolModel {
    private String name;
    private String target;
    private Integer count;

    public static SchoolModel createSchool(String name){
        return SchoolModel.builder()
                .name(name)
                .count(0)
                .build();
    }

    public void addCount(){
        this.count++;
    }

    @Override
    public String toString() {
        if(name.endsWith("고") || name.endsWith("초")) {
            return name + "등학교"+ "\t" + count;
        }else if(name.endsWith("중") || name.endsWith("대")){
            return name + "학교" + "\t" + count;
        }
        return name + "\t" + count;
    }
}
