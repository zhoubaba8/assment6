package com.example.a5;

import androidx.appcompat.app.AppCompatActivity;


import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    SQLDB myDb;
    TextView balance;
    EditText Day;
    EditText Item,Price,Month,Years,DayFrom,MonthFrom,YearFrom,DayTo,MonthTo,YearTo,PriceFrom,PriceTo;
    Button Sub,Add,FL,CF;
    TableLayout history;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new SQLDB(this);


        balance = (TextView) findViewById(R.id.balance);
        Day = (EditText) findViewById(R.id.Day);
        Month = (EditText) findViewById(R.id.Month);
        Years = (EditText) findViewById(R.id.Years);
        Price = (EditText) findViewById(R.id.Price);
        Item = (EditText) findViewById(R.id.Item);
        Add = (Button) findViewById(R.id.Add);
        Sub = (Button) findViewById(R.id.Sub);
        FL = (Button) findViewById(R.id.FL);
        CF = (Button) findViewById(R.id.CF);
        DayFrom = (EditText) findViewById(R.id.DayFrom);
        MonthFrom = (EditText) findViewById(R.id.MonthFrom);
        YearFrom = (EditText) findViewById(R.id.YearFrom);
        DayTo = (EditText) findViewById(R.id.DayTo);
        MonthTo = (EditText) findViewById(R.id.MonthTo);
        YearTo = (EditText) findViewById(R.id.YearTo);
        PriceFrom = (EditText) findViewById(R.id.PriceFrom);
        PriceTo = (EditText) findViewById(R.id.PriceTo);
        history = (TableLayout) findViewById(R.id.tableHistory);
        GetHistory();
        AddTransaction();
        Filter();
        ClearFilter();
    }

    public void AddTransaction(){
        Add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = Double.parseDouble(Price.getText().toString());
                        Model model = new Model();
                        String day = Day.getText().toString();
                        String month = Month.getText().toString();
                        String year = Years.getText().toString();
                        model.mDate =  CreateDate(day, month, year);
                        model.mItem = Item.getText().toString();
                        model.mPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetHistory();
                        ClearText();


                    }
                }
        );

        Sub.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = -1 * Double.parseDouble(Price.getText().toString());
                        Model model = new Model();
                        String day = Day.getText().toString();
                        String month = Month.getText().toString();
                        String year = Years.getText().toString();
                        model.mDate =  CreateDate(day, month, year);
                        model.mItem = Item.getText().toString();
                        model.mPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetHistory();
                        ClearText();
                    }
                }
        );
    }

    public void GetHistory(){
        Cursor result = myDb.getAllData();
        DisplayHistory(result, false);
    }

    public void DisplayHistory(Cursor result, boolean filtered){
        if (result == null){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;
        ClearTable();
        while(result.moveToNext()){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams columnLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            columnLayout.weight = 1;

            TextView date = new TextView(this);
            date.setLayoutParams(columnLayout);
            date.setText(result.getString(2));
            tr.addView(date);

            TextView priceView = new TextView(this);
            priceView.setLayoutParams(columnLayout);
            priceView.setText(result.getString(3));
            tr.addView(priceView);

            TextView item = new TextView(this);
            item.setLayoutParams(columnLayout);
            item.setText(result.getString(1));
            tr.addView(item);

            history.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


            double price = Double.parseDouble(result.getString(3));
            balance += price;
        }
        if (!filtered){
            MainActivity.this.balance.setText("Current Balance: $" + df.format(balance));
        }
    }

    public void ClearText(){
        MainActivity.this.Day.setText("");
        MainActivity.this.Month.setText("");
        MainActivity.this.Years.setText("");
        MainActivity.this.Price.setText("");
        MainActivity.this.Item.setText("");
        MainActivity.this.DayFrom.setText("");
        MainActivity.this.MonthFrom.setText("");
        MainActivity.this.YearFrom.setText("");
        MainActivity.this.DayTo.setText("");
        MainActivity.this.MonthTo.setText("");
        MainActivity.this.YearTo.setText("");
        MainActivity.this.PriceFrom.setText("");
        MainActivity.this.PriceTo.setText("");
    }

    public void ClearTable(){
        int count = history.getChildCount();
        for (int i = 1; i < count; i++) {
            history.removeViewAt(1);
        }
    }

    public void Filter(){
        FL.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String priceFromString = PriceFrom.getText().toString();
                        String priceToString = PriceTo.getText().toString();
                        String day = DayFrom.getText().toString();
                        String month = MonthFrom.getText().toString();
                        String year = YearFrom.getText().toString();
                        String dateFrom = CreateDate(day, month, year);
                        day = DayTo.getText().toString();
                        month = MonthTo.getText().toString();
                        year = YearTo.getText().toString();
                        String dateTo = CreateDate(day, month, year);


                        Cursor result = myDb.getFilteredData(priceFromString, priceToString, dateFrom, dateTo);
                        DisplayHistory(result, true);
                    }
                }
        );
    }
    public String CreateDate(String day, String month, String year){
        if (month.isEmpty() || day.isEmpty() || year.isEmpty()) {
            return "";
        }
        else {
            int dayIn = Integer.parseInt(day);
            int monthIn = Integer.parseInt(month);
            if (dayIn < 10 && monthIn >= 10) {
                return year + "-" + month + "-0" + day;
            }
            else if (dayIn >= 10 && monthIn < 10){
                return year + "-0" + month + "-" + day;
            }

            else {
                return year + "-" + month + "-" + day;
            }
        }
    }
    public void ClearFilter(){
        CF.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClearText();
                        GetHistory();
                    }
                }
        );
    }


}