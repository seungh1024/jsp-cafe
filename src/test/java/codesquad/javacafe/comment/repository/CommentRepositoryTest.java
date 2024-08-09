package codesquad.javacafe.comment.repository;

import codesquad.javacafe.comment.entity.Comment;
import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.post.entity.Post;
import codesquad.javacafe.post.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentRepositoryTest {
	private static CommentRepository commentRepository;
	private static Connection connection;
	private static long memberId;
	private static long postId;

	@BeforeAll
	static void setUp() throws SQLException {
		commentRepository = CommentRepository.getInstance();
		DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
		connection = DBConnection.getConnection();
		createTable();
	}

	@BeforeEach
	void init() {
		Member member = new Member("user1", "password", "User One");
		MemberRepository.getInstance().save(member);
		memberId = member.getId();

		Post post = new Post();
		post.setWriter("User One");
		post.setTitle("title1");
		post.setContents("contents1");
		post.setMemberId(memberId);
		post.setCreatedAt(LocalDateTime.now());
		PostRepository.getInstance().save(post);
		postId = post.getId();
	}

	@AfterEach
	void afterEach() throws SQLException {
		clearTable();
	}

	static void createTable() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
		Statement statement = connection.createStatement();

		String createCommentTableSql = "CREATE TABLE if not exists comment (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"post_id BIGINT NOT NULL, " +
			"member_id BIGINT NOT NULL, " +
			"comment_contents TEXT, " +
			"comment_create TIMESTAMP" +
			")";
		statement.execute(createCommentTableSql);

		String createMemberTableSql = "CREATE TABLE if not exists member (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"member_id VARCHAR(255), " +
			"member_password VARCHAR(255), " +
			"member_name VARCHAR(255)" +
			")";
		statement.execute(createMemberTableSql);

		String createPostTableSql = "CREATE TABLE if not exists post (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"post_writer VARCHAR(255), " +
			"post_title VARCHAR(255), " +
			"post_contents TEXT, " +
			"post_create TIMESTAMP," +
			"member_id BIGINT NOT NULL" +
			")";
		statement.execute(createPostTableSql);

		statement.close();
		connection.close();
	}

	private void clearTable() throws SQLException {
		var sql = "DELETE FROM comment";
		try (var statement = connection.createStatement()) {
			statement.execute(sql);
		}

		sql = "DELETE FROM post";
		try (var statement = connection.createStatement()) {
			statement.execute(sql);
		}

		sql = "DELETE FROM member";
		try (var statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}

	@Test
	public void testSaveComment() {
		Comment comment = new Comment();
		comment.setPostId(postId);
		Member member = new Member();
		member.setId(memberId);
		member.setName("User One");
		comment.setMember(member);
		comment.setComment("This is a test comment");
		comment.setCreatedAt(LocalDateTime.now());

		Comment savedComment = commentRepository.save(comment);

		assertNotNull(savedComment.getId());
		assertEquals("This is a test comment", savedComment.getComment());
	}

	@Test
	public void testFindAllComments() {
		Comment comment1 = new Comment();
		comment1.setPostId(postId);
		Member member = new Member();
		member.setId(memberId);
		member.setName("User One");
		comment1.setMember(member);
		comment1.setComment("First comment");
		comment1.setCreatedAt(LocalDateTime.now());
		commentRepository.save(comment1);

		Comment comment2 = new Comment();
		comment2.setPostId(postId);

		Member member2 = new Member("user2", "password", "User Two");
		MemberRepository.getInstance().save(member2);
		comment2.setMember(member2);
		comment2.setComment("Second comment");
		comment2.setCreatedAt(LocalDateTime.now());
		commentRepository.save(comment2);
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;

		List<Comment> comments = commentRepository.findAll(postId,lastCreated,lastCommentId);

		assertNotNull(comments);
		assertEquals(2, comments.size());
		assertEquals("First comment", comments.get(0).getComment());
		assertEquals("Second comment", comments.get(1).getComment());
	}

	@Test
	public void testDeleteComment() {
		Comment comment = new Comment();
		comment.setPostId(postId);
		Member member = new Member();
		member.setId(memberId);
		member.setName("User One");
		comment.setMember(member);
		comment.setComment("Comment to delete");
		comment.setCreatedAt(LocalDateTime.now());
		Comment savedComment = commentRepository.save(comment);
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;

		int result = commentRepository.deleteOne(savedComment.getId());

		assertEquals(1, result);
		List<Comment> comments = commentRepository.findAll(postId, lastCreated, lastCommentId);
		assertNull(comments);
	}

	@Test
	public void testDeletePostComments() {
		Comment comment1 = new Comment();
		comment1.setPostId(postId);
		Member member = new Member();
		member.setId(memberId);
		member.setName("User One");
		comment1.setMember(member);
		comment1.setComment("First comment");
		comment1.setCreatedAt(LocalDateTime.now());
		commentRepository.save(comment1);


		Comment comment2 = new Comment();
		comment2.setPostId(postId);
		Member member2 = new Member("user2", "password", "User Two");
		MemberRepository.getInstance().save(member);
		comment2.setMember(member2);
		comment2.setComment("Second comment");
		comment2.setCreatedAt(LocalDateTime.now());
		commentRepository.save(comment2);
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;

		int result = commentRepository.deletePostComments(postId);

		assertEquals(2, result);
		List<Comment> comments = commentRepository.findAll(postId, lastCreated, lastCommentId);
		assertNull(comments);
	}
}