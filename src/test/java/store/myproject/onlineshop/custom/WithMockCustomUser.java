package store.myproject.onlineshop.custom;

import org.springframework.security.test.context.support.WithSecurityContext;
import store.myproject.onlineshop.domain.enums.CustomerRole;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "test@test.com";

    CustomerRole role() default CustomerRole.ROLE_CUSTOMER;

}