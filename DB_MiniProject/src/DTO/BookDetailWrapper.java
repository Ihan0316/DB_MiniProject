package DTO;

public class BookDetailWrapper {
	private BOOKS bookDto;
    private RENTALS rentalDto;
    private USERS userDto;
    private RESERVATIONS reservationDto;
    private REVIEWS reviewDto;

    public BookDetailWrapper(BOOKS bookDto, RENTALS rentalDto,USERS userDto,RESERVATIONS reservationDto,REVIEWS reviewDto) {
        this.bookDto = bookDto;
        this.rentalDto = rentalDto;
        this.userDto =userDto;
        this.reservationDto = reservationDto;
        this.reviewDto = reviewDto;
    }

    public BOOKS getBookDto() {
        return bookDto;
    }

    public RENTALS getRentalDto() {
        return rentalDto;
    }
    public USERS getUserDto() {
        return userDto;
    }
    public RESERVATIONS getReservationDto() {
        return reservationDto;
    }
    public REVIEWS getReviewDto() {
        return reviewDto;
    }
    
}
