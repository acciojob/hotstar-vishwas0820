
package com.driver.services;


import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.repository.ProductionHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionHouseService {

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addProductionHouseToDb(ProductionHouseEntryDto productionHouseEntryDto){

        //dtoToEntity
        ProductionHouse productionHouse= new ProductionHouse();
        productionHouse.setName(productionHouseEntryDto.getName());

        productionHouse.setRatings(0.0);

        // Save to the database
        ProductionHouse savedHouse = productionHouseRepository.save(productionHouse);

        // Return the generated ID
        return savedHouse.getId();

    }



}
