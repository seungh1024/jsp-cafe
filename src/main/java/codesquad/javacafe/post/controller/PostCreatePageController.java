package codesquad.javacafe.post.controller;

import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.session.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PostCreatePageController implements SubController {
    private static final Logger log = LoggerFactory.getLogger(PostCreatePageController.class);

    @Override
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        log.info("[PostController doProcess]");
        var method = req.getMethod();
        log.info("[PostController doProcess] method: {}", method);
        switch (method) {
            case "GET": {
                SessionManager.getInstance().loginCheck(req, "loginInfo");

                var dispatcher = req.getRequestDispatcher("/WEB-INF/qna/form.jsp");
                dispatcher.forward(req, res);

                break;
            }
            default:
                throw ClientErrorCode.PAGE_NOT_FOUND.customException("request uri =" + req.getRequestURI() + ", request method = " + method);
        }
    }
}
