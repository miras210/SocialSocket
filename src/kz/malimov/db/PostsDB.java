package kz.malimov.db;

import kz.malimov.models.Post;
import kz.malimov.models.PostView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class PostsDB {
    public static PostsDB postsDB = new PostsDB();
    private static Connection cn = PortalRepository.getInstance().getConnection();

    private PostsDB() {}

    public static PostsDB getInstance() {
        return postsDB;
    }

    public void addPost(String title, String content, int visibility, int author_id) {
        try {
            PreparedStatement ps = cn.prepareStatement
                    ("INSERT INTO posts (title, content, visibility, author_id) VALUES (?, ?, ?, ?)");
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, visibility);
            ps.setInt(4, author_id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Post> getPostsList(int userId, boolean authorized) {
        try {
            String sqlAuthor = "SELECT DISTINCT post_id, title, content FROM posts p\n" +
                    "LEFT JOIN friends f ON (f.uid1 = p.author_id)\n" +
                    "LEFT JOIN friends c On (c.uid2 = p.author_id)\n" +
                    "WHERE (c.uid1 = f.uid2 AND c.uid1 = ?) OR visibility < 2";
            String unAuth = "SELECT post_id, title, content FROM posts\n" +
                    "WHERe visibility = 0";
            PreparedStatement ps;
            if (authorized) {
                ps = cn.prepareStatement(sqlAuthor);
                ps.setInt(1, userId);
            } else ps = cn.prepareStatement(unAuth);
            ResultSet res = ps.executeQuery();
            List<Post> posts = new LinkedList<>();
            while (res.next()) {
                Post p = new Post();
                p.setId(res.getInt(1));
                p.setTitle(res.getString(2));
                p.setContent(res.getString(3));
                posts.add(p);
            }
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PostView getPostViewById(int id) {
        try {
            PostView p = new PostView();
            PreparedStatement ps = cn.prepareStatement("SELECT post_id, title, content, commentable, author_id " +
                    "FROM posts WHERE post_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                p.setId(rs.getInt(1));
                p.setTitle(rs.getString(2));
                p.setContent(rs.getString(3));
                p.setCommentable(rs.getBoolean(4));
                p.setAuthorId(rs.getInt(5));
            }
            if (p.isCommentable()) {
                ps = cn.prepareStatement("SELECT comment FROM comments WHERE post_id = ?");
                ps.setInt(1, id);
                ResultSet set = ps.executeQuery();
                List<String> comments = new LinkedList<>();
                while (set.next()) {
                    comments.add(set.getString(1));
                }
                p.setComments(comments);
            }
            return p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Post> getPostsByUserId(int id) {
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT post_id, title, content FROM posts" +
                    " WHERE author_id = ?");
            ps.setInt(1, id);
            List<Post> posts = new LinkedList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getInt(1));
                p.setTitle(rs.getString(2));
                p.setContent(rs.getString(3));
                posts.add(p);
            }
            return posts;
        } catch (Exception e) {

        }
        return null;
    }

    public void addComment(String txt, int author_id, int post_id){
        try {
            PreparedStatement ps = cn.prepareStatement
                    ("INSERT INTO comments (author_id, post_id, comment) VALUES (?, ?, ?)");
            ps.setInt(1, author_id);
            ps.setInt(2, post_id);
            ps.setString(3, txt);
            ps.executeUpdate();
        } catch (Exception e){

        }
    }
}
