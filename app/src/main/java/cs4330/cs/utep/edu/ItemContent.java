package cs4330.cs.utep.edu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ItemContent extends AppCompatActivity
{
    private String tag = "lifecycle";
    private Item item = new Item();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Bundle b = getIntent().getExtras();
        item = b.getParcelable("cs4330.cs.utep.edu");
        TextView name = findViewById(R.id.name);
        TextView initPrice = findViewById(R.id.initPrice);
        TextView newPrice = findViewById(R.id.newPrice);
        TextView perChange = findViewById(R.id.perChange);
        TextView urlString = findViewById(R.id.url);
        Button updateButton = findViewById(R.id.updateButton);

        name.setText(item.getName());
        urlString.setText(item.getUrl());
        initPrice.setText("$" + String.format("%.2f",item.getPrice()));


        updateButton.setOnClickListener(view ->
        {
          // item.updatePrice();                                                    I dont know why app crashes with this method
            newPrice.setText("$" + String.format("%.2f",item.getNewPrice()));
            if(item.getPercent() < 0)
                perChange.setText("down %" + String.format("%.2f",Math.abs(item.getPercent())));
            else
                perChange.setText("up %" + String.format("%.2f",Math.abs(item.getPercent())));
        });
        urlString.setOnClickListener(view ->
        {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(item.getUrl()));
                startActivity(intent);
        });

    }
    private void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
    public void onStart()
    {
        super.onStart();
        Log.d(tag, "onStart was called");
    }
    public void onPause()
    {
        super.onPause();
        Log.d(tag, "onPause was called");
    }
    public void onResume()
    {
        super.onResume();
        Log.d(tag, "onPause was called");
    }
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(tag, "onDestroy was called");
    }
}
