package store.myproject.onlineshop.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ExceptionHandlingController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null){
            int statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return "/login";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else {
                return "error/error";
            }
        }

        return "error/error";
    }

    @GetMapping("/error/403")
    public String forbiddenError() {
        return "error/403";
    }

}
