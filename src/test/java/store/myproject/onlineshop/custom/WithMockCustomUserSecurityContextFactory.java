package store.myproject.onlineshop.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

@Slf4j
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        // Authentication 은 본인이 인증에 사용하는 클래스를 생성하면 됩니다.

        final Authentication auth = new UsernamePasswordAuthenticationToken(
                annotation.username(),
                null,
                List.of(new SimpleGrantedAuthority(annotation.role().name())));

        context.setAuthentication(auth);

        return context;
    }
}