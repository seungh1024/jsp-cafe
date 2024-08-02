package codesquad.javacafe.comment.dto.response;

import codesquad.javacafe.comment.entity.Comment;
import codesquad.javacafe.member.entity.Member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentResponseDto {
    private long id;
    private long postId;
    private String comment;
    private LocalDateTime createdAt;
    private Member member;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.member = comment.getMember();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
    }

    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
    }

    public long getMemberId() {
        return member.getId();
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String toString() {
        return "CommentResponseDto{" +
                "id=" + id +
                ", postId=" + postId +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ", member=" + member +
                '}';
    }
}