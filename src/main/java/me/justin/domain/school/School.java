package me.justin.domain.school;

import lombok.*;

@Builder @AllArgsConstructor
@Getter @Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class School {
    private String name;
    private Integer count;

    public static School createSchool(String name){
        return School.builder()
                .name(name)
                .count(0)
                .build();
    }

    public void addCount(){
        this.count++;
    }

    public String toString() {
        if(name.endsWith("고")) {
            return name + "등학교"+ "\t" + count;
        }else if(name.endsWith("중")){
            return name + "학교" + "\t" + count;
        }
        return name + "\t" + count;
    }
}
