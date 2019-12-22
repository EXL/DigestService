package ru.exlmoto.lab;

import java.util.ArrayList;

public class DigestModelFactory {
    private ArrayList<DigestItem> items;

    public DigestModelFactory() {
        items = new ArrayList<>();
    }

    private class DigestItem {
        private String username;
        private String group;
        private String avatar;
        private String message;
        private String date;

        public DigestItem(String username, String group, String avatar, String message, String date) {
            this.username = username;
            this.group = group;
            this.avatar = avatar;
            this.message = message;
            this.date = date;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public void addDigest(String username, String group, String avatar, String message, String date) {
        items.add(new DigestItem(username, group, avatar, message, date));
    }

    public ArrayList<DigestItem> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }
}
