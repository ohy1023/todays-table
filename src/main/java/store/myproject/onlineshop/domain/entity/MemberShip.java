package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.enums.Level;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_ship_id")
    private Long id;

    private Level level;

    private float discount_rate;

    private float accumulation_rate;

    @Builder
    public MemberShip(Long id, Level level, float discount_rate, float accumulation_rate) {
        this.id = id;
        this.level = level;
        this.discount_rate = discount_rate;
        this.accumulation_rate = accumulation_rate;
    }
}
