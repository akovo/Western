package com.starter.tasks;

import com.starter.DataRetrieval;
import com.starter.models.entities.Stock;
import com.starter.repositories.StockRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.starter.DataRetrieval.executeRequest;

@Component
public class StopLoss {

    private static final JSONParser PARSER = new JSONParser();

    private final StockRepository stockRepository;

    private static double HALF_DROP = -0.2;
    private static double FULL_DROP = -0.25;

    private int count = 0;

    @Autowired
    public StopLoss(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void stopLossMonitoring() {
        if (count % 5 == 0) {
            Set<String> stocks = getStockSet();
            int portfolio = currerntmarketval();

            try {
                String data = executeRequest(DataRetrieval.BASE_URL + "api/account?key=" + DataRetrieval.API_KEY);
                JSONObject obj = (JSONObject) PARSER.parse(data);
                JSONArray holdings = (JSONArray) obj.get("holdings");
                int size = 0;
                for (int i = 0; i < holdings.size(); i++) {
                    JSONObject holding = (JSONObject) holdings.get(i);
                    int marketValue = Integer.parseInt(String.valueOf(holding.get("market_value")));
                    int bookValue = Integer.parseInt(String.valueOf(holding.get("book_cost")));

                    if (bookValue == 0 || marketValue == 0) {
                        continue;
                    }
                    size++;
                }

                double stockValue = portfolio*1.0/size;

                Map<String, Integer> toBuy = new HashMap<>();

                for (int i = 0; i < holdings.size(); i++) {
                    JSONObject holding = (JSONObject) holdings.get(i);
                    int marketValue = Integer.parseInt(String.valueOf(holding.get("market_value")));
                    int bookValue = Integer.parseInt(String.valueOf(holding.get("book_cost")));

                    if (bookValue == 0 || marketValue == 0) {
                        continue;
                    }

                    double wantedValue = stockValue - marketValue;

                    String ticker = String.valueOf(holding.get("ticker"));

                    String url = DataRetrieval.BASE_URL + "api/stock?ticker=" + ticker + "&key=" + DataRetrieval.API_KEY;
                    String priceData = executeRequest(url);
                    JSONObject jsonPriceData = (JSONObject) PARSER.parse(priceData);
                    double price = Integer.parseInt(String.valueOf(jsonPriceData.get("price")))*1.0;

                    double numberOfShares = wantedValue/price;
                    int absNum = (int) Math.floor(Math.abs(numberOfShares));
                    if (absNum == 0) {
                        continue;
                    }
                    if (numberOfShares < 0) {
                        sellStock(ticker, absNum);
                    } else {
                        toBuy.put(ticker, absNum);
                    }

                }

                    for (String stock : toBuy.keySet()) {
                    buyStock(stock, toBuy.get(stock));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else {
            String data = executeRequest(DataRetrieval.BASE_URL + "api/account?key=" + DataRetrieval.API_KEY);

            try {
                JSONObject obj = (JSONObject) PARSER.parse(data);
                JSONArray holdings = (JSONArray) obj.get("holdings");
                for (int i = 0; i < holdings.size(); i++) {
                    JSONObject holding = (JSONObject) holdings.get(i);
                    int marketValue = Integer.parseInt(String.valueOf(holding.get("market_value")));
                    int bookValue = Integer.parseInt(String.valueOf(holding.get("book_cost")));

                    if (bookValue == 0 || marketValue == 0) {
                        continue;
                    }

                    double returns = (marketValue*1.0 - bookValue)/bookValue;
                    if (returns <= FULL_DROP) {
                        checkFullDump(String.valueOf(holding.get("ticker")), Integer.parseInt(String.valueOf(holding.get("shares"))));
                    } else if (returns <= HALF_DROP) {
                        checkHalfDump(String.valueOf(holding.get("ticker")), Integer.parseInt(String.valueOf(holding.get("shares")))/2);
                    } else if (returns >= 1.6) {

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        count++;


    }

    @Transactional
    public void checkHalfDump(String ticker, int numShares) {
        Stock stock = stockRepository.findByTicker(ticker);
        if (!stock.getHalfDump()) {
            String url = DataRetrieval.BASE_URL + "api/sell?ticker=" + stock.getTicker() + "&shares=" + numShares + "&key=" + DataRetrieval.API_KEY;
            executeRequest(url);
            stock.setHalfDump(true);
            stockRepository.save(stock);
        }
    }

    public static Set<String> getStockSet() {
        String url = DataRetrieval.BASE_URL + "api/stock/list?key=" + DataRetrieval.API_KEY;
        String data = executeRequest(url);

        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(data);
            JSONArray tickers = (JSONArray) obj.get("stock_tickers");
            Set<String> stocks = new HashSet<>();
            for (int i = 0; i < tickers.size(); i++) {
                stocks.add(String.valueOf(tickers.get(i)));
            }
            return stocks;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public void checkFullDump(String ticker, int numShares) {
        Stock stock = stockRepository.findByTicker(ticker);
        if (!stock.getFullDump()) {
            String url = DataRetrieval.BASE_URL + "api/sell?ticker=" + stock.getTicker() + "&shares=" + numShares + "&key=" + DataRetrieval.API_KEY;
            executeRequest(url);
            stock.setFullDump(true);
            stockRepository.save(stock);
        }
    }

    public static int currerntmarketval() {
        String url = DataRetrieval.BASE_URL + "api/account?key=" + DataRetrieval.API_KEY;
        String data = executeRequest(url);

        JSONParser parser = new JSONParser();
        try {
            JSONObject result = (JSONObject) parser.parse(data);
            JSONArray holdings = (JSONArray) result.get("holdings");
            int x = 0;
            x+= Integer.parseInt(String.valueOf(result.get("cash")));
            for (int i = 0; i < holdings.size(); i++) {
                x += Integer.parseInt(String.valueOf(((JSONObject) holdings.get(i)).get("market_value")));
            }
            return x;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public void sellStock(String ticker, int numberOfShares) {
        String url = DataRetrieval.BASE_URL + "api/sell?ticker=" + ticker + "&shares=" + numberOfShares + "&key=" + DataRetrieval.API_KEY;
        System.out.println("Sell " + numberOfShares + " of " + ticker);
        String data = executeRequest(url);
        System.out.println(data);
    }

    public void buyStock(String ticker, int numberOfShares) {
        String url = DataRetrieval.BASE_URL + "api/buy?ticker=" + ticker + "&shares=" + numberOfShares + "&key=" + DataRetrieval.API_KEY;
        System.out.println("Buy " + numberOfShares + " of " + ticker);
        String data = executeRequest(url);
        System.out.println(data);
    }



}
