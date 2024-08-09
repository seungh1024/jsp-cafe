package codesquad.javacafe;


import codesquad.javacafe.post.service.PostService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * root 요청은 여기에서 핸들링
 */
@WebServlet("")
public class MainController extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("[MainController] GET request");
        var postList = PostService.getInstance().getAllPosts(1);
        log.debug("[MainController] postList: {}", postList);
        req.setAttribute("postList",postList);
        var pageCount = PostService.getInstance().getAllPostCount();
        var startPage = 1;
        var endPage = startPage + 4;
        if(endPage*15 > pageCount) {
            endPage = (pageCount+14)/15;
            req.setAttribute("isEnd",true);
        }

        req.setAttribute("startPage", 1);
        req.setAttribute("endPage", endPage);
        var dispatcher = req.getRequestDispatcher("/WEB-INF/index.jsp");
        dispatcher.forward(req, resp);
    }
}
