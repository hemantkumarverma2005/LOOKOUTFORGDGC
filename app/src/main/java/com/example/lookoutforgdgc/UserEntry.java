package com.example.lookoutforgdgc;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.lookoutforgdgc.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserEntry {
    private static final String FILE_NAME = "users_db.txt";
    private static final String SEP = "\t";

    /**
     * Saves a new user to the user database file.
     * @param context The application context.
     * @param user The User object to save.
     * @return true if the user was saved successfully, false otherwise.
     */
    public static synchronized boolean saveUser(Context context, User user) {
        if (context == null || user == null) return false;
        if (TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getPassword())) return false;

        if (!TextUtils.isEmpty(user.getEmail()) && !Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
            return false;
        }
        if (isUsernameTaken(context, user.getUserName())) return false;

        user.setId(generateNextId(context));

        File file = new File(context.getFilesDir(), FILE_NAME);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(format(user));
            bw.newLine();
            bw.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks if a username is already taken.
     * @param context The application context.
     * @param username The username to check.
     * @return true if the username is taken, false otherwise.
     */
    public static synchronized boolean isUsernameTaken(Context context, String username) {
        if (context == null || TextUtils.isEmpty(username)) return true;
        List<User> users = readAll(context);
        for (User u : users) {
            if (u.getUserName().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user exists with the given username and password.
     * @param context The application context.
     * @param username The username.
     * @param password The password.
     * @return true if a user with matching credentials is found, false otherwise.
     */
    public static synchronized boolean checkUser(Context context, String username, String password) {
        if (context == null || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) return false;
        List<User> users = readAll(context);
        for (User u : users) {
            if (u.getUserName().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the score of a user.
     * @param context The application context.
     * @param username The username of the user.
     * @return The user's score, or -1 if the user is not found.
     */
    public static synchronized long getScore(Context context, String username) {
        if (context == null || TextUtils.isEmpty(username)) return -1;
        List<User> users = readAll(context);
        for (User u : users) {
            if (u.getUserName().equalsIgnoreCase(username)) {
                return u.getScore();
            }
        }
        return -1;
    }

    /**
     * Updates the score of a user.
     *
     * @param context  The application context.
     * @param username The username of the user to update.
     * @param newScore The new score.
     */
    public static synchronized void addScore(Context context, String username, long newScore) {
        if (context == null || TextUtils.isEmpty(username)) return;
        List<User> users = readAll(context);
        boolean updated = false;
        for (User u : users) {
            if (u.getUserName().equalsIgnoreCase(username)) {
                u.setScore(u.getScore()+newScore);
                updated = true;
                break;
            }
        }
        if (updated) {
            writeAll(context, users);
        }
    }

    /**
     * Retrieves a User object by their username.
     * @param context The application context.
     * @param username The username of the user to retrieve.
     * @return The User object if found, otherwise null.
     */
    public static synchronized User getUser(Context context, String username) {
        if (context == null || TextUtils.isEmpty(username)) return null;
        List<User> users = readAll(context);
        for (User u : users) {
            if (u.getUserName().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }


    private static int generateNextId(Context context) {
        List<User> users = readAll(context);
        int maxId = 0;
        for (User u : users) {
            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }
        return maxId + 1;
    }

    private static String format(User user) {
        return user.getId() + SEP +
                user.getUserName() + SEP +
                user.getEmail() + SEP +
                user.getPassword() + SEP +
                user.getScore();
    }

    private static User parse(String line) {
        if (TextUtils.isEmpty(line)) return null;
        String[] p = line.split(SEP, -1);
        if (p.length < 5) return null;
        try {
            int id = Integer.parseInt(p[0]);
            String un = p[1];
            String em = p[2];
            String pw = p[3];
            long sc = Long.parseLong(p[4]);
            return new User(id, un, em, pw, sc);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<User> readAll(Context context) {
        List<User> out = new ArrayList<>();
        if (context == null) return out;
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return out;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = parse(line);
                if (u != null) out.add(u);
            }
        } catch (IOException ignored) { }
        return out;
    }

    private static void writeAll(Context context, List<User> users) {
        if (context == null) return;
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (User u : users) {
                bw.write(format(u));
                bw.newLine();
            }
        } catch (IOException ignored) {
        }
    }
}
