package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        // 1. Check if series already exists
        WebSeries existingSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if (existingSeries != null) {
            throw new Exception("Series is already present");
        }

        // 2. Get production house
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).orElse(null);
        if (productionHouse == null) return -1;

        // 3. Create and populate WebSeries object
        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);

        // 4. Save series
        webSeriesRepository.save(webSeries);

        // 5. Update production house's average rating
        List<WebSeries> allSeries = webSeriesRepository.findAll();
        double totalRating = 0;
        int count = 0;

        for (WebSeries ws : allSeries) {
            if (ws.getProductionHouse().getId() == productionHouse.getId()) {
                totalRating += ws.getRating();
                count++;
            }
        }

        double averageRating = totalRating / count;
        productionHouse.setRatings(averageRating);
        productionHouseRepository.save(productionHouse);

        return webSeries.getId();

    }

}