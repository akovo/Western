package com.starter.controllers;

import com.google.common.collect.ImmutableMap;
import com.starter.models.entities.Stock;
import com.starter.repositories.StockRepository;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final ImmutableMap<String, String> HEALTHY = ImmutableMap.of("response", "healthy");

    private static final String API_KEY = "17_cTdFVNIoNNrMxpfHCWg";

    private static final String BASE_URL = "http://oec-2018.herokuapp.com/";

    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();

    private final StockRepository stockRepository;

    @Autowired
    public HealthController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    @RequestMapping(method = RequestMethod.GET)
    public ImmutableMap getHealth() {
        saveStocks(getStockList());
        return HEALTHY;
    }


    public void saveStocks(List<String> stocks) {
        for (String st : stocks) {
            val stock = new Stock();
            stock.setTicker(st);
            stock.setHalfDump(false);
            stock.setFullDump(false);
            stockRepository.save(stock);
        }
    }

    public List<String> getStockList() {
        String url = BASE_URL + "/api/stock/list?key=" + API_KEY;
        String data = executeRequest(url);

        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(data);
            JSONArray tickers = (JSONArray) obj.get("stock_tickers");
            List<String> stocks = new ArrayList<>();
            for (int i = 0; i < tickers.size(); i++) {
                stocks.add(String.valueOf(tickers.get(i)));
            }
            return stocks;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String executeRequest(String url) {
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

        return data;
    }
}
