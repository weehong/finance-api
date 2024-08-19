package com.mattemat.finance.repository;

import com.mattemat.finance.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    @Query(value = "SELECT value FROM currencies WHERE code = ?1", nativeQuery = true)
    BigDecimal findByCurrency(String code);
}
