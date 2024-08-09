package codesquad.javacafe.comment.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.javacafe.comment.repository.CommentRepository;

public class AsyncCommentDeleter {
	private static final AsyncCommentDeleter instance = new AsyncCommentDeleter();
	private static final Logger log = LoggerFactory.getLogger(AsyncCommentDeleter.class);
	private final ExecutorService[] threadPoolExecutor;
	private static final int MAX_QUEUE_ARRAY_SIZE = 10;

	private AsyncCommentDeleter() {
		threadPoolExecutor = new ExecutorService[MAX_QUEUE_ARRAY_SIZE];
		for (int i = 0; i < MAX_QUEUE_ARRAY_SIZE; i++) {
			threadPoolExecutor[i] = Executors.newSingleThreadExecutor();
		}
	}
	public static AsyncCommentDeleter getInstance() {
		return instance;
	}

	public void asyncDelete(long postId, AtomicInteger retryCount) {
		var hashCode = (int)(postId % MAX_QUEUE_ARRAY_SIZE);

		threadPoolExecutor[hashCode].execute(()->{
			int result = CommentRepository.getInstance().deletePostComments(postId);
			log.debug("[Post Comments All Deleted] delete count = "+result);
			if (result == 0) {
				var retry = retryCount.incrementAndGet();
				if (retry == 5) {
					log.error("[AsyncCommentDeleter] Failed to delete post comment with id {}", postId);
				}else {
					asyncDelete(postId,retryCount);
				}
			}
		});
	}
}
