package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        Member member3 = new Member("member3", 10, teamB);
        Member member4 = new Member("member4", 10, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void testJPQL() {
        //member1을 찾아라.
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void testQuerydsl() {
        ///QMember m = new QMember("aaaba");
        //QMember m = QMember.member;

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1")
                        .and(member.age.eq(10))
                )
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
        Assertions.assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
        Assertions.assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void fetch() {

        // 멤버를 모두 조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();
    }

    @Test
    public void fetchOne() {

        // 단건 조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
    }

    @Test
    public void fetchFirst() {

        // 처음 한 건 조회
        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                //.limit(1).fetch() //  fetchFirst()와 같다
                .fetchFirst();
    }

    @Test
    public void fetchResults() {

        // 페이징에서 사용
        QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .fetchResults();

        long total = result.getTotal(); //  총 개수
        System.out.println("total = " + total);

        List<Member> content = result.getResults(); //  실제 데이터
        for (Member member : content) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void fetchCount() {

        // count 조회
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();
        System.out.println("count = " + count);
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsFirst())
                .fetch();

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        for (Member member : result) {
            System.out.println("member = " + member);
            System.out.println("    team = " + member.getTeam().getName());
        }
    }

    @Test
    public void paging2() {
        QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        System.out.println("result.getTotal() = " + result.getTotal());
        System.out.println("result.getLimit() = " + result.getLimit());
        System.out.println("result.getOffset() = " + result.getOffset());
        for (Member member : result.getResults()) {
            System.out.println("member = " + member);
            System.out.println("    team = " + member.getTeam().getName());
        }
    }

    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        System.out.println("count = " + tuple.get(member.count()));
        System.out.println("age sum = " + tuple.get(member.age.sum()));
        System.out.println("age avg = " + tuple.get(member.age.avg()));
        System.out.println("age max = " + tuple.get(member.age.max()));
        System.out.println("age min = " + tuple.get(member.age.min()));
    }

    /**+
     * 팀의 이름과 각 팀의 평균 연령 구하기
     */
    @Test
    public void group() {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        System.out.println("name = " + teamA.get(team.name));
        System.out.println("    age avg = " + teamA.get(member.age.avg()));

        System.out.println("name = " + teamB.get(team.name));
        System.out.println("    age avg = " + teamB.get(member.age.avg()));
    }
}
