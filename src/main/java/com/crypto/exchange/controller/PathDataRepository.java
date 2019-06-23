package com.crypto.exchange.controller;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crypto.exchange.core.PathData;
import com.crypto.exchange.core.TopCoin;

@Repository
public interface PathDataRepository extends CrudRepository<PathData, Long> {
	
	@Query(value = "select name, left_code as code, gain\r\n" + 
			"	from top_coins where top_rnk <= :rank and name in :wallets \r\n" + 
			"    order by top_rnk", nativeQuery = true)
	List<TopCoin> findTopCoins(@Param("wallets") List<String> wallets, @Param("rank") int rank);
}
