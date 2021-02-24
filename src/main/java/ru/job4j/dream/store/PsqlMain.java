package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Photo;
import ru.job4j.dream.model.Post;

public class PsqlMain {
    public static void main(String[] args) {
        Store store = PsqlStore.instOf();
//        store.save(new Post(1, "Java Job"));
//        store.update(new Post(2, "Java"));
//        for (Post post : store.findAllPosts()) {
//            System.out.println(post.getId() + " " + post.getName());
//        }
//        System.out.println(store.findByIdPost(3));

//
        store.save(new Candidate(0, "Java Junior", "test"));
        store.update(new Candidate(0, "Java middle", "test"));
        for (Candidate candidate : store.findAllCandidates()) {
            System.out.println(candidate.getId() + " " + candidate.getName() + " " + candidate.getPhotoId());
        }
//        System.out.println(store.findByIdCandidate(1));
//
//        store.save(new Photo(0,"test"));
//        for (Photo photo : store.findAllPhoto()) {
//            System.out.println(photo.getId() + " " + photo.getName());
    }
}

