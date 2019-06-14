package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import service.ImpresaService;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages= {"controller","service"})
@SpringBootApplication
public class ProgettoEsameImpreseApplication {

	public static void main(String[] args) {
		//URL contenente il dataset
				String url = "https://www.dati.gov.it/api/3/action/package_show?id=866cfedc-8eb9-4784-9ec4-f5730f252e89";
				if(args.length == 1)
					url = args[0]; //Url by args ;-)
				try {
					
					//creazione connessione 
					URLConnection openConnection = new URL(url).openConnection();
					openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
					InputStream in = openConnection.getInputStream();
					
					//Parsing JSON
					 String data = "";
					 String line = "";
					 try {
					   InputStreamReader inR = new InputStreamReader( in );
					   BufferedReader buf = new BufferedReader( inR );
					  
					   while ( ( line = buf.readLine() ) != null ) {
						   data+= line;
						   System.out.println( line );
					   }
					 } finally {
					   in.close();
					 }
					 
					 //Ricerca dell'URL contente il csv
					JSONObject obj = (JSONObject) JSONValue.parseWithException(data); 
					JSONObject objI = (JSONObject) (obj.get("result"));
					JSONArray objA = (JSONArray) (objI.get("resources"));
					try {
					for(Object o: objA){
					    if ( o instanceof JSONObject ) {
					        JSONObject o1 = (JSONObject)o; 
					        String format = (String)o1.get("format");
					        String urlD = (String)o1.get("url");
					        System.out.println(format + " | " + urlD);
					        if(format.equals("csv")) {
					        	Downloader file=new Downloader();
					        	file.download_data_set(urlD, "ImpreseOOP.csv");
					        }
					    }
					  }
					System.out.println( "OK" );
					} catch (FileAlreadyExistsException e) {
						e=new FileAlreadyExistsException("File già esistente");
						e.getMessage();
					}
				} catch (IOException | ParseException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				SpringApplication.run(ProgettoEsameImpreseApplication.class, args);
				//ImpresaService impserv=new ImpresaService();
				//System.out.println(impserv.filterField("NumImp", ">", 10000));
			}

}
