package controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public Collection<Impresa> getData(@RequestParam(name="filter",required=false,defaultValue="null") String query) {
		if(query.equals("null")) return impserv.getData();
		else {
			String[] tokenquery=query.split(":");
			String logicalop=tokenquery[0];
			String query1=tokenquery[1];
			String query2=tokenquery[2];
			System.out.println(logicalop+" -- "+query1+" -- "+query2);
			ArrayList<Impresa> out1=new ArrayList<Impresa>();
			ArrayList<Impresa> out2=new ArrayList<Impresa>();
			//parse query1
			String token[]=query1.split(";");
			String fieldName=token[0];
			String operator=token[1];
			String valuefilter=token[2];
			
				if(!(fieldName.equals("CodAteco")|| fieldName.equals("Dim")|| fieldName.equals("Descrizione"))) {
					return impserv.filterField(fieldName, operator, Integer.parseInt(valuefilter));
			}else return impserv.filterField(fieldName, operator, valuefilter);
			//ArrayList<Impresa> iout=impserv.filterField(fieldName, operator, value1);
			//ArrayList<Impresa>
		}
		
		//if(fieldName.equals("null") && operator.equals("null") && value.equals("null")) return impserv.getData();
			//else return impserv.filterField(fieldName, operator, value);
		
	}
	

}
