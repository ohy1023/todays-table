package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import store.myproject.onlineshop.domain.enums.Level;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShip {

    @Id
    @GeneratedValue
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
