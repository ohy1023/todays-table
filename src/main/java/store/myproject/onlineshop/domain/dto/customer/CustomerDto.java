package store.myproject.onlineshop.domain.dto.customer;

import lombok.*;
import store.myproject.onlineshop.domain.entity.Customer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Long id;
    private String email;
    private String password;
    private String nickName;
    private String userName;
    private String tel;
    private String address;

    /**
     * UserEntity를 UserDto로 변환
     */
    public static CustomerDto toDto(Customer savedCustomer) {

        return CustomerDto.builder()
                      .id(savedCustomer.getId())
                      .email(savedCustomer.getEmail())
                      .nickName(savedCustomer.getNickName())
                      .userName(savedCustomer.getUserName())
                      .build();
    }
}
