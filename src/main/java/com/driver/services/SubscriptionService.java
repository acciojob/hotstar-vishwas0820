package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensSubscribed());

        int totalAmount = 0;
        switch (subscription.getSubscriptionType()) {
            case BASIC:
                totalAmount = 500 + (200 * subscription.getNoOfScreensSubscribed());
                break;
            case PRO:
                totalAmount = 800 + (250 * subscription.getNoOfScreensSubscribed());
                break;
            case ELITE:
                totalAmount = 1000 + (350 * subscription.getNoOfScreensSubscribed());
                break;
        }

        subscription.setTotalAmountPaid(totalAmount);
        subscriptionRepository.save(subscription);
        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) {
            return null;
        }

        Subscription subscription = user.getSubscription();
        int priceDifference = 0;
        if (subscription.getSubscriptionType() == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        } else if (subscription.getSubscriptionType() == SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            priceDifference = 300 + (50 * subscription.getNoOfScreensSubscribed());
        } else {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            priceDifference = 200 + (100 * subscription.getNoOfScreensSubscribed());
        }

        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + priceDifference);
        subscriptionRepository.save(subscription);
        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        return null;
    }

}
