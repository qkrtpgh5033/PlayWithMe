package com.idea5.playwithme.together.domain;

import com.idea5.playwithme.article.domain.Article;
import com.idea5.playwithme.event.domain.Event;
import com.idea5.playwithme.member.domain.Member;
import com.idea5.playwithme.timeline.domain.Timeline;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Together {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "together_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    private Event event;

    // 양방향 연관관계 메서드
    public void setEvent(Event event){
        if (this.event != null)
            this.event.getTogethers().remove(this);

        this.event = event;
        event.getTogethers().add(this);
    }

//    @OneToOne(cascade = CascadeType.REMOVE)
//    @JoinColumn(name = "timeline_id")
//    private Timeline timeline; // 읽기 전용

}

