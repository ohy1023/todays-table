package store.myproject.onlineshop.domain.dto.customer;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCheckRequest {

    @NotNull
    private String NickName;
}
