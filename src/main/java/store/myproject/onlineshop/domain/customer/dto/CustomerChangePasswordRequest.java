package store.myproject.onlineshop.domain.customer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerChangePasswordRequest {

    @NotNull
    private String currentPassword;

    @NotNull
    private String newPassword;
}
