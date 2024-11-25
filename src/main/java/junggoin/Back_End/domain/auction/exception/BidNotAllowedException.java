package junggoin.Back_End.domain.auction.exception;

public class BidNotAllowedException extends RuntimeException {
    public BidNotAllowedException() {
        super();
    }

    public BidNotAllowedException(String message) {
        super(message);
    }
}
