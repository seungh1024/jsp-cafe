package codesquad.javacafe.auth.controller;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.javacafe.auth.dto.request.LoginRequestDto;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.auth.service.AuthService;
import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.member.service.MemberService;
import codesquad.javacafe.post.service.PostService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthController implements SubController {
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Override
	public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("[AuthController doProcess]");
		var method = req.getMethod();
		log.info("[AuthController doProcess] method: {}", method);
		switch (method) {
			case "GET":{
				var dispatcher = req.getRequestDispatcher("/user/login.jsp");
				dispatcher.forward(req,res);
				break;
			}
			case "POST": {
				var body = req.getParameterMap();
				if (body.isEmpty()) {
					throw ClientErrorCode.PARAMETER_IS_NULL.customException("user parameter info = "+body);
				}
				var loginDto = new LoginRequestDto(body);
				log.info("[AuthController doProcess] loginDto: {}", loginDto);
				MemberInfo loginInfo = AuthService.getInstance().getLoginInfo(loginDto);
				var session = req.getSession();
				session.setAttribute("loginInfo", loginInfo);
				session.setMaxInactiveInterval(30 * 60); // 세션 30분 설정


				redirectMainPage(req, res);
				break;
			}
			case "DELETE":{
				log.debug("Auth DELETE");
				var session = req.getSession();
				session.removeAttribute("loginInfo");
				res.setStatus(200);
				break;
			}
			default:
				throw ClientErrorCode.METHOD_NOT_ALLOWED.customException("AuthController request method = " + method);
		}
	}

	public void redirectMainPage(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		var postList = PostService.getInstance().getAllPosts(1);
		req.setAttribute("postList", postList);
		var dispatcher = req.getRequestDispatcher("/WEB-INF/index.jsp");
		dispatcher.forward(req, res);
	}
}
