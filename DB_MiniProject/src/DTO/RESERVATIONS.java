package DTO;

import java.util.Date;

public class RESERVATIONS {
    private int rsID;
    private String userID;
    private int bookID;
    private Date rsDate;
    private String rsState;

    public int getRsID() {
        return rsID;
    }

    public void setRsID(int rsID) {
        this.rsID = rsID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public Date getRsDate() {
        return rsDate;
    }

    public void setRsDate(Date rsDate) {
        this.rsDate = rsDate;
    }

    public String getRsState() {
        return rsState;
    }

    public void setRsState(String rsState) {
        this.rsState = rsState;
    }
}
