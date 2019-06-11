package controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.Impresa;
import model.Metadati;
import service.ImpresaService;

@RestController
@RequestMapping
public class ImpresaController {
	
	@Autowired
	ImpresaService impserv;
	
	@GetMapping(value="/metadata") 
	public ArrayList<Metadati> getMetadati(){
		return impserv.getMetadati();
	}
	
	@GetMapping(value="/data") 
	public ArrayList<Impresa> getData() {
		return impserv.getData();
	}
	

}
