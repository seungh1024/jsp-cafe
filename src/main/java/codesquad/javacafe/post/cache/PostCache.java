package codesquad.javacafe.post.cache;

import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.member.dto.request.MemberUpdateRequestDto;
import codesquad.javacafe.post.dto.request.PostRequestDto;
import codesquad.javacafe.post.dto.response.PostResponseDto;

import java.util.*;

public class PostCache {
    private static final PostCache instance = new PostCache();
    private static final int MAX_ENTRIES = 1000;
    // LRU 캐시로 사용
    private static final Map<Long, PostResponseDto> cache = new LinkedHashMap<>(MAX_ENTRIES, 0.75f,true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, PostResponseDto> eldest) {
            return cache.size() > MAX_ENTRIES;
        }
    };

    private PostCache() {
    }

    public static PostCache getInstance() {
        return instance;
    }

    public void set(long id, PostResponseDto postResponseDto) {
        cache.put(id, postResponseDto);
    }

    public PostResponseDto get(long id) {
        cache.get(id);

        return cache.get(id);
    }

    public List<PostResponseDto> getCacheList() {
        return cache.values().stream().toList();
    }

    public void updateCache(PostRequestDto postRequestDto) {
        var postDto = cache.get(postRequestDto.getId());
        if(postDto != null) {
            postDto.update(postRequestDto);
            cache.put(postDto.getId(), postDto);
        }
    }

    public void updateCache(long id, String name) {
        for(Map.Entry<Long, PostResponseDto> entry : cache.entrySet()) {
            var value = entry.getValue();
            if (value.getMemberId() == id) {
                value.updateMemberName(name);
            }
        }
    }

    public void deletePost(long postId) {
        cache.remove(postId);
    }
}
