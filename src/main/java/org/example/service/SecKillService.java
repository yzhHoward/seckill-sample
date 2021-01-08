package org.example.service;

import org.example.entity.Commodity;

import java.math.BigDecimal;
import java.util.List;

public interface SecKillService {

    List<Commodity> findAll();

    Commodity findById(Long commodityId);

    boolean buy(long commodityId, BigDecimal money, String phone);
}
