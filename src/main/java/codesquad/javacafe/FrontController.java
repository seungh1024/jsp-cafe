package codesquad.javacafe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codesquad.javacafe.common.exception.HttpStatus;
import codesquad.javacafe.common.exception.ServerErrorCode;
import codesquad.javacafe.post.controller.PostCreatePageController;
import codesquad.javacafe.post.controller.PostPagingController;
import codesquad.javacafe.post.controller.PostUpdatePageController;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.javacafe.auth.controller.AuthController;
import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.member.controller.MemberController;
import codesquad.javacafe.member.controller.MemberInfoController;
import codesquad.javacafe.member.controller.MemberProfileController;
import codesquad.javacafe.post.controller.PostController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/*")
public class FrontController extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(FrontController.class);
	private static Map<String, SubController> subControllers = new HashMap<>();
	// TODO proxy controller 만들기?
	private String message;

	public void init() {
		log.info("[Initializing FrontController]");
		subControllers.put("/users", new MemberController());
		subControllers.put("/users/profile", new MemberProfileController());
		subControllers.put("/users/info", new MemberInfoController());
		subControllers.put("/post", new PostController());
		subControllers.put("/auth", new AuthController());
		subControllers.put("/post/page", new PostCreatePageController());
		subControllers.put("/post/update", new PostUpdatePageController());
		subControllers.put("/post/paging", new PostPagingController());
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) {
		log.info("[FrontController] service, Request URI = {}, Http Method = {}", req.getRequestURI(), req.getMethod());
		var uri = req.getRequestURI().replace("/api", "");
		log.debug("[FrontController] uri = {}", uri);
		var subController = subControllers.get(uri);

		try {
			if (subController == null) {
				log.error("Page Not Found");
				throw ClientErrorCode.PAGE_NOT_FOUND.customException("요청 uri = " + uri);
			}
			subController.doProcess(req, res);
		} catch (CustomException exception) {
			log.error("[CustomException] error name = {}, debug message = {}", exception.getErrorName(),
					exception.getDebugMessage());

			res.setStatus(exception.getHttpStatus().getStatusCode());
			req.setAttribute("exception", exception);
			var dispatcher = req.getRequestDispatcher("/WEB-INF/error/customError.jsp");
			try {
				if (Objects.equals(exception.getHttpStatus(), HttpStatus.UNAUTHORIZED)) {
					dispatcher = req.getRequestDispatcher("/user/login.jsp");
				}
				dispatcher.forward(req, res);
			} catch (Exception e) {
				log.error("[FrontController Dispatch Error] message = ", e.getMessage());
			}
		} catch (ServletException exception) {
			log.error("[ServletException] message = {}", exception.getMessage());
		} catch (IOException exception) {
			log.error("[IOException] message = {}", exception.getMessage());
		} catch (Exception exception) {
			log.error("[Exception] message = {}", exception.getMessage());
			req.setAttribute("exception", ServerErrorCode.INTERNAL_SERVER_ERROR.customException("Server Error : "+exception.getMessage()));
			var dispatcher = req.getRequestDispatcher("/WEB-INF/error/customError.jsp");
			try{
				dispatcher.forward(req, res);
			} catch (ServletException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
	}

	public void destroy() {
	}
}