package ru.exlmoto.lab;

import javax.persistence.*;

/*
    CREATE TABLE `digest_users` (
        `id` MEDIUMINT(8) unsigned NOT NULL AUTO_INCREMENT,
        `username` VARCHAR(100) NOT NULL,
        `avatar` MEDIUMTEXT NOT NULL,
        PRIMARY KEY(`id`)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ENGINE=MyISAM;
 */

@Entity
@Table(name = "digest_users")
public class DigestUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String avatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
