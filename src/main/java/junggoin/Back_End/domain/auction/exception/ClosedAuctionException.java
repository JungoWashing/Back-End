package junggoin.Back_End.domain.auction.exception;

public class ClosedAuctionException extends RuntimeException {
    public ClosedAuctionException() {
        super();
    }
    public ClosedAuctionException(String message) {
        super(message);
    }
}
