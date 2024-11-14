package DTO;

import java.util.Date;

public class RECOMMENDBOOKS {
    private int recommendID;
    private String userID;
    private String bookName;
    private String writer;
    private String publisher;
    private Date pubDate;
    private Date reDate;
    private String completeYN;

    public int getRecommendID() {
        return recommendID;
    }

    public void setRecommendID(int recommendID) {
        this.recommendID = recommendID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public Date getReDate() {
        return reDate;
    }

    public void setReDate(Date reDate) {
        this.reDate = reDate;
    }

    public String getCompleteYN() {
        return completeYN;
    }

    public void setCompleteYN(String completeYN) {
        this.completeYN = completeYN;
    }
}
