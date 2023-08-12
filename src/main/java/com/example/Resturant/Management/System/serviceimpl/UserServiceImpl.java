package com.example.Resturant.Management.System.serviceimpl;

import com.example.Resturant.Management.System.constents.RestaurantConstants;
import com.example.Resturant.Management.System.dao.UserDao;
import com.example.Resturant.Management.System.jwt.CustomerUsersDetailService;
import com.example.Resturant.Management.System.jwt.JwtFilter;
import com.example.Resturant.Management.System.jwt.JwtUtils;
import com.example.Resturant.Management.System.pojo.User;
import com.example.Resturant.Management.System.service.UserService;
import com.example.Resturant.Management.System.utils.EmailUtils;
import com.example.Resturant.Management.System.utils.RestaurantUtils;
import com.example.Resturant.Management.System.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailService customerUsersDetailService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;


    @Override
    public ResponseEntity<String> signup(Map<String, String> requestMap) {

        try {
            log.info("Inside signup {}", requestMap);
            if (validateSignupMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return RestaurantUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);

                } else {
                    return RestaurantUtils.getResponseEntity("Email already exists.", HttpStatus.BAD_REQUEST);
                }

            } else {
                return RestaurantUtils.getResponseEntity(RestaurantConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignupMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
                && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;


    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if (customerUsersDetailService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":" + jwtUtils.generateToken(customerUsersDetailService.getUserDetail().getEmail(),
                            customerUsersDetailService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "wait for admin approval." + "\"}", HttpStatus.BAD_REQUEST);
                }

            }

        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
           if (jwtFilter.isAdmin()){
               return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);


           }else {
               return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
           }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
       try {
           if (jwtFilter.isAdmin()){
              Optional<User> optional =  userDao.findById(Integer.parseInt(requestMap.get("id")));

              if (!optional.isEmpty()){
                  userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                  sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());

                  return RestaurantUtils.getResponseEntity("User Status updated Successfully.",HttpStatus.OK);


              }else {
                  RestaurantUtils.getResponseEntity("User id doesnot exist",HttpStatus.OK);

              }


           }else {
               return RestaurantUtils.getResponseEntity(RestaurantConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

           }


       }catch (Exception ex){
           ex.printStackTrace();
       }
       return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }



    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {

        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")){

            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved","USER:-" + user + "\n is approved by \nADMIN:-"+jwtFilter.getCurrentUser(),allAdmin);

        }else {

            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER:-" + user + "\n is disabled by \nADMIN:-"+jwtFilter.getCurrentUser(),allAdmin);
        }


    }
    @Override
    public ResponseEntity<String> checkToken() {
        return RestaurantUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!userObj.equals(null)){
                if (userObj.getPassword().equals(requestMap.get("oldPassword"))){
                    userObj.setPassword(requestMap.get("newPassword"));
                    userDao.save(userObj);
                    return RestaurantUtils.getResponseEntity("Password Updated Successfully",HttpStatus.OK);


                }

                return RestaurantUtils.getResponseEntity("Incorrect Old Password",HttpStatus.BAD_REQUEST);
            }

            return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isEmpty(user.getEmail())){
                emailUtils.forgotMail(user.getEmail(),"Credentials by Restaurant Management System",user.getPassword());
                return RestaurantUtils.getResponseEntity("Check YOur mail for Credentials",HttpStatus.OK);

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantConstants.SOMETHING_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
