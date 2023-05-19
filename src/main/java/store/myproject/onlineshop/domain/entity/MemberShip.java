package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import store.myproject.onlineshop.domain.enums.Level;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberShip {

    @Id
    @GeneratedValue
    @Column(name = "member_ship_id")
    private Long id;

    private Level level;

    private float discount_rate;

    private float accumulation_rate;

}
