
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
        int userId = subscriptionEntryDto.getUserId();
        SubscriptionType type = subscriptionEntryDto.getSubscriptionType();
        int screens = subscriptionEntryDto.getNoOfScreensRequired();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return -1;

        int totalAmount = 0;

        switch (type) {
            case BASIC:
                totalAmount = 500 + 200 * screens;
                break;
            case PRO:
                totalAmount = 800 + 250 * screens;
                break;
            case ELITE:
                totalAmount = 1000 + 350 * screens;
                break;
        }

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(type);
        subscription.setNoOfScreensSubscribed(screens);
        subscription.setStartSubscriptionDate(new Date());
        subscription.setTotalAmountPaid(totalAmount);
        subscription.setUser(user);

        subscriptionRepository.save(subscription);

        // Link subscription to user and save user
        user.setSubscription(subscription);
        userRepository.save(user);

        return totalAmount;

    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) return -1;

        Subscription subscription = user.getSubscription();
        SubscriptionType currentType = subscription.getSubscriptionType();
        int screens = subscription.getNoOfScreensSubscribed();
        int oldAmount = subscription.getTotalAmountPaid();

        int newAmount = 0;
        SubscriptionType newType;

        switch (currentType) {
            case BASIC:
                newType = SubscriptionType.PRO;
                newAmount = 800 + 250 * screens;
                break;
            case PRO:
                newType = SubscriptionType.ELITE;
                newAmount = 1000 + 350 * screens;
                break;
            case ELITE:
                throw new Exception("Already the best Subscription");
            default:
                return -1;
        }

        int difference = newAmount - oldAmount;

        subscription.setSubscriptionType(newType);
        subscription.setTotalAmountPaid(newAmount);

        subscriptionRepository.save(subscription);

        return difference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int totalRevenue = 0;

        for (Subscription sub : subscriptions) {
            totalRevenue += sub.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
