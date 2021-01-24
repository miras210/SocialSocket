package kz.malimov.db;

import kz.malimov.models.Profile;
import kz.malimov.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDB {
    public static UserDB userDB = new UserDB();
    private static Connection cn = PortalRepository.getInstance().getConnection();
    private static List<User> users;

    private UserDB() {}

    public static UserDB getInstance() {
        init();
        return userDB;
    }

    private static void init() {
        try {
            Statement s = cn.createStatement();
            ResultSet rs = s.executeQuery("select * from users");
            users = getDBUsers(rs);
        } catch (SQLException e) {
            System.out.println("catch");
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(int id) {
        for (User u : users) {
            if (u.getId() == id) {
                return u.copyWithoutPassword();
            }
        }
        return null;
    }

    public void addUser(User user) {
        int id = 0;
        try {
            PreparedStatement ps =
                    cn.prepareStatement("INSERT INTO " +
                            "users(username, password) " +
                            "VALUES (?, ?)");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement("SELECT currval('users_id_seq'::regclass)");
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                id = res.getInt(1);
            }
            user.setId(id);
            users.add(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(int id) {
        users.removeIf(u -> u.getId() == id);
        try {
            PreparedStatement ps = cn.prepareStatement("DELETE FROM users " +
                    "WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> findUsers(User user) {
        List<User> matchList = new ArrayList<>();
        Pattern namePattern = Pattern.compile(user.getUsername(), Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        User copy;
        for (User u:
                users) {
            matcher = namePattern.matcher(u.getUsername());
            if (matcher.find()) {
                copy = u.copyWithoutPassword();
                matchList.add(copy);
            }
        }
        return matchList;
    }

    protected static List<User> getDBUsers(ResultSet rs) {
        List<User> users = new ArrayList<>();
        try {
            User user;
            while (rs.next()) {
                user = new User();
                user.setId(rs.getInt(1));
                user.setUsername(rs.getString(2));
                user.setPassword(rs.getString(3));
                user.setVisibility(rs.getInt(4));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Profile getProfileById(int id) {
        try {
            Profile p = new Profile();
            User u = getUserById(id);
            p.setUsername(u.getUsername());
            p.setVisibility(u.getVisibility());
            p.setPosts(PostsDB.getInstance().getPostsByUserId(id));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}