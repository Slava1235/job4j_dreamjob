package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Photo;
import ru.job4j.dream.model.Post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class PsqlStore implements Store {
    private static final Logger logger = Logger.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            logger.info("Error");
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            logger.info("Error");
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("id"), it.getString("name"), it.getString("photoId")));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return candidates;
    }

    @Override
    public Collection<Photo> findAllPhoto() {
        List<Photo> photo = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM photo")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    photo.add(new Photo(it.getInt("id"), it.getString("photoId")));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return photo;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    @Override
    public void save(Photo photo) {
        if (photo.getId() == 0) {
            create(photo);
        } else {
           update(photo);
        }
    }

    private void create(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO photo(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, photo.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    photo.setId(id.getInt(1));
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO post(name) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            logger.info("Error");
        }
        return post;
    }

    private Candidate create(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO candidate(name , photoid) VALUES (?, ?) ", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
           ps.setString(2, candidate.getPhotoId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return candidate;
    }

    public void update(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update post set name = (?) where id =(?)")
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
        } catch (Exception e) {
            logger.info("Error");
        }
    }

    public void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update candidate set name = (?) where id =(?)")
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getId());
            ps.execute();
        } catch (Exception e) {
            logger.info("Error");
        }
    }

    public void update(Photo photo) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update photo set name = (?) where id =(?)")
        ) {
            ps.setString(1, photo.getName());
            ps.setInt(2, photo.getId());
            ps.execute();
        } catch (Exception e) {
            logger.info("Error");
        }
    }


    @Override
    public Post findByIdPost(int id) {
        Post post = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post WHERE id = (?)")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    String name = it.getString(2);
                    post = new Post(id, name);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return post;
    }

    @Override
    public Candidate findByIdCandidate(int id) {
        Candidate candidate = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM candidate WHERE id = (?)");
             PreparedStatement psPhoto = cn.prepareStatement("SELECT * FROM photo WHERE id = (?)")
        ) {
            ps.setInt(1, id);
            psPhoto.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                try (ResultSet itPhoto = psPhoto.executeQuery()) {
                    if (itPhoto.next()) {
                        if (it.next()) {
                            String name = it.getString(2);
                            String photo = itPhoto.getString(1);
                            candidate = new Candidate(id, name, photo);
                        }
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return candidate;
    }

    @Override
    public Photo findByIdPhoto(int id) {
        Photo photo = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM photo WHERE id = (?)")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    String name = it.getString(2);
                    photo = new Photo(id, name);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return photo;
    }

}