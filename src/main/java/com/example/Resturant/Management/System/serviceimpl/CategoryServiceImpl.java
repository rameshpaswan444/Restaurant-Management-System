package com.example.Resturant.Management.System.serviceimpl;

import com.example.Resturant.Management.System.constents.RestaurantConstants;
import com.example.Resturant.Management.System.dao.CategoryDao;
import com.example.Resturant.Management.System.jwt.JwtFilter;
import com.example.Resturant.Management.System.pojo.Category;
import com.example.Resturant.Management.System.service.CategoryService;
import com.example.Resturant.Management.System.utils.RestaurantUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateCategoryMap(requestMap,false)){
                    categoryDao.save(getCategoryFromMap(requestMap,false));
                    return RestaurantUtils.getResponseEntity("Category Added Successfully.",HttpStatus.OK);

                }

            }else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }



    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {

        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            }else if (!validateId){
                return true;
            }
        }
        return false;
    }
    private Category getCategoryFromMap(Map<String,String> requestMap,Boolean isAdd){
        Category category = new Category();
        if (isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));

        }
        category.setName(requestMap.get("name"));
        return category;
    }
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if (!Strings.isEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){

                log.info("Inside if");
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
            }else {
                return new ResponseEntity<List<Category>>(categoryDao.findAll(),HttpStatus.OK);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
       try {
           if (jwtFilter.isAdmin()){
               if (validateCategoryMap(requestMap,true)){
                Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()){
                    categoryDao.save(getCategoryFromMap(requestMap,true));
                    return RestaurantUtils.getResponseEntity("Category Updated successfully",HttpStatus.OK);


                }else {
                    return RestaurantUtils.getResponseEntity("Category id doesnot exist",HttpStatus.OK);

                }
               }
               return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);


           }else {
               return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

           }

       }catch (Exception ex){
           ex.printStackTrace();
       }
       return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
