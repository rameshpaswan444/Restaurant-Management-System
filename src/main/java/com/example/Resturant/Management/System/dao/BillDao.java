package com.example.Resturant.Management.System.dao;

import com.example.Resturant.Management.System.pojo.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository<Bill, Integer> {
}
