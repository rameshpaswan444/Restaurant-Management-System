package com.example.Resturant.Management.System.dao;

import com.example.Resturant.Management.System.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category,Integer> {

    List<Category> getAllCategory();
}
