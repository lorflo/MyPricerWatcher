package cs4330.cs.utep.edu;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.widget.Toast;
import com.google.gson.Gson;

public class MainActivity extends ListActivity
{

    //private String url = "https://www.discountmugs.com/product/fd10-translucent-color-flying-discs/";
    private Item item = new Item();
    private int selectedItem;
    private String itemName;
    private static LinkedHashMap<String,Item> itemStorage = new <String,Item>LinkedHashMap();
   private static List<String> list;
    private static  ArrayAdapter<String> adapter;
    MyDBHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        list = new ArrayList<String>(itemStorage.keySet());
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        setListAdapter(adapter);
        Button add = new Button(this);
        add.setText("add item");
        getListView().addFooterView(add);
        add.setOnClickListener(view -> addItemDialog());
        getListView().setOnCreateContextMenuListener(this);

        db = new MyDBHandler(this);
    }


    public void onListItemClick(ListView parent, View v, int position, long id)
    {
       item = itemStorage.get(list.get(position));
        Intent i = new Intent(this, ItemContent.class);
        i.putExtra("cs4330.cs.utep.edu",  item);
        startActivityForResult(i,1);
        Toast.makeText(this, "Item Clicked", Toast.LENGTH_SHORT).show();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle b = data.getExtras();
                item = b.getParcelable("cs4330.cs.utep.edu");
            }
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        selectedItem = info.position;
        itemName = (String)list.get(info.position);
        menu.setHeaderTitle(itemName);
        createMenu(menu);
    }
    public boolean onContextItemSelected(MenuItem item)
    {
        menuChoice(item);
        return true;
    }


    private void createMenu(Menu menu)
    {
        MenuItem mnu1 = menu.add(0, 0, 0, "Edit");
        MenuItem mnu2 = menu.add(0, 1, 0, "Delete");
    }

    private boolean menuChoice(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 0:
                editDialog();
                return true;
            case 1:
                deleteDialog();
                return true;
        }
        return false;
    }
    public void deleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
        builder.setMessage("Do you want to delete this item?");
        builder.setPositiveButton("yes", (dialog, id) ->
                {
                    itemStorage.remove(list.get(selectedItem));
                    list.remove(selectedItem);
                    adapter.notifyDataSetChanged();

                });
        builder.setNegativeButton("no", (dialog,id) -> dialog.cancel());
        builder.create();
        builder.show();
    }
    public void editDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getListView().getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
        View customView = inflater.inflate(R.layout.add_item, null);
        builder.setTitle("Edit Item");
        builder.setMessage("NOTE: Clicking 'save' will move item to the bottom of the list");
        builder.setView(customView);
        EditText name = customView.findViewById(R.id.newIN);
        EditText url = customView.findViewById(R.id.newURL);
        builder.setPositiveButton("save", (dialog, id) ->
        {
            item = itemStorage.get(list.get(selectedItem));
            String newName = name.getText().toString();
            String newURL = url.getText().toString();
            String label;
            if(newName.equals(""))
            {
                Item oldItem = itemStorage.get(list.get(selectedItem));
                item.setName(oldItem.getName());
            }
            else
                item.setName(newName);
            if(newURL.equals(""))
            {
                Item oldItem = itemStorage.get(list.get(selectedItem));
                item.setUrl(oldItem.getUrl());
            }
            else
                item.setUrl(newURL);

            label = item.getName() + " $" +String.format("%.2f",item.getPrice());
            itemStorage.remove(list.get(selectedItem));
            itemStorage.put(label,item);
            list.remove(selectedItem);
            list.add(label);
            adapter.notifyDataSetChanged();

        });
        builder.setNegativeButton("cancel", (dialog,id) -> dialog.cancel());
        builder.create();
        builder.show();
    }
    public void addItemDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getListView().getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
        View customView = inflater.inflate(R.layout.add_item, null);
        builder.setTitle("New Item");
        builder.setView(customView);
        EditText name = customView.findViewById(R.id.newIN);
        EditText url = customView.findViewById(R.id.newURL);


        builder.setPositiveButton("save", (dialog, id) ->
        {
            String action = getIntent().getAction();
            String type = getIntent().getType();
            String newURL;
            String newName = name.getText().toString();

            if(Intent.ACTION_SEND.equalsIgnoreCase(action)
            && type != null && ("text/plain".equals(type)))
            {
            newURL = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            }
            else
                newURL = url.getText().toString();

            Item newI = new Item(newURL);
            if(!newName.equals(" "))
                newI.setName(newName);
            String label = newI.getName() + " $" +String.format("%.2f",newI.getPrice());
            itemStorage.put(label,newI);
            list.add(label);
            boolean insert = db.addItemData(item.getName(),item.getPrice(),item.getUrl());
            if(insert)
                Toast.makeText(this, "data saved", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "data not saved", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("cancel", (dialog,id) -> dialog.cancel());
        builder.create();
        builder.show();
    }
    public void onPause()
    {
        super.onPause();


    }

}
