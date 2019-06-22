package com.crypto.main.controllrer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.main.core.PathData;

public class CryptoService {
	@Autowired  
    private CryptoRepository cryptRepository;
	public void addPathData(PathData pd){  
		cryptRepository.save(pd);  
    }  
	public void addAllPathData(List<PathData> entities){  
		cryptRepository.saveAll(entities);  
    } 
}
