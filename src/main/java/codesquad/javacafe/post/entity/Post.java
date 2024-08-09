package codesquad.javacafe.post.entity;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Post {
    private long id;
    private String writer;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private long memberId;

    public Post(){}


    public Post(long id, String writer, String title, String contents, long memberId) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.createdAt = LocalDateTime.now();
        this.memberId = memberId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getWriter() {
        return writer;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getMemberId() {
        return memberId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", writer='" + writer + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", createdAt=" + createdAt +
                ", memberId=" + memberId +
                '}';
    }
}
