package study.querydsl.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static study.querydsl.entity.QMember.member;

@Repository
public class MemberDslRepository {

    private EntityManager em;
    private JPAQueryFactory queryFactory;

    public MemberDslRepository(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(memberIdEq(id))
                .fetchOne();
        return Optional.ofNullable(member);
    }

    private BooleanExpression memberIdEq(Long id) {
        if (id == null)
            return null;
        return member.id.eq(id);
    }

    public List<Member> findAll() {
        return queryFactory
                .selectFrom(member)
                .fetch();
    }

    public List<Member> findByUsername(String username) {
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(username))
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        if (username == null)
            return null;
        return member.username.eq(username);
    }
}
