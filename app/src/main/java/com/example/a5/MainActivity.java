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
    EditText Date;
    EditText Price;
    EditText Item;
    Button Add;
    Button Sub;
    TableLayout history;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new SQLDB(this);

        balance = (TextView) findViewById(R.id.balance);
        Date = (EditText) findViewById(R.id.Date);
        Price = (EditText) findViewById(R.id.Price);
        Item = (EditText) findViewById(R.id.Item);
        Add = (Button) findViewById(R.id.btnAdd);
        Sub = (Button) findViewById(R.id.btnSub);
        history = (TableLayout) findViewById(R.id.tableHistory);
        AddTransaction();
        GetHistory();
    }

    public void AddTransaction(){
        Add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = Double.parseDouble(Price.getText().toString());
                        Model model = new Model();
                        model.mDate =  Date.getText().toString();
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
                        model.mDate =  Date.getText().toString();
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
        ClearTable();
        Cursor result = myDb.getAllData();
        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;
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
        MainActivity.this.balance.setText("Current Balance: $" + df.format(balance));
    }

    public void ClearText(){
        MainActivity.this.Date.setText("");
        MainActivity.this.Price.setText("");
        MainActivity.this.Item.setText("");
    }

    public void ClearTable(){
        int count = history.getChildCount();
        for (int i = 1; i < count; i++) {
            history.removeViewAt(1);
        }
    }
}