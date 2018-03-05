package com.starter.controllers;

import com.starter.DataRetrieval;
import com.starter.models.v1.PortfolioOverview;
import com.starter.models.v1.StockDetails;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private static PortfolioOverview overview;
    private static List<StockDetails> details;

    public static void loadPortfolioController() {
        overview = currerntmarketval();
        details = getFullStockDetails();
    }

    @RequestMapping(value = "/value", method = RequestMethod.GET)
    public PortfolioOverview getPortfolio() {
        return overview;
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public List<StockDetails> getDetails() {
        return details;
    }

    public static List<StockDetails> getFullStockDetails(){
        List<StockDetails> stockDetails = new ArrayList<>();
        String url = DataRetrieval.BASE_URL + "api/account?key=" + DataRetrieval.API_KEY;
        String data = executeRequest(url);

        JSONParser parser = new JSONParser();

        try {
            JSONObject result = (JSONObject) parser.parse(data);
            JSONArray holdings = (JSONArray) result.get("holdings");

            for (int i = 0; i < holdings.size(); i++) {
                JSONObject holding = (JSONObject) holdings.get(i);
                StockDetails stock = new StockDetails();
                stock.setTicker(String.valueOf(holding.get("ticker")));
                stock.setNumberOfShares(Integer.parseInt(String.valueOf(holding.get("shares"))));
                stock.setBookValue(Integer.parseInt(String.valueOf(holding.get("book_cost")))/100.0);
                stock.setMarketValue(Integer.parseInt(String.valueOf(holding.get("market_value")))/100.0);


                String price = DataRetrieval.BASE_URL + "api/stock?ticker=" + stock.getTicker() + "&key=" + DataRetrieval.API_KEY;
                String priceData = executeRequest(price);
                JSONObject priceObj = (JSONObject) parser.parse(priceData);
                stock.setPrice(Double.parseDouble(String.valueOf(priceObj.get("price")))/100.0);
                stockDetails.add(stock);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stockDetails;
    }

    public static PortfolioOverview currerntmarketval() {
        String url = DataRetrieval.BASE_URL + "api/account?key=" + DataRetrieval.API_KEY;
        String data = executeRequest(url);

        PortfolioOverview overview = new PortfolioOverview();


        JSONParser parser = new JSONParser();
        try {
            JSONObject result = (JSONObject) parser.parse(data);
            JSONArray holdings = (JSONArray) result.get("holdings");
            int x = 0;
            x+= Integer.parseInt(String.valueOf(result.get("cash")));
            overview.setCash(Integer.parseInt(String.valueOf(result.get("cash")))/100.0);
            for (int i = 0; i < holdings.size(); i++) {
                x += Integer.parseInt(String.valueOf(((JSONObject) holdings.get(i)).get("market_value")));
            }
            overview.setEquity(x/100.0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return overview;

    }

    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();

    public static String executeRequest(String url) {
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = HTTP_CLIENT.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        String data = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuffer result = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            data = result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(data);

        return data;
    }

}
