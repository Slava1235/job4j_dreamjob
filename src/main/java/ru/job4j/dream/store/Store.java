package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Photo;
import ru.job4j.dream.model.Post;

import java.util.Collection;

public interface Store {
    Collection<Post> findAllPosts();

    Collection<Candidate> findAllCandidates();

    Collection<Photo> findAllPhoto();

    void save(Post post);

    void save(Photo photo);

    void update(Post post);

    void update(Candidate candidate);

    void update(Photo photo);

    void save(Candidate candidate);


    Post findByIdPost(int id);

    Candidate findByIdCandidate(int id);

    Photo findByIdPhoto(int id);


}