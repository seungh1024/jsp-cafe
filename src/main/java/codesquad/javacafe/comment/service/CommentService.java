package codesquad.javacafe.comment.service;

import codesquad.javacafe.comment.dto.request.CommentRequestDto;
import codesquad.javacafe.comment.dto.response.CommentResponseDto;
import codesquad.javacafe.comment.entity.Comment;
import codesquad.javacafe.comment.repository.CommentRepository;
import codesquad.javacafe.common.exception.ClientErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentService {

    private static final CommentService instance = new CommentService();
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private CommentService() {}
    public static CommentService getInstance() {
        return instance;
    }

    public CommentResponseDto save(CommentRequestDto commentDto) {
        var comment = commentDto.toEntity();
        var savedComment = CommentRepository.getInstance().save(comment);
        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> getCommentList(long postId, LocalDateTime lastCreated, long lastCommentId) {
        var commentList = CommentRepository.getInstance().findAll(postId, lastCreated, lastCommentId);
        if (Objects.isNull(commentList)) {
            return null;
        }
        return commentList.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    public void delete(long commentId) {
        var result = CommentRepository.getInstance().deleteOne(commentId);
        log.debug("result = {}", result);
        if (result == 0) {
            throw ClientErrorCode.COMMENT_IS_NULL.customException("comment id = " + commentId);
        }
    }
}
