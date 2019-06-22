package com.crypto.main.controllrer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crypto.main.core.PathData;

@RestController
public class CryptoController {
	@Autowired
	private CryptoService cryptoService;

	@RequestMapping("/")
	public void addPathData(List<PathData> entities) {
		cryptoService.addAllPathData(entities);
	}
}
