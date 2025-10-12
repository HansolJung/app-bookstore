package it.korea.app_bookstore.book.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import it.korea.app_bookstore.book.dto.BookSearchDTO;
import it.korea.app_bookstore.book.entity.BookEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BookSearchSpecification implements Specification<BookEntity> {

    private BookSearchDTO searchDTO;

    public BookSearchSpecification(BookSearchDTO searchDTO) {
        this.searchDTO = searchDTO;
    }

    // root 비교대상 -> entity (JPA 가 만들어서 넣어줌)
    // query : sql 조작 (잘 사용하지 않음)
    // cb : where 조건
    @Override
    public Predicate toPredicate(Root<BookEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        /*
         * query 파라미터는 sql 을 조작할 수 있지만 잘 사용하지 않는다.
         * 그 이유는 service 또는 pageable 객체에서 이미 정렬 또는 페이징 처리를 하기 때문.
         * 여기서 추가로 조작하면 복잡도가 상승하고 오류 발생시 유지보수가 어려워짐.
         */
        //query.distinct(true);
        //query.groupBy(root.get("title"));
        //query.orderBy(cb.desc(root.get("createDate")));
        if (StringUtils.isNotBlank(searchDTO.getSearchText())) {   // 검색어가 있는 경우...
            String likeText = "%" + searchDTO.getSearchText() + "%";

            predicates.add(cb.like(root.get("title"), likeText));
            predicates.add(cb.like(root.get("author"), likeText));
            Predicate orPredicate = orTogether(predicates, cb);
            predicates.clear();
            predicates.add(orPredicate);
        }

        if (searchDTO.getCaId() != 0) {   // 카테고리 전체보기가 아닐 경우...
            predicates.add(cb.equal(root.get("category").get("caId"), searchDTO.getCaId()));
        }

        predicates.add(cb.equal(root.get("delYn"), "N"));   // 기본적으로 삭제 여부가 N 인 도서들만 검색

        return andTogether(predicates, cb);
    }

    private Predicate andTogether(List<Predicate> predicates, CriteriaBuilder cb) {
        return cb.and(predicates.toArray(new Predicate[0]));  // 타입 추론. array를 만들 때 new Predicate 객체로 만들겠다는 뜻.
    }

    private Predicate orTogether(List<Predicate> predicates, CriteriaBuilder cb) {
        return cb.or(predicates.toArray(new Predicate[0]));   // 타입 추론. array를 만들 때 new Predicate 객체로 만들겠다는 뜻.
    }
}
