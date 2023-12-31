package com.example.Resturant.Management.System.restimpl;

import com.example.Resturant.Management.System.constents.RestaurantConstants;
import com.example.Resturant.Management.System.rest.BillRest;
import com.example.Resturant.Management.System.service.BillService;
import com.example.Resturant.Management.System.utils.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BillRestImpl implements BillRest {
    @Autowired
    BillService billService;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
      try {
          return billService.generateReport(requestMap);

      }catch (Exception ex){
          ex.printStackTrace();
      }
      return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);


    }
}
