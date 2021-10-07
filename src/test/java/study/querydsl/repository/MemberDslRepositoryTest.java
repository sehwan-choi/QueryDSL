package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberDslRepositoryTest {

    @Autowired
    MemberDslRepository memberDslRepository;

    @Autowired
    EntityManager em;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberDslRepository.save(member);

        Member findMember = memberDslRepository.findById(member.getId()).get();
        Assertions.assertThat(member).isEqualTo(findMember);
        System.out.println("findMember = " + findMember);

        List<Member> result = memberDslRepository.findAll();
        Assertions.assertThat(result).containsExactly(member);
        for (Member member1 : result) {
            System.out.println("findAll member1 = " + member1);
        }

        List<Member> result2 = memberDslRepository.findByUsername(member.getUsername());
        Assertions.assertThat(result2).containsExactly(member);
        for (Member member1 : result2) {
            System.out.println("findByUsername member = " + member1);
        }
    }
}