package services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import customerreview.AbstractBusinessService;
import customerreview.CommentCurseException;
import customerreview.CustomerReviewModel;
import customerreview.CustomerReviewService;
import customerreview.Product;
import customerreview.ProductModel;
import customerreview.RatingLessThanZeroException;
import customerreview.Required;
import customerreview.User;
import customerreview.UserModel;
import de.hybris.platform.customerreview.dao.CustomerReviewDao;
import de.hybris.platform.customerreview.jalo.CustomerReview;
import de.hybris.platform.customerreview.jalo.CustomerReviewManager;

public class NewService extends AbstractBusinessService implements de.hybris.platform.customerreview.CustomerReviewService{
	
	ArrayList<String> curselist  = new ArrayList<String>();
	{try (BufferedReader br = new BufferedReader(new FileReader("cursewords.txt"))) {

        String currentLine;

        while ((currentLine = br.readLine()) != null) {
            curselist.add(currentLine);
        }
        br.close();
        
    	} catch (IOException e) {
    		e.printStackTrace();
    	} 
	
	}
	
	public CustomerReviewModel createCustomerReview(Double rating, String headline, String comment, UserModel user, ProductModel product)
	{
		Boolean curse = false; 
		Boolean negativeRating = false;
		for (String curseword: curselist) {
			String lowerComment = comment.toLowerCase();
			
			if (lowerComment.contains(curseword)) {
				curse = true;
				break;
			}
		}
		
		if (rating < 0) {
			negativeRating = true;
		}
		
		if (curse == true) {
			throw new CommentCurseException("Your comment contains illegal words. Please remove them.");
		} else if (negativeRating == true) {
			throw new RatingLessThanZeroException("Your rating is less than zero. Please replace it with a rating larger than zero.");
		} else {
			CustomerReview review = CustomerReviewManager.getInstance().createCustomerReview(rating, headline, comment, 
					(User)getModelService().getSource(user), (Product)getModelService().getSource(product));
			
			return (CustomerReviewModel)getModelService().get(review);
		}
	 }
		
	public int getNumberOfReviewsInRange(double top, double bottom) {
		List<CustomerReview> allReviews = CustomerReviewManager.getInstance().getAllReviews((Product)getModelService().getSource(product));
		List<CustomerReview> properReviews;
		
		for (CustomerReview review: allReviews) {
			if ((review.getRating() <= top) && (review.getRating() >= bottom)) {
				properReviews.add(review);
			}
		}
		
		return properReviews.size();
	}
}
