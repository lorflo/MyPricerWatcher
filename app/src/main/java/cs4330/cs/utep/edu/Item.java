package cs4330.cs.utep.edu;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the item class. it will
 */
public class Item implements Parcelable
{
    String name;
    private double price = 0;
    private double percentChange = 0;
    private String url;
    PriceFinder finder;

    public Item()//basic constructor
    {
        new Item("no url given");
    }
    public Item(String url)//constructor to generate a new item based on the url
    {
        finder = new PriceFinder(url);
        name = finder.getItemName();
        price = finder.getInitPrice();
        this.url = "http://" + url;
    }
    public Item(Parcel in)
    {
        readFromParcel(in);
    }
    public Double getPrice()
    {
        return price;
    }
    public void setPrice(Double price)
    {
        this.price = price;
    }

    public String getName()//gets the item name from PriceFinder class
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }
    public double getPercent()//gets the price from PriceFinder class
    {
        return percentChange;
    }
    public Double getNewPrice()
    {
        return price;
    }
    public Double updatePrice()//gets updated price and percentage change from PriceFinder class
    {
        price = finder.findPrice();
        return price;
    }
    public Double percentChange()//gets updated price and percentage change from PriceFinder class
    {
        percentChange = finder.percentChange(price);
        return percentChange;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       // dest.writeParcelable(finder, flags);
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeDouble(percentChange);
        dest.writeString(url);
    }
    private void readFromParcel(Parcel in)
    {
        //finder = in.readParcelable(PriceFinder.class.getClassLoader());


        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        name = in.readString();
        price = in.readDouble();
        percentChange = in.readDouble();
        url = in.readString();
    }
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Item createFromParcel(Parcel in) {
                    return new Item(in);
                }

                public Item[] newArray(int size) {
                    return new Item[size];
                }
            };
}
