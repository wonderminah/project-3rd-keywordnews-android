package com.example.project.keywordnews;

/**
 * Created by SCIT on 2018-03-04.
 */

public class News {

    private String title;       //제목
    private String description; //내용
    private String author;      //기자
    private String link;        //링크
    private String category;    //분류
    private String pubDate;     //날짜
    private String imgSrc;      //사진주소

    public News() {}
    public News(String title, String description, String author, String link, String category, String pubDate, String imgSrc) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.link = link;
        this.category = category;
        this.pubDate = pubDate;
        this.imgSrc = imgSrc;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getPubDate() {
        return pubDate;
    }
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
    public String getImgSrc() {
        return imgSrc;
    }
    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\n' +
                ", description='" + description + '\n' +
                ", author='" + author + '\n' +
                ", link='" + link + '\n' +
                ", category='" + category + '\n' +
                ", pubDate='" + pubDate + '\n' +
                '}';
    }
}
