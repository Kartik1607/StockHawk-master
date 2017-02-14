package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.VolleyInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by Kartik Sharma on 19/11/16.
 */
public class StockGraphActivity extends AppCompatActivity {

    class StockGraphData{
        String date;
        float value;

        public StockGraphData(String date, float value) {
            this.date = date;
            this.value = value;
        }
    }

    private final String string_five_days = "5d",
                   string_one_month = "1m",
                   string_six_months = "6m",
                   string_one_year = "1y";

    static String currentSelected = "5d";
    ArrayList<StockGraphData> five_days, one_month, six_month, one_year;
    LineChartView lineChartView;
    String symbol, current, percent, open, low, high, mkt_cap, pe_ratio, div_Yield;
    TabLayout tabLayout;
    int color;
    AppBarLayout appBarLayout;
    NestedScrollView scrollView;
    Toolbar toolbar;
    TextView tv_symbol, tv_current_percent;
    TextView tv_open, tv_low, tv_high, tv_mkt_cap, tv_pe_ratio, tv_div_yield;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        context = this;
        tv_symbol = (TextView) findViewById(R.id.textView_Symbol);
        tv_current_percent = (TextView) findViewById(R.id.textView_quote);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        tv_open = (TextView) findViewById(R.id.textView_open);
        tv_low = (TextView) findViewById(R.id.textView_low);
        tv_high = (TextView) findViewById(R.id.textView_high);
        tv_mkt_cap = (TextView) findViewById(R.id.textView_mkt_cap);
        tv_pe_ratio = (TextView) findViewById(R.id.textView_pe_ratio);
        tv_div_yield = (TextView) findViewById(R.id.textView_div_yield);

        symbol = getIntent().getStringExtra(Utils.JSON_symbol);
        current = getIntent().getStringExtra(Utils.JSON_bid);
        percent = getIntent().getStringExtra(Utils.JSON_ChangeInPercent);
        open = getIntent().getStringExtra(Utils.JSON_Open);
        low = getIntent().getStringExtra(Utils.JSON_Low);
        high = getIntent().getStringExtra(Utils.JSON_High);
        mkt_cap = getIntent().getStringExtra(Utils.JSON_Mkt_Cap);
        pe_ratio = getIntent().getStringExtra(Utils.JSON_PE_Ratio);
        div_Yield = getIntent().getStringExtra(Utils.JSON_Div_yield);

        tv_symbol.setText(symbol);
        tv_current_percent.setText(current + "( " + percent + " )");
        tv_open.setText(open);
        tv_low.setText(low);
        tv_high.setText(high);
        tv_mkt_cap.setText(mkt_cap);
        tv_pe_ratio.setText(pe_ratio);
        tv_div_yield.setText(div_Yield);

        if(percent.charAt(0) == '-'){
            color = ContextCompat.getColor(this,R.color.material_red_700);
        }else{
            color = ContextCompat.getColor(this,R.color.material_green_700);
        }
        appBarLayout.setBackgroundColor(color);
        lineChartView = (LineChartView) findViewById(R.id.lineChart);
        lineChartView.setYAxis(false);
        lineChartView.setXAxis(false);
        lineChartView.setYLabels(AxisController.LabelPosition.OUTSIDE);
        lineChartView.setXLabels(AxisController.LabelPosition.NONE);
        lineChartView.setAxisColor(color);
        lineChartView.setLabelsColor(color);
        lineChartView.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                ArrayList<StockGraphData> data = null;
                switch(currentSelected){
                    case string_five_days : data = five_days; break;
                    case string_one_month : data = one_month; break;
                    case string_six_months : data = six_month; break;
                    case string_one_year : data = one_year;  break;
                }

