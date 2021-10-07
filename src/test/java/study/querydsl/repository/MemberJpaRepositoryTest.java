package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        Assertions.assertThat(findMember).isEqualTo(member);
        System.out.println("findMember = " + findMember);

        List<Member> result = memberJpaRepository.findAll();
        Assertions.assertThat(result).containsExactly(member);
        for (Member member1 : result) {
            System.out.println("findAll member1 = " + member1);
        }

        List<Member> result2 = memberJpaRepository.findByUsername(member.getUsername());
        Assertions.assertThat(result2).containsExactly(member);
        for (Member member1 : result2) {
            System.out.println("findByUsername member = " + member1);
        }
    }

}