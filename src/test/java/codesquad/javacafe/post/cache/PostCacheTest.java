package codesquad.javacafe.post.cache;

import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.post.controller.PostController;
import codesquad.javacafe.post.dto.request.PostCreateRequestDto;
import codesquad.javacafe.post.dto.response.PostResponseDto;
import codesquad.javacafe.post.entity.Post;
import codesquad.javacafe.post.repository.PostRepository;
import codesquad.javacafe.util.CustomHttpServletRequest;
import codesquad.javacafe.util.CustomHttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 캐시 성능 테스트
 */
class PostCacheTest {

    private PostController postController;
    private Connection connection;

    @Test
    @DisplayName("캐시 히트는 항상 디스크 IO 보다 빠르다")
    void cacheTest() throws SQLException, ServletException, IOException {
        cacheSetUp();

        // post 하나 저장
        Map<String, String[]> body = new HashMap<>();
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();

        List<Long> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            body.put("writer", new String[]{"writer1"});
            body.put("title", new String[]{"title1"});
            body.put("contents", new String[]{"contents1"});
            PostCreateRequestDto postDto = new PostCreateRequestDto(body);
            Post save = PostRepository.getInstance().save(postDto);
            list.add(save.getId());
        }

        long notCacheStart = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            ((CustomHttpServletRequest) request).setMethod("GET");
            ((CustomHttpServletRequest) request).addParameter("postId",  + list.get(i)+ "");
            postController.doProcess(request, response);

        }
        long notCacheEnd = System.currentTimeMillis();
        long notCacheTime = notCacheEnd - notCacheStart;



        long cacheHitStart = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            ((CustomHttpServletRequest) request).setMethod("GET");
            ((CustomHttpServletRequest) request).addParameter("postId",  + list.get(i)+ "");
            postController.doProcess(request, response);

        }
        long cacheHitEnd = System.currentTimeMillis();
        long cacheHitTime = cacheHitEnd - cacheHitStart;

        System.out.println("notCacheTime: " + notCacheTime);
        System.out.println("cacheHitTime: " + cacheHitTime);
        assertTrue(cacheHitTime < notCacheTime);

        clearTable();
    }

    @Test
    @DisplayName("LRU 테스트")
    void lruCacheTest() {
        List<PostResponseDto> postList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PostResponseDto postResponseDto = new PostResponseDto(i, "writer" + i, "title" + i, "contents" + i, LocalDateTime.now());
            postList.add(postResponseDto);
            PostCache.getInstance().set(i, postResponseDto);
        }


        List<PostResponseDto> cacheList = PostCache.getInstance().getCacheList();
        List<Long> list = cacheList.stream().map(PostResponseDto::getId).collect(Collectors.toList());

        List<Long> expectedList = List.of(0L, 1L, 2L, 3L, 4L);
        assertEquals(expectedList, list);

        PostCache.getInstance().get(2);
        list = new ArrayList<>();
        for (PostResponseDto postResponseDto : PostCache.getInstance().getCacheList()) {
            list.add(postResponseDto.getId());
        }

        expectedList = List.of(0L, 1L, 3L, 4L, 2L);
        assertEquals(expectedList,list);

        for (int i = 5; i <= 100; i++) {
            PostResponseDto postResponseDto = new PostResponseDto(i, "writer" + i, "title" + i, "contents" + i, LocalDateTime.now());
            PostCache.getInstance().set(i, postResponseDto);
        }

        assertNull(PostCache.getInstance().get(0));

    }

    void cacheSetUp() throws SQLException {
        postController = new PostController();
        DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        connection = DBConnection.getConnection();
        createTable();
    }

    void createTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement statement = connection.createStatement();

        // Create the post table
        String createTableSql = "CREATE TABLE post (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "post_writer VARCHAR(255), " +
                "post_title VARCHAR(255), " +
                "post_contents TEXT, " +
                "post_create TIMESTAMP" +
                ")";
        statement.execute(createTableSql);

        statement.close();
        connection.close();
    }

    private void clearTable() throws SQLException {
        var sql = "DELETE FROM post";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}