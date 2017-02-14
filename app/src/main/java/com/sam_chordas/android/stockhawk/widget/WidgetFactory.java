package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.SingleStockData;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.StockGraphActivity;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor cursor;
    private Context context;

    public WidgetFactory(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex(QuoteColumns._ID));
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        if (cursor!= null  && cursor.moveToPosition(position)) {
            view.setTextViewText(R.id.stock_symbol,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
            view.setTextViewText(R.id.bid_price,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            view.setTextViewText(R.id.change,
                    cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
            Intent intent = new Intent();
            SingleStockData stockData = getStockData(position);
            intent.putExtra(Utils.JSON_symbol,stockData.getSymbol());
            intent.putExtra(Utils.JSON_bid,stockData.getBid_price());
            intent.putExtra(Utils.JSON_ChangeInPercent,stockData.getPercent_change());
            intent.putExtra(Utils.JSON_Open, stockData.getOpen());
            intent.putExtra(Utils.JSON_Low,stockData.getLow());
            intent.putExtra(Utils.JSON_High,stockData.getHigh());
            intent.putExtra(Utils.JSON_Mkt_Cap,stockData.getMkt_cap());
            intent.putExtra(Utils.JSON_PE_Ratio,stockData.getPe_ratio());
            intent.putExtra(Utils.JSON_Div_yield,stockData.getDiv_yield());
            view.setOnClickFillInIntent(R.id.widget_item, intent);
        }
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private SingleStockData getStockData(int position){
        Cursor c = cursor;
        c.moveToPosition(position);
        SingleStockData stockData = new SingleStockData();
        stockData.setSymbol(c.getString(c.getColumnIndex(QuoteColumns.SYMBOL)));
        stockData.setBid_price(c.getString(c.getColumnIndex(QuoteColumns.BIDPRICE)));
        stockData.setPercent_change(c.getString(c.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
        stockData.setOpen(c.getString(c.getColumnIndex(QuoteColumns.OPEN)));
        stockData.setLow(c.getString(c.getColumnIndex(QuoteColumns.LOW)));
        stockData.setHigh(c.getString(c.getColumnIndex(QuoteColumns.HIGH)));
        stockData.setMkt_cap(c.getString(c.getColumnIndex(QuoteColumns.MKT_CAP)));
        stockData.setPe_ratio(c.getString(c.getColumnIndex(QuoteColumns.PE_RATIO)));
        stockData.setDiv_yield(c.getString(c.getColumnIndex(QuoteColumns.DIV_YIELD)));
        return stockData;
    }

}