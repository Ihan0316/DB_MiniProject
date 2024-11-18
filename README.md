"# DB_MiniProject" 

#### SQL DDL
```
-- 테이블
create table categories(
	categoryID number(4) primary key,
	categoryName varchar2(20) not null,
	description varchar2(100) not null	
);

create table books(
	bookID number(4) primary key,
	bookName varchar2(100) not null,
	writer varchar2(30) not null,
	publisher varchar2(30) not null,
	pubDate date not null,
	bookCTG number(4) constraint ctg_FK references categories(categoryID),
	description varchar2(800) not null,
	stock number(1) default 0
);

create table users(
	userID varchar2(30) primary key,
	password varchar2(20) not null,
	userName varchar2(20) not null,
	tel varchar2(20) not null,
	regdate date not null,
	rentalYN varchar(1) default 'Y',
	delayCount number(1) default 0
);

create table rentals(
	rentalId number(4) primary key,
	userID varchar2(30) constraint uID_FK references users(userID),
	bookID number(4) constraint bookID_FK references books(bookID),
	rentalDate date not null,
	returnDueDate date not null,
	returnDate date,
	rentalState varchar2(10) not null
);

create table reservations(
	rsID number(4) primary key,
	userID varchar2(30) constraint RuID_FK references users(userID),
	bookID number(4) constraint RbookID_FK references books(bookID),
	rsDate date not null,
	rsState varchar2(10) not null
);

create table recommendBooks(
	recommendID number(4) primary key,
	userID varchar2(30) constraint RCuID_FK references users(userID),
	bookName varchar2(100) not null,
	writer varchar2(30) not null,
	publisher varchar2(30) not null,
	pubDate date not null,
	reDate date not null,
	completeYN varchar(1) default 'N'
);

create table reviews(
	reviewID number(4) primary key,
	userID varchar2(30) constraint REVuID_FK references users(userID),
	bookID number(4) constraint REVbookID_FK references books(bookID),
	score number(1) not null,
	review varchar2(100) not null,
	reviewDate date not null
);

-- 시퀀스 (희망도서신청번호, 카테고리번호, 리뷰번호, 대여번호, 예약번호)
CREATE SEQUENCE  "SCOTT"."RCMBOOK_SEQ"  MINVALUE 1 MAXVALUE 999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  CYCLE;
create sequence category_seq start with 1 increment by 1 nocache;
CREATE SEQUENCE  "SCOTT"."REVIEWID_SEQ"  MINVALUE 1 MAXVALUE 999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  CYCLE;
CREATE SEQUENCE  "SCOTT"."RENTALID_SEQ"  MINVALUE 1 MAXVALUE 999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  CYCLE;
CREATE SEQUENCE  "SCOTT"."RESERVATIONID_SEQ"  MINVALUE 1 MAXVALUE 999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  CYCLE;
```
