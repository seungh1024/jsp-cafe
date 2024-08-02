package codesquad.javacafe.comment.repository;

import codesquad.javacafe.comment.entity.Comment;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static codesquad.javacafe.common.db.DBConnection.close;
import static codesquad.javacafe.common.db.DBConnection.getConnection;

public class CommentRepository {
    private static final Logger log = LoggerFactory.getLogger(CommentRepository.class);
    private static CommentRepository instance = new CommentRepository();

    private CommentRepository() {

    }

    public static CommentRepository getInstance() {
        return instance;
    }

    public Comment save(Comment comment) {
        var sql = "insert into comment(post_id, member_id, comment_contents, comment_create)\n" +
                "values (?,?,?,?)";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPostId());
            ps.setLong(2, comment.getMember().getId());
            ps.setString(3, comment.getComment());
            ps.setTimestamp(4, Timestamp.valueOf(comment.getCreatedAt()));

            ps.executeUpdate();

            var generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                long pk = generatedKeys.getLong(1);
                log.debug("[Comment PK] {}", pk);
                comment.setId(pk);
            }

            return comment;
        } catch (SQLException exception) {
            log.error("[SQLException] throw error when member save, Class Info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        } finally {
            close(con, ps, null);

        }
    }

    public List<Comment> findAll(long postId) {
        log.debug("[Comment Repository] post id = {}", postId);
        var sql = "select c.id, c.post_id, c.comment_contents, c.comment_create, m.id, m.member_name" +
                " from comment c inner join member m" +
                " on c.member_id = m.id where c.post_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, postId);

            rs = ps.executeQuery();
            if (rs.next()) {
                var commentList = new ArrayList<Comment>();
                do {
                    var comment = new Comment();
                    comment.setId(rs.getLong("c.id"));
                    comment.setPostId(rs.getLong("post_id"));
                    comment.setComment(rs.getString("comment_contents"));
                    comment.setCreatedAt(rs.getTimestamp("comment_create").toLocalDateTime());
                    var id = rs.getLong("m.id");
                    var memberName = rs.getString("member_name");
                    var member = new Member();
                    member.setId(id);
                    member.setName(memberName);
                    comment.setMember(member);

                    commentList.add(comment);
                } while (rs.next());

                System.out.println(commentList);
                return commentList;
            } else {
                log.info("[Comment Repository] 댓글 정보가 없습니다.");
                return null;
            }

        } catch (SQLException exception) {
            log.error("[SQLException] throw error when findById, Class info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        } finally {
            close(con, ps, rs);
        }

    }
}