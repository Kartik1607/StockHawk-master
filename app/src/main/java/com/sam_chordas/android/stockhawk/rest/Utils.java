package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;
  public static final String JSON_query = "query";
  public static final String JSON_count = "count";
  public static final String JSON_results = "results";
  public static final String JSON_quote = "quote";
  public static final String JSON_Name = "Name";
  public static final String JSON_Change = "Change";
  public static final String JSON_symbol = "symbol";
  public static final String JSON_bid = "Bid";
  public static final String JSON_Open = "Open";
  public static final String JSON_Low = "DaysLow";
  public static final String JSON_High = "DaysHigh";
  public static final String JSON_Mkt_Cap = "MarketCapitalization";
  public static final String JSON_PE_Ratio = "PERatio";
  public static final String JSON_Div_yield = "DividendYield";
  public static final String JSON_ChangeInPercent = "ChangeinPercent";
  public static final String JSON_Adj_close = "Adj_Close";
  public static final String JSON_Date = "Date";

  public static final String TAG = "tag";
  public static final String TAG_add = "add";
  public static final String TAG_init = "init";
  public static final String TAG_periodic = "periodic";


  public static ArrayList quoteJsonToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject(JSON_query);
        int count = Integer.parseInt(jsonObject.getString(JSON_count));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject(JSON_results)
              .getJSONObject(JSON_quote);
          ContentProviderOperation operation = buildBatchOperation(jsonObject);
          if(operation!=null)
              batchOperations.add(operation);
        } else{
          resultsArray = jsonObject.getJSONObject(JSON_results).getJSONArray(JSON_quote);

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              ContentProviderOperation operation = buildBatchOperation(jsonObject);
              if(operation!=null)
                batchOperations.add(operation);
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
    try {
      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    }catch (NumberFormatException e){
      Log.d("MY_APP","Bad Data");
    }
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String name = jsonObject.getString(JSON_Name);
      if(name.equals("null")){
        return null;
      }
      String change = jsonObject.getString(JSON_Change);
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString(JSON_symbol));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString(JSON_bid)));

      builder.withValue(QuoteColumns.OPEN, truncateBidPrice(jsonObject.getString(JSON_Open)));
      builder.withValue(QuoteColumns.LOW, truncateBidPrice(jsonObject.getString(JSON_Low)));
      builder.withValue(QuoteColumns.HIGH,truncateBidPrice(jsonObject.getString(JSON_High)));
      builder.withValue(QuoteColumns.MKT_CAP, jsonObject.getString(JSON_Mkt_Cap));
      builder.withValue(QuoteColumns.PE_RATIO, jsonObject.getString(JSON_PE_Ratio));
      builder.withValue(QuoteColumns.DIV_YIELD,jsonObject.getString(JSON_Div_yield));

      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString(JSON_ChangeInPercent), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }
}
