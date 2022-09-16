package com.idea5.playwithme.event.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private Integer categoryId;

    @Column(length = 100)
    private String name;

    @Column(length = 200)
    private String location;

    // TODO: 크롤링 시간으로 들어옴
    @CreatedDate
    private LocalDateTime date;


//    @OneToOne(mappedBy = "event", fetch = FetchType.LAZY)
//    private Board board; // 일대일 양방향 매핑 ( 읽기 전용 )

//    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
//    private List<Timeline> timelines = new ArrayList<>(); // 일대다 양방향 매핑 (읽기 전용)


}
