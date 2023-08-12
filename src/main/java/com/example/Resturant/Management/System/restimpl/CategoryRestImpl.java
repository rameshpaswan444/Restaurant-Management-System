package com.example.Resturant.Management.System.restimpl;

import com.example.Resturant.Management.System.constents.RestaurantConstants;
import com.example.Resturant.Management.System.pojo.Category;
import com.example.Resturant.Management.System.rest.CategoryRest;
import com.example.Resturant.Management.System.service.CategoryService;
import com.example.Resturant.Management.System.utils.RestaurantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

@RestController
public class CategoryRestImpl implements CategoryRest {

    @Autowired
    CategoryService categoryService;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
       try {
           return categoryService.addNewCategory(requestMap);

       }catch (Exception ex){
           ex.printStackTrace();
       }
       return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
     try {
         return categoryService.getAllCategory(filterValue);

     }catch (Exception ex){
         ex.printStackTrace();
     }
     return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
      try {
          return categoryService.updateCategory(requestMap);

      }catch (Exception ex){
          ex.printStackTrace();
      }
      return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
