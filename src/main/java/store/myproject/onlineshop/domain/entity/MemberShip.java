package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.enums.Level;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_ship_id")
    private Long id;

    private Level level;

    private float discount_rate;

    private float accumulation_rate;


}
