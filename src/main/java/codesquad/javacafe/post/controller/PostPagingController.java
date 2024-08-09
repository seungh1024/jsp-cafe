package codesquad.javacafe.post.controller;

import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.post.service.PostService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 * 페이징 처리를 위한 컨트롤러
 */
public class PostPagingController implements SubController {

    private static final Logger log = LoggerFactory.getLogger(PostPagingController.class);

    @Override
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        log.info("[PostPagingController doProcess]");
        var method = req.getMethod();
        log.info("[PostPagingController doProcess] method: {}", method);

        switch (method) {
            case "GET": {
                var pageNumber = Integer.parseInt(req.getParameter("page"));
                var pageCount = PostService.getInstance().getAllPostCount();
                var postList = PostService.getInstance().getAllPosts(pageNumber);
                var startPage = ((pageNumber-1)/5)*5+1;
                var endPage = startPage + 4;
                if(endPage*15 > pageCount) {
                    endPage = (pageCount+14)/15;
                    req.setAttribute("isEnd",true);
                }

                log.debug("[PostPagingController] postList = {}", postList);

                req.setAttribute("postList", postList);
                req.setAttribute("startPage", startPage);
                req.setAttribute("endPage", endPage);
                var dispatcher = req.getRequestDispatcher("/WEB-INF/index.jsp");
                dispatcher.forward(req, res);

                break;
            }
            default:
                throw ClientErrorCode.PAGE_NOT_FOUND.customException("request uri =" + req.getRequestURI() + ", request method = " + method);
        }
    }
}
