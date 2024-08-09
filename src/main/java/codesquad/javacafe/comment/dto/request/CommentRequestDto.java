package codesquad.javacafe.comment.dto.request;

import codesquad.javacafe.comment.entity.Comment;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.member.entity.Member;

import java.util.Map;

public class CommentRequestDto {
    private long postId;
    private long memberId;
    private String comment;

    public CommentRequestDto(long postId, long memberId, String comment) {
        this.postId = postId;
        this.memberId = memberId;
        this.comment = comment;
    }

    public CommentRequestDto(Map<String,Object> body) {
        try {
            this.postId = (long) body.get("postId");
            this.memberId = (long) body.get("memberId");
            this.comment = (String) body.get("comment");
        } catch (Exception exception) {
            throw ClientErrorCode.PARAMETER_IS_NULL.customException("CommentRequestDto = " + body);
        }
    }

    public Comment toEntity() {
        var member = new Member();
        member.setId(this.memberId);
        return new Comment(this.postId, this.comment, member);
    }

    @Override
    public String toString() {
        return "CommentRequestDto{" +
                "postId=" + postId +
                ", memberId=" + memberId +
                ", comment='" + comment + '\'' +
                '}';
    }
}
