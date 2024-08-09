package codesquad.javacafe.post.dto.response;

import codesquad.javacafe.post.dto.request.PostRequestDto;
import codesquad.javacafe.post.entity.Post;

import java.time.LocalDateTime;
import java.util.Objects;

public class PostResponseDto {
    private long id;
    private String writer;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private long memberId;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.writer = post.getWriter();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.createdAt = post.getCreatedAt();
        this.memberId = post.getMemberId();
    }

    public PostResponseDto(long id, String writer, String title, String contents, LocalDateTime createdAt) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public void update(PostRequestDto postRequestDto) {
        this.id = postRequestDto.getId();
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
    }

    public long getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getMemberId() {
        return memberId;
    }

    @Override
    public String toString() {
        return "PostResponseDto{" +
                "id=" + id +
                ", writer='" + writer + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", createdAt=" + createdAt +
                ", memberId=" + memberId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResponseDto that = (PostResponseDto) o;
        return id == that.id && Objects.equals(writer, that.writer) && Objects.equals(title, that.title) && Objects.equals(contents, that.contents) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, writer, title, contents, createdAt);
    }

    public void updateMemberName(String name) {
        this.writer = name;
    }
}
