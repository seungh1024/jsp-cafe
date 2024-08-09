package codesquad.javacafe.comment.controller;

import codesquad.javacafe.comment.dto.request.CommentRequestDto;
import codesquad.javacafe.comment.service.CommentService;
import codesquad.javacafe.comment.util.LocalDateTimeSerializer;
import codesquad.javacafe.common.SubController;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.util.JsonUtils;
import com.google.gson.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

public class CommentController implements SubController {
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @Override
    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        log.info("[CommentController doProcess]");
        var method = req.getMethod();
        log.info("[CommentController doProcess] Method: {}", method);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .create();

        switch (method) {
            case "GET": {
                var postId = Long.parseLong(req.getParameter("postId"));
                var lastCreated = LocalDateTime.parse(req.getParameter("lastCreated").replace(" ","T"));
                var lastCommentId = Long.parseLong(req.getParameter("lastCommentId"));
                log.debug("[CommentController] Query parameter Info : postId = {}, lastCreated = {}, lastCommentId = {}", postId, lastCreated, lastCommentId);
                var commentList = CommentService.getInstance().getCommentList(postId, lastCreated, lastCommentId);
                log.debug("[CommentController] Comment List: {}", commentList);

                if (Objects.isNull(commentList)) {
                    res.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    var jsonResponse = gson.toJson(commentList);
                    log.debug("[CommentController] jsonResponse : {}", jsonResponse);
                    JsonUtils.sendResponse(res, jsonResponse);
                }

                break;
            }
            case "POST": {
                var body = JsonUtils.getBody(req);
                log.debug("data get");
                // Extract data from JSON object
                long postId = body.get("postId").getAsLong();
                long memberId = body.get("memberId").getAsLong();
                String comment = body.get("comment").getAsString();
                log.debug("data get success");

                var data = new HashMap<String, Object>();
                data.put("postId", postId);
                data.put("memberId", memberId);
                data.put("comment", comment);

                log.debug("data = {}", data);
                var commentRequestDto = new CommentRequestDto(data);
                log.debug("[CommentController Save] commentRequestDto: {}", commentRequestDto);
                var commentResponseDto = CommentService.getInstance().save(commentRequestDto);
                log.debug("[CommentController Response DTO] commentResponseDto: {}", commentResponseDto);

                var jsonResponse = gson.toJson(commentResponseDto);
                log.debug("[CommentController Response JSON] jsonResponse: {}", jsonResponse);

                JsonUtils.sendResponse(res, jsonResponse);
                break;
            }
            case "DELETE": {
                var body = JsonUtils.getBody(req);
                try {
                    var commentId = body.get("commentId").getAsLong();
                    log.debug("CommentController Delete] commentId: {}", commentId);
                    CommentService.getInstance().delete(commentId);
                    res.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } catch (Exception exception) {
                    throw ClientErrorCode.PARAMETER_IS_NULL.customException("Comment Request Info = " + body);
                }
                break;
            }
            default:
                throw ClientErrorCode.METHOD_NOT_ALLOWED.customException("request uri = " + req.getRequestURI() + ", request method = " + method);
        }
    }

}
