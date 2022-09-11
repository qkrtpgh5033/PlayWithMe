package com.idea5.playwithme.review.controller;

import com.idea5.playwithme.article.domain.Article;
import com.idea5.playwithme.article.service.ArticleService;
import com.idea5.playwithme.member.domain.Member;
import com.idea5.playwithme.member.service.MemberService;
import com.idea5.playwithme.review.domain.Review;
import com.idea5.playwithme.review.domain.ReviewForm;
import com.idea5.playwithme.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final MemberService memberService;
    private final ArticleService articleService;

    // 회원 매너 평가폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{article_id}")
    public String reviewForm(Model model, @PathVariable("article_id") Long articleId, Principal principal) {
        Article article = articleService.findById(articleId);
        Member member = memberService.findMember(principal.getName());

        List<Review> reviewList = reviewService.getReviewList(articleId, member.getId());
        if(reviewList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "평가 권한이 없습니다.");
        }

        // TODO: event 일시 가져올 때 이렇게 가져와도 되는지
        // 평가날짜(오늘 날짜)가 이벤트 날짜보다 전이면 평가X
        LocalDate eventDate = article.getBoard().getEvent().getDate().toLocalDate();
        if(LocalDate.now().compareTo(eventDate) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "평가 가능한 기간이 아닙니다.");
        }
//        System.out.println("eventDate = " + eventDate);
//        System.out.println(LocalDate.now());

        model.addAttribute("reviewList", reviewList);

        return "review_form";
    }

    // 회원 매너 평가
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{article_id}")
    public String review(@PathVariable("article_id") Long articleId, @Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {


        // TODO: 리뷰하기 페이지로 redirect 변경하기
        return "review_form";
    }
}
