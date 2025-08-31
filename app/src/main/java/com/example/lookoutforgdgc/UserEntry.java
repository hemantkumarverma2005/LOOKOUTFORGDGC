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

    public static synchronized boolean isUsernameTaken(Context context, String username) {
        if (context == null || TextUtils.isEmpty(username)) return false;
        for (User u : readAll(context)) {
            if (username.equalsIgnoreCase(u.getUserName())) return true;
        }
        return false;
    }

    public static synchronized boolean checkUser(Context context, String username, String password) {
        if (context == null) return false;
        for (User u : readAll(context)) {
            if (username != null && password != null &&
                    username.equalsIgnoreCase(u.getUserName()) &&
                    password.equals(u.getPassword())) return true;
        }
        return false;
    }

    public static synchronized long getScore(Context context, String username) {
        if (context == null || TextUtils.isEmpty(username)) return 0L;
        for (User u : readAll(context)) {
            if (username.equalsIgnoreCase(u.getUserName())) return u.getScore();
        }
        return 0L;
    }

    public static synchronized boolean addScore(Context context, String username, long newScore) {
        if (context == null || TextUtils.isEmpty(username)) return false;
        List<User> all = readAll(context);
        boolean updated = false;
        for (User u : all) {
            if (username.equalsIgnoreCase(u.getUserName())) {
                u.setScore(newScore);
                updated = true;
                break;
            }
        }
        return updated && writeAll(context, all);
    }

    private static int generateNextId(Context context) {
        int max = 0;
        for (User u : readAll(context)) max = Math.max(max, u.getId());
        return max + 1;
    }

    private static String format(User u) {
        String un = (u.getUserName() == null ? "" : u.getUserName()).replace("\n", " ").replace("\t", " ");
        String em = (u.getEmail() == null ? "" : u.getEmail()).replace("\n", " ").replace("\t", " ");
        String pw = (u.getPassword() == null ? "" : u.getPassword()).replace("\n", " ").replace("\t", " ");
        return u.getId() + SEP + un + SEP + em + SEP + pw + SEP + u.getScore();
    }

    private static User parse(String line) {
        if (line == null) return null;
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

    private static boolean writeAll(Context context, List<User> users) {
        if (context == null) return false;
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (User u : users) {
                bw.write(format(u));
                bw.newLine();
            }
            bw.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
