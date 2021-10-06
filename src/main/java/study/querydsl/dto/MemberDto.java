package study.querydsl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(of = {"username","age"})
public class MemberDto {

    private String username;
    private int age;

    public MemberDto() {
    }

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
