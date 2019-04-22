package cs4330.cs.utep.edu;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.ArrayList;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
    private Item item = new Item();
    private int selectedItem,itemId;
    private String itemName,label;
    private  ArrayList<Item> itemStorage = new ArrayList<>();
    private  ArrayList<String> list;
    private static  ArrayAdapter<String> adapter;
    private MyDBHandler db;
    private String action = "";
    private String type = "";
    private Intent shared;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        shared = getIntent();
        if(Intent.ACTION_SEND.equals(shared.getAction()))//if url has been shared
        {
            addItemDialog();
        }

            NetworkManager wifi = new NetworkManager();
        if(!wifi.isWifiAvailable(this))
            askForWifi();

        db = new MyDBHandler(this);
        getAllData(itemStorage);
        if(itemStorage != null)
            list = makeList(itemStorage);

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        setListAdapter(adapter);
        Button add = new Button(this);
        add.setText("add item");
        getListView().addFooterView(add);
        add.setOnClickListener(view -> addItemDialog());
        getListView().setOnCreateContextMenuListener(this);
    }

     // passes item to second activity
    public void onListItemClick(ListView parent, View v, int position, long id)
    {
       item = itemStorage.get(position);
       selectedItem = position;
        Intent i = new Intent(this, ItemContent.class);
        i.putExtra("cs4330.cs.utep.edu",  item);
        startActivityForResult(i,1);
        toast("Item Clicked");
    }
    //gets new item values from update
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle b = data.getExtras();
                item = b.getParcelable("result");

                updateData(item.getId(),item.getName(),item.getPrice(),
                        item.getNewPrice(),item.getPercent(),item.getUrl());//Attempts to update the database

                label = item.getName() + " $" +String.format("%.2f",item.getNewPrice());
                itemStorage.remove(selectedItem);//remove current item from storage
                itemStorage.add(selectedItem,item);//Stores the item and its label.
                list.remove(selectedItem);//remove current item from list
                list.add(selectedItem,label);//Updates the list of items.
                adapter.notifyDataSetChanged();//updates the apps display list
            }
        }
    }
    //creates a context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;//information on the context menu created
        selectedItem = info.position;
        itemName = list.get(info.position);
        menu.setHeaderTitle(itemName);
        createMenu(menu);
    }
    public boolean onContextItemSelected(MenuItem item)
    {
        menuChoice(item);
        return true;
    }

    //creates the menu items
    private void createMenu(Menu menu)
    {
        //creates the menu items
        MenuItem mnu1 = menu.add(0, 0, 0, "Edit");
        MenuItem mnu2 = menu.add(0, 1, 0, "Delete");
    }
    //Determines which option was was selected
    private boolean menuChoice(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 0:
                editDialog();//Edit option was selected
                return true;
            case 1:
                deleteDialog();//Delete option was selected
                return true;
        }
        return false;//nothing was selected
    }
    //displays a dialog to the user asking if they really want to delete the item
    public void deleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());//Creates a builder to  build the dialog
        builder.setMessage("Do you want to delete this item?");//sets the message to be displayed
        //creates button to save the changes made
        builder.setPositiveButton("yes", (dialog, id) ->
                {
                    item = itemStorage.get(selectedItem);
                    deleteData(item.getId());
                    itemStorage.remove(selectedItem);//remove current item from storage
                    list.remove(selectedItem);//remove current item from list
                    adapter.notifyDataSetChanged();//updates the apps display list

                });
        builder.setNegativeButton("cancel", (dialog,id) -> dialog.cancel());//the cancel button
        builder.create();//creates the dialog
        builder.show();//displays the dialog
    }
    //Displays a dialog to the user where they can edit an items name and url
    public void editDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getListView().getContext()); //Gets a layout to use in the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());//Creates a builder to  build the dialog
        View customView = inflater.inflate(R.layout.add_item, null);//puts the layout in a view
        builder.setTitle("Edit Item"); //sets dialog title
        builder.setView(customView);//sets the view to be seen by user
        EditText name = customView.findViewById(R.id.newIN);//Gets the input area for the name
        EditText url = customView.findViewById(R.id.newURL);//Gets the input area for the url
        TextView currentName = customView.findViewById(R.id.newIN);//to show the current name
        TextView currentUrl = customView.findViewById(R.id.newURL);//to show the current url
       item = itemStorage.get(selectedItem);//Gets the item that was selected
        currentName.setText(item.getName());//Displays the current name
        currentUrl.setText(item.getUrl());//Displays the current url

        //creates button to save the changes made
        builder.setPositiveButton("save", (dialog, id) ->
        {

            String newName = name.getText().toString();//Gets the user input for the name
            String newURL = url.getText().toString();//Gets the user input for the url
            Item oldItem = itemStorage.get(selectedItem);//current item
             //checks to see if a new name was entered, if not sets the name to its current one
            if(newName.equals(""))
            {
                item.setName(oldItem.getName());//sets current name
                item.setId(oldItem.getId());
            }
            else
            {
                item.setName(newName);//sets new name
                item.setId(oldItem.getId());
            }
            //checks to see if a new url was entered, if not sets the url to its current one
            if(newURL.equals(""))
            {
                item.setUrl(oldItem.getUrl());//sets current url
                item.setId(oldItem.getId());
            }
            else
            {
                item.setUrl(newURL);//sets new url
                item.setId(oldItem.getId());
            }

            updateData(item.getId(),item.getName(),item.getPrice(),item.getNewPrice(),item.getPercent(),item.getUrl());//Attempts to update the database
            //Label to display the item to user
            label = item.getName() + " $" +String.format("%.2f",item.getPrice());

            itemStorage.remove(selectedItem);//remove current item from storage
            itemStorage.add(selectedItem,item);//Stores the item and its label.
            list.remove(selectedItem);//remove current item from list
            list.add(selectedItem,label);//Updates the list of items.
            adapter.notifyDataSetChanged();//updates the apps display list

        });
        builder.setNegativeButton("cancel", (dialog,id) -> dialog.cancel());//the cancel button
        builder.create();//creates the dialog
        builder.show();//displays the dialog
    }
    public void addItemDialog()
    {
        action = shared.getAction();
        type = shared.getType();
        LayoutInflater inflater = LayoutInflater.from(getListView().getContext());//Gets a layout to use in the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());//Creates a builder to  build the dialog
        View customView = inflater.inflate(R.layout.add_item, null);//puts the layout in a view
        builder.setTitle("New Item"); //sets dialog title
        builder.setView(customView);//sets the view to be seen by user
        EditText name = customView.findViewById(R.id.newIN);//Gets the input area for the name
        EditText url = customView.findViewById(R.id.newURL);//Gets the input area for the url
        TextView urlText = customView.findViewById(R.id.newURL);//text set for the url
        if(Intent.ACTION_SEND.equals(action)&& type != null) {
            if ("text/plain".equals(type)) {
                String sharedUrl = shared.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedUrl != null)
                    urlText.setText(sharedUrl);
            }
        }

        //creates button to save the changes made
        builder.setPositiveButton("save", (dialog, id) ->
        {
            itemId = list.size() + 1;//creates a new id for each item added
            String newURL = url.getText().toString();//Gets user input for url.
            String newName = name.getText().toString();//Gets user input for name.

            Item newI = new Item(newURL);//Creates a  new item.

            if(!newName.equals(" "))   //If the item name has been entered
                newI.setName(newName); //set the name of the new item.

            newI.setId(itemId);//sets the items id

            //Label to display the item to user
            label = newI.getName() + " $" +String.format("%.2f",newI.getPrice());
            itemStorage.add(newI); //Stores the item
            list.add(label);//Updates the list of items.

            addData(newI.getId(),newI.getName(),newI.getPrice(),newI.getNewPrice(),newI.getPercent(),newI.getUrl());//Attempts to add data to the database

            adapter.notifyDataSetChanged();//updates the apps displayed list
            shared.setAction("");
        });
        builder.setNegativeButton("cancel", (dialog,id) ->  { dialog.cancel(); shared.setAction("");});//the cancel button
        builder.create();//creates the dialog
        builder.show();//displays the dialog to te user
    }
    public ArrayList<String> makeList(ArrayList<Item> itemStorage)
    {
        ArrayList<String> itemsList = new ArrayList<>();
        for(Item i: itemStorage)//go through each item
        {
            //check if price has been updated and sets label to new price
            if(i.getNewPrice() != null) {
                itemsList.add(i.getName() + " $"
                        + String.format("%.2f", i.getNewPrice()));
            }
            else//price hasn't been updated
            {
                itemsList.add(i.getName() + " $"
                        + String.format("%.2f", i.getPrice()));
            }
        }
        return itemsList;
    }
    public void askForWifi()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());//Creates a builder to  build the dialog
        builder.setTitle("Wifi"); //sets dialog title
        builder.setMessage("Wifi is disabled, click \"settings\" to enable it now"); //sets dialog title
        builder.setPositiveButton("settings", (dialog, id) ->
        {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });
        builder.setNegativeButton("cancel", (dialog,id) -> dialog.cancel());//the cancel button
        builder.create();//creates the dialog
        builder.show();//displays the dialog to te user

    }
    //================================================= data base methods =====================================================================
    public void addData(int id, String name, Double iPrice,Double nPrice,Double percent, String url)
    {
        //Attempts to add the items data to a table in a database, and displays a message if successful or not.
        boolean insert = db.addItemData(id,name,iPrice,nPrice,percent,url);
        if (insert)
            toast("data saved");
        else
            toast("data not saved");
    }
    public void getAllData(ArrayList<Item> itemArrayList)
    {
        Cursor cursor = db.getAllItemData();

        if (cursor.getCount() == 0)
        {
            toast("No data found");
        }
        else
        {
            while (cursor.moveToNext())//sets all items values
            {
                Item restore = new Item();
                restore.setId(cursor.getInt(0));
                restore.setName(cursor.getString(1));
                restore.setPrice(cursor.getDouble(2));
                restore.setNewPrice(cursor.getDouble(3));
                restore.setPercent(cursor.getDouble(4));
                restore.setUrl(cursor.getString(5));
                itemArrayList.add(restore);
            }
        }
    }
    public void updateData(int id, String name, Double iPrice,Double nPrice,Double percent, String url)
    {
        boolean updated = db.updateData(id,name,iPrice,nPrice,percent,url);
        if(updated)
            toast("item updated");
        else
            toast("item not updated");
    }
    public void deleteData(int id)
    {
       int deletedRows = db.deleteData(id);
        if(deletedRows > 0)
            toast("item deleted");
        else
            toast("item not deleted");
    }


    private void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

}
