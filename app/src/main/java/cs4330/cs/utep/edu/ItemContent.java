package cs4330.cs.utep.edu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemContent extends AppCompatActivity
{
    private String tag = "lifecycle";
    private Item item;
    private TextView perChange;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Bundle b = getIntent().getExtras();
        item = b.getParcelable("cs4330.cs.utep.edu");

        //find views by ids
        TextView name = findViewById(R.id.name);
        TextView initPrice = findViewById(R.id.initPrice);
        TextView newPrice = findViewById(R.id.newPrice);
        perChange = findViewById(R.id.perChange);
        TextView urlString = findViewById(R.id.url);
        Button updateButton = findViewById(R.id.updateButton);
        //set values in views
        name.setText(item.getName());
        initPrice.setText("$" + String.format("%.2f",item.getPrice()));
        newPrice.setText("$" + String.format("%.2f",item.getNewPrice()));
        percent();//tells if percent is up or down
        urlString.setText(item.getUrl());
        //actions performed when update button is clicked
        updateButton.setOnClickListener(view ->
        {
            toast("updating...");
            item.updatePrice();
            newPrice.setText("$" + String.format("%.2f",item.getNewPrice()));
            percent();//tells if percent is up or down
            //return new item values to main activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",item);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        });
        //opens web page when url is clicked
        urlString.setOnClickListener(view ->
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(item.getUrl()));
            startActivity(intent);
        });
    }
    //percent up or dowm
    public void percent()
    {
        if(item.getPercent() < 0)
            perChange.setText("down %" + String.format("%.2f",Math.abs(item.getPercent())));
        else
            perChange.setText("up %" + String.format("%.2f",Math.abs(item.getPercent())));
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
