
package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        // Get user from DB
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) return 0;

        int userAge = user.getAge();
        Subscription subscription = user.getSubscription();
        SubscriptionType userSubType = subscription.getSubscriptionType();

        // Get all web series
        List<WebSeries> allWebSeries = webSeriesRepository.findAll();

        int count = 0;
        for (WebSeries webSeries : allWebSeries) {
            if (userAge >= webSeries.getAgeLimit()) {
                SubscriptionType requiredSub = webSeries.getSubscriptionType();
                // Check if user’s subscription allows them to view this series
                if (canView(userSubType, requiredSub)) {
                    count++;
                }
            }
        }

        return count;

    }

    private boolean canView(SubscriptionType userType, SubscriptionType requiredType) {
        if (userType == SubscriptionType.ELITE) return true;
        if (userType == SubscriptionType.PRO && requiredType != SubscriptionType.ELITE) return true;
        return userType == SubscriptionType.BASIC && requiredType == SubscriptionType.BASIC;
    }


}
