package codesquad.javacafe.post.controller;

import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.util.CustomHttpServletRequest;
import codesquad.javacafe.util.CustomHttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PostPageControllerTest {
    PostCreatePageController postPageController = new PostCreatePageController();

    @Test
    public void test_replicate_process_get_request_forwarding_with_assertions() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("GET");

        Member member = new Member("user1", "password", "User One");
        MemberRepository.getInstance().save(member);
        request.setAttribute("userId","user1");
        request.getSession().setAttribute("loginInfo", new MemberInfo(1, "user1", "User One"));

        postPageController.doProcess(request, response);

        assertEquals("/WEB-INF/qna/form.jsp", ((CustomHttpServletResponse) response).getForwardedUrl());

    }

    @Test
    public void test_replicate_process_get_request_forwarding_with_non_login() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("GET");

        CustomException exception = assertThrows(CustomException.class, () -> {
            postPageController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.UNAUTHORIZED_USER.getHttpStatus(), exception.getHttpStatus());

    }
}