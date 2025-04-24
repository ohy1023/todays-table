package store.myproject.onlineshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import store.myproject.onlineshop.service.AlertService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@WithMockUser
class AlertControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlertService alertService;

    @Test
    @DisplayName("SSE 구독 성공")
    void subscribe_success() throws Exception {
        SseEmitter emitter = new SseEmitter();
        given(alertService.subscribe(anyString(), anyString()))
                .willReturn(emitter);

        mockMvc.perform(get("/api/subscribe")
                        .header("Last-Event-ID", "12345"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("SSE 구독 실패- 인증 안된 사용자")
    @WithAnonymousUser
    void subscribe_fail_unauthenticated() throws Exception {
        SecurityContextHolder.clearContext(); // 인증 해제

        mockMvc.perform(get("/api/subscribe"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
