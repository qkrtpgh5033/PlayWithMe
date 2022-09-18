package com.idea5.playwithme.together.repository;

import com.idea5.playwithme.member.domain.Member;
import com.idea5.playwithme.together.domain.Together;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface TogetherRepository extends JpaRepository<Together, Long> {

    List<Together> findByMemberId(Long id);
    List<Together> findByArticleIdAndMemberIdNot(Long articleId, Long MemberId);
}
