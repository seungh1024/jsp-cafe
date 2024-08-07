package codesquad.javacafe.member.controller;

import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MemberProfileController implements SubController {
    private static final Logger log = LoggerFactory.getLogger(MemberProfileController.class);
    private static final MemberService memberService = MemberService.getInstance();

    @Override
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        log.info("[MemberInfoController doProcess]");
        var method = req.getMethod();
        log.info("[MemberInfoController doProcess] method: {}", method);

        switch (method) {
            case "GET" : {
                var userId = req.getParameter("userId");
                var memberInfo = memberService.getMemberInfo(userId);
                req.setAttribute("memberInfo", memberInfo);
                var dispatcher = req.getRequestDispatcher("/WEB-INF/user/profile.jsp");
                dispatcher.forward(req, res);
                break;
            }
            default: throw ClientErrorCode.METHOD_NOT_ALLOWED.customException("Request Method = "+method);
        }
    }
}
