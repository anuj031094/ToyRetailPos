package com.ey.sales.repository;

import com.ey.sales.model.SalesData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<SalesData, Integer> {
}
