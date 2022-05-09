package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final long INITIAL_COUNT = 1;
    private final AtomicLong count;
    private final Map<Long, Post> repository;

    public PostRepositoryImpl() {
        count = new AtomicLong(INITIAL_COUNT);
        repository = new ConcurrentHashMap<>();
    }

    public List<Post> all() {
        return repository.values().stream()
                .filter(value -> !value.isRemoved())
                .collect(Collectors.toList());
    }

    public Optional<Post> getById(long id) {
        var post = repository.get(id);
        if (post != null && !post.isRemoved()) {
            return Optional.of(post);
        } else {
            return Optional.empty();
        }
    }

    public Post save(Post post) {
        long postId = post.getId();
        if (postId == 0) {
            post.setId(count.getAndIncrement());
            repository.put(post.getId(), post);
            return post;
        }
        if (repository.containsKey(postId) && !repository.get(postId).isRemoved()) {
            repository.put(postId, post);
            return post;
        }
        throw new NotFoundException();
    }

    public void removeById(long id) {
        var post = repository.get(id);
        if (post == null) {
            throw new NotFoundException();
        } else {
            post.setRemoved(true);
        }
    }
}