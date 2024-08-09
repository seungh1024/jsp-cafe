package codesquad.javacafe;

import codesquad.javacafe.comment.controller.CommentController;
import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/ajax/*")
public class AjaxController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AjaxController.class);
    private static Map<String, SubController> ajaxController = new HashMap<>();
    public void init() throws ServletException {
        ajaxController.put("/comment", new CommentController());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        log.info("[FrontController] service, Request URI = {}, Http Method = {}", req.getRequestURI(), req.getMethod());
        var uri = req.getRequestURI().replace("/ajax", "");
        log.debug("[FrontController] uri = {}", uri);
        var subController = ajaxController.get(uri);

        try {
            if (subController == null) {
                log.error("Page Not Found");
                throw ClientErrorCode.PAGE_NOT_FOUND.customException("요청 uri = " + uri);
            }
            subController.doProcess(req, res);
        } catch (CustomException exception) {
            // TODO error response
            log.error("[Ajax Controller CustomException] message = {}" , exception.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("[AJAX CONTROLLER Exception] message = {}" , exception.getMessage());
        }
    }
}
