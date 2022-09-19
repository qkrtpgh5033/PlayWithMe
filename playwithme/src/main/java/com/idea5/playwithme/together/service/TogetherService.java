package com.idea5.playwithme.together.service;


import com.idea5.playwithme.article.dto.ArticleDto;
import com.idea5.playwithme.event.domain.Event;
import com.idea5.playwithme.event.dto.EventDto;
import com.idea5.playwithme.event.service.EventService;
import com.idea5.playwithme.member.dto.MemberRecruitDto;
import com.idea5.playwithme.timeline.domain.Timeline;
import com.idea5.playwithme.timeline.exception.DataNotFoundException;
import com.idea5.playwithme.timeline.service.TimelineService;
import com.idea5.playwithme.together.domain.Together;
import com.idea5.playwithme.together.domain.TogetherInfoDto;
import com.idea5.playwithme.together.exception.TogetherNotFoundException;
import com.idea5.playwithme.together.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import com.idea5.playwithme.article.domain.Article;
import com.idea5.playwithme.article.repository.ArticleRepository;
import com.idea5.playwithme.member.domain.Member;
import com.idea5.playwithme.member.exception.MemberNotFoundException;
import com.idea5.playwithme.member.repository.MemberRepository;
import com.idea5.playwithme.review.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Slf4j
@Service
@RequiredArgsConstructor
public class TogetherService {
    @Autowired
    TogetherRepository togetherRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReviewService reviewService;

    @Autowired
    TimelineService timelineService;

    @Autowired
    EventService eventService;


    public void save(Long articleId, Long memberId){

        /**
         * Todo
         * article 예외처리
         */

        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new MemberNotFoundException("Member is Not Found");
        });

        Article article = articleRepository.findById(articleId).orElse(null);
        Long eventId = article.getEventIdByBoard();
        Event event = eventService.getEvent(eventId);


        Together together = Together.builder()
                .article(article)
                .member(member)
                .event(event)
                .build();

        togetherRepository.save(together);

        // Together 저장 -> Timeline 자동 생성되도록
        timelineService.create(together, member, article);
    }

    public Together findById(long id) {
        return togetherRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("no %d question not found,".formatted(id)));
    }

    @Transactional
    public void saveTogetherAndReview(Long articleId, List<Long> ids) {
        for(int i = 0; i < ids.size(); i++){
            this.save(articleId, ids.get(i));

            Long reviewerId = ids.get(i);
            for(int j = 0; j < ids.size(); j++){
                Long revieweeId = ids.get(j);
                if(reviewerId != revieweeId){
                    System.out.println("revieweeId = " + revieweeId);
                    System.out.println("reviewerId = " + reviewerId);
                    reviewService.save(articleId, revieweeId, reviewerId);
                }

            }
        }
    }

    public List<TogetherInfoDto> findTogetherListByMemberId(Long member_id) {

        List<TogetherInfoDto> lists = new ArrayList<>();
        List<Together> togethers = togetherRepository.findByMemberId(member_id);
        for (Together together : togethers) {
            TogetherInfoDto togetherInfoDto = new TogetherInfoDto();
            
            Event event = together.getEvent();
            EventDto eventDto = EventDto.getDtoFromEntity(event);

            List<Object[]> members = memberRepository.findMembersByArticleIdAndMemberIdNot(getArticleId(together), member_id);

            for (Object[] objects : members) {
                String ninckname = objects[0].toString();
                Long id = (Long)objects[1];
                String username = objects[2].toString();

                Member member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("Member is Not Found..."));
                MemberRecruitDto memberDto = MemberRecruitDto.builder()
                        .id(member.getId())
                        .nickname(ninckname)
                        .username(username)
                        .build();

                togetherInfoDto.addMembers(memberDto);
            }
            togetherInfoDto.setEventDto(eventDto);
            togetherInfoDto.setId(together.getId());
            togetherInfoDto.setArticleDto(ArticleDto.toDto(together.getArticle()));
            lists.add(togetherInfoDto);
        }

        for (TogetherInfoDto list : lists) {
            log.info("이벤트명 = " + list.getEventDto().getName());
            List<MemberRecruitDto> members = list.getMembers();
            for (MemberRecruitDto member : members) {
                log.info("이름 : " + member.getNickname());
            }
        }


        return lists;
    }
    
    public Long getArticleId(Together together){
        return together.getArticle().getId();
    }

    @Transactional
    public void doDelete(Long togetherId, Long memberId) {
        Together together = togetherRepository.findById(togetherId).orElseThrow(() ->
                new TogetherNotFoundException("Together is Not Found...."));

        Timeline timeline = timelineService.findByTogetherId(togetherId);
        timeline.setTogether(null);
        timelineService.deleteTimeline(timeline.getId());
        togetherRepository.deleteById(togetherId);
        reviewService.deleteReview(getArticleId(together), memberId);
        log.info("together is deleted ...");

    }
}
