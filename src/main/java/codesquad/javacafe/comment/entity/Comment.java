package codesquad.javacafe.comment.entity;

import codesquad.javacafe.member.entity.Member;

import java.time.LocalDateTime;

public class Comment {
    private long id;
    private long postId;
    private String comment;
    private LocalDateTime createdAt;
    private Member member;

    public Comment(){}
    public Comment(long postId, String comment, Member member){
        this.postId = postId;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.member = member;
    }

    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
    }


    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Member getMember() {
        return member;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ", member=" + member +
                '}';
    }
}
