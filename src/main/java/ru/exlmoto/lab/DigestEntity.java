package ru.exlmoto.lab;

import javax.persistence.*;

/*
    CREATE TABLE `digests` (
        `id` MEDIUMINT(8) unsigned NOT NULL AUTO_INCREMENT,
        `author` MEDIUMINT(8) unsigned NOT NULL,
        `date` INT unsigned NOT NULL,
        `digest` TEXT NOT NULL,
        PRIMARY KEY(`id`)
    ) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ENGINE=MyISAM;
*/

@Entity
@Table(name = "digests")
public class DigestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer author;

    private Integer date;

    private String digest;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
