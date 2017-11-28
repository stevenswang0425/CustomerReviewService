package services;

public class RatingLessThanZeroException extends Exception {
	
	public RatingLessThanZeroException(String message) {
		super(message);
	}

}

