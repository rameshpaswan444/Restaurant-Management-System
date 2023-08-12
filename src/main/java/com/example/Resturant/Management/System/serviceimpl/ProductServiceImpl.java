package com.example.Resturant.Management.System.serviceimpl;

import com.example.Resturant.Management.System.constents.RestaurantConstants;
import com.example.Resturant.Management.System.dao.ProductDao;
import com.example.Resturant.Management.System.jwt.JwtFilter;
import com.example.Resturant.Management.System.pojo.Category;
import com.example.Resturant.Management.System.pojo.Product;
import com.example.Resturant.Management.System.service.ProductService;
import com.example.Resturant.Management.System.utils.RestaurantUtils;
import com.example.Resturant.Management.System.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    ProductDao productDao;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
     try {
         if (jwtFilter.isAdmin()){
             if (validateProductMap(requestMap,false)){
                 productDao.save(getProductFromMap(requestMap,false));
                 return RestaurantUtils.getResponseEntity("Product Added Successfully",HttpStatus.OK);

             }
             return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);


         }else {
             return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

         }

     }catch (Exception ex){
         ex.printStackTrace();
     }
     return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);


    }




    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {

        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            } else if (!validateId) {
                return true;

            }
        }
        return false;
    }
    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {

        Category category=new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));



        Product product = new Product();
        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;

    }
    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap,true)){
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        Product product= getProductFromMap(requestMap,true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return RestaurantUtils.getResponseEntity("Product updated Successfully",HttpStatus.OK);


                    }else {
                        return RestaurantUtils.getResponseEntity("Product id doesnot exist.",HttpStatus.OK);

                    }

                }else{
                    return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);

                }

            }else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional = productDao.findById(id);
                if (!optional.isEmpty()){
                    productDao.deleteById(id);
                    return RestaurantUtils.getResponseEntity("Product deleted Successfully",HttpStatus.OK);

                }
                    return RestaurantUtils.getResponseEntity("Product id doesnot exist",HttpStatus.OK);

            }else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

            }

        }catch (Exception ex){
            ex.printStackTrace();

        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional optional =  productDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()){
                    productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));

                    return RestaurantUtils.getResponseEntity("Product Status update Successfully",HttpStatus.OK);


                }else{
                    return RestaurantUtils.getResponseEntity("Product id doesnot exists",HttpStatus.OK);

                }

            }else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
       try {
           return new ResponseEntity<>(productDao.getProductByCategory(id),HttpStatus.OK);

       }catch (Exception ex){
           ex.printStackTrace();
       }
       return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();

        }
        return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