                Toast.makeText(context, data.get(entryIndex).date + " : " + data.get(entryIndex).value, Toast.LENGTH_LONG).show();
            }
        });

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        lineChartView.dismiss();
                        switch (tab.getPosition()) {
                            case 0:
                                loadDetails(symbol, "5d");
                                currentSelected = "5d";
                                break;
                            case 1:
                                loadDetails(symbol, string_one_month);
                                currentSelected = string_one_month;
                                break;
                            case 2:
                                loadDetails(symbol, string_six_months);
                                currentSelected = string_six_months;
                                break;
                            case 3:
                                loadDetails(symbol, string_one_year);
                                currentSelected = string_one_year;
                                break;
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TabLayout.Tab tab = null;
        switch(currentSelected){
            case string_five_days:
                tab = tabLayout.getTabAt(0);
                break;
            case string_one_month:
                tab = tabLayout.getTabAt(1);
                break;
            case string_six_months:
                tab = tabLayout.getTabAt(2);
                break;
            case string_one_year:
                tab = tabLayout.getTabAt(3);
                break;
        }
        tab.select();
        loadDetails(symbol,currentSelected);
    }


    private void  fetchData(String url, final ArrayList<StockGraphData> data) throws IOException {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            loadGraph(response, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyInstance.getInstance(this).getRequestQueue().add(request);

    }

    private void loadGraph(String json, ArrayList<StockGraphData> data) throws JSONException {
        JSONObject object = (new JSONObject(json)).getJSONObject(Utils.JSON_query);
        JSONObject results = object.getJSONObject(Utils.JSON_results);
        JSONArray quotes = results.getJSONArray(Utils.JSON_quote);
        LineSet dataset = new LineSet();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for(int i = 0; i < quotes.length(); ++i){
            JSONObject current = quotes.getJSONObject(i);
            double value = current.getDouble(Utils.JSON_Adj_close);
            if(value < min)
                min = value;
            if(value > max)
                max = value;
            String date = current.getString(Utils.JSON_Date);
            dataset.addPoint(
                    date,
                    (float) value
            );
            data.add(new StockGraphData(date,(float) value));
        }
        min = min < 0 ? 0 : min;
        dataset.setColor(color);
        lineChartView.setAxisBorderValues((int)Math.floor(min),(int)Math.ceil(max));
        lineChartView.addData(dataset);
        lineChartView.show();
        int step = lineChartView.getHeight() / 5;
        lineChartView.setStep(step);
    }

    private void loadGraph(ArrayList<StockGraphData> data){
        LineSet dataset = new LineSet();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for(int i = 0; i < data.size(); ++i){
            double value = data.get(i).value;
            if(value < min)
                min = value;
            if(value > max)
                max = value;
            dataset.addPoint(
                    data.get(i).date,
                    (float) value
            );
        }
        min = min < 0 ? 0 : min;
        dataset.setColor(color);
        lineChartView.setAxisBorderValues((int)Math.floor(min),(int)Math.ceil(max));
        lineChartView.addData(dataset);
        lineChartView.show();
        int step = lineChartView.getHeight() / 5;
        lineChartView.setStep(step);
    }

    private void loadDetails(String symbol, String duration) {

        ArrayList<StockGraphData> data = null;

        switch (duration) {
            case string_five_days:
                five_days = new ArrayList<>();
                data = five_days;
                break;
            case string_one_month:
                one_month = new ArrayList<>();
                data = one_month;
                break;
            case string_six_months:
                six_month = new ArrayList<>();
                data = six_month;
                break;
            case string_one_year:
                one_year = new ArrayList<>();
                data = one_year;
                break;
        }

        if(data.size() != 0){
            loadGraph(data);
            return;
        }


        StringBuilder urlStringBuilder = new StringBuilder();

        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = ", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("\"" + symbol + "\"", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode(" and startDate = \"", "UTF-8"));
            urlStringBuilder.append(getDate(duration));
            urlStringBuilder.append(URLEncoder.encode("\" and endDate = \"", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode(getDate(null) + "\"", "UTF-8"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");


            String urlString;

            urlString = urlStringBuilder.toString();

            try {
                fetchData(urlString,data);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String getDate(String duration) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = GregorianCalendar.getInstance();
        if (duration == null) {
            return df.format(cal.getTime());
        }
        switch (duration) {
            case string_five_days:
                cal.add(GregorianCalendar.DATE, -5);
                break;
            case string_one_month:
                cal.add(GregorianCalendar.MONTH, -1);
                break;
            case string_six_months:
                cal.add(GregorianCalendar.MONTH, -6);
                break;
            case string_one_year:
                cal.add(GregorianCalendar.YEAR, -1);
                break;
        }
        return df.format(cal.getTime());
    }

}
