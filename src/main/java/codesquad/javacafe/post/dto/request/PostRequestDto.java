package codesquad.javacafe.post.dto.request;

import codesquad.javacafe.post.entity.Post;

import java.util.Map;

public class PostRequestDto {
    private long id;
    private String writer;
    private String title;
    private String contents;
    private long memberId;

    public PostRequestDto(long id, String title, String contents, long memberId) {
        this. id = id;
        this.title = title;
        this.contents = contents;
        this.memberId = memberId;
    }

    public PostRequestDto(Map<String, String[]> body) {
        this.title = body.get("title")[0];
        this.contents = body.get("contents")[0];
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

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "PostRequestDto{" +
                "id=" + id +
                ", writer='" + writer + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", memberId=" + memberId +
                '}';
    }


    public Post toEntity() {
        return new Post(this.id, this.writer,this.title,this.contents, this.memberId);
    }

    public long getMemberId() {
        return this.memberId;
    }

}
