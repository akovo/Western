package com.starter.repositories;

import com.starter.models.entities.Stock;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StockRepository extends CrudRepository<Stock, String> {

    Stock findByTicker(String ticker);

    List<Stock> findAllBy();

}
