package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class MemberRepositoryTest {


    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        Assertions.assertThat(findMember).isEqualTo(member);
        System.out.println("findMember = " + findMember);

        List<Member> result = memberRepository.findAll();
        Assertions.assertThat(result).containsExactly(member);
        for (Member member1 : result) {
            System.out.println("findAll member1 = " + member1);
        }

        List<Member> result2 = memberRepository.findByUsername(member.getUsername());
        Assertions.assertThat(result2).containsExactly(member);
        for (Member member1 : result2) {
            System.out.println("findByUsername member = " + member1);
        }
    }

    @Test
    public void searchTest() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberRepository.search(condition);

        Assertions.assertThat(result).extracting("username").containsExactly("member4");

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }

    @Test
    public void searchPageSimple() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest request = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, request);

        Assertions.assertThat(result.getSize()).isEqualTo(3);
        Assertions.assertThat(result.getContent()).extracting("username").containsExactly("member1","member2","member3");

        System.out.println("result.getSize() = " + result.getSize());
        
        for (MemberTeamDto memberTeamDto : result.getContent()) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }

    @Test
    public void searchPageComplex() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest request = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageComplex(condition, request);

        Assertions.assertThat(result.getSize()).isEqualTo(3);
        Assertions.assertThat(result.getContent()).extracting("username").containsExactly("member1","member2","member3");

        System.out.println("result.getSize() = " + result.getSize());

        for (MemberTeamDto memberTeamDto : result.getContent()) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }

    @Test
    public void searchPageComplexOptimization() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        PageRequest request = PageRequest.of(0, 5);

        Page<MemberTeamDto> result = memberRepository.searchPageComplexOptimization(condition, request);

        Assertions.assertThat(result.getSize()).isEqualTo(5);
        Assertions.assertThat(result.getContent()).extracting("username").containsExactly("member1","member2","member3","member4");

        System.out.println("result.getSize() = " + result.getSize());

        for (MemberTeamDto memberTeamDto : result.getContent()) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }
}