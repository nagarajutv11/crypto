package com.crypto.main.controllrer;


import org.springframework.data.repository.CrudRepository;

import com.crypto.main.core.PathData;

public interface CryptoRepository extends CrudRepository<PathData, String>{
}
