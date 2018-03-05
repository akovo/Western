package com.starter;

import com.starter.controllers.PortfolioController;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;


@SpringBootApplication
public class StarterApplication {

	private static final String API_KEY = "17_cTdFVNIoNNrMxpfHCWg";

	private static final String BASE_URL = "http://oec-2018.herokuapp.com/";

	private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(StarterApplication.class);
		Properties prop = new Properties();
		prop.setProperty("spring.resources.static-locations", "classpath:/static/");
		app.setDefaultProperties(prop);
		app.run(args);
		PortfolioController.loadPortfolioController();
	}



}
