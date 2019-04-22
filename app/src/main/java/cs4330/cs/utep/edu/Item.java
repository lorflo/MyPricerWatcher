package cs4330.cs.utep.edu;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the item class. it will
 */
public class Item implements Parcelable
{
    private  int id = 0;
    private String name ="";
    private double initPrice = 0;
    private double newPrice = 0;
    private double percentChange = 0;
    private String url;
    private PriceFinder finder = new PriceFinder();

    public Item()//basic constructor
    {
        new Item("nothing entered");
    }
    public Item(String url)//constructor to generate a new item based on the url
    {
        finder = new PriceFinder(url);
        initPrice = finder.getInitPrice();
        newPrice = initPrice;
        this.url = url;
    }
    //getters and setters for item values
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public Double getPrice()
    {
        return initPrice;
    }
    public void setPrice(Double price)
    {
        initPrice = price;
    }

    public String getName() { return name; }
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

    public Double getPercent()
    {
        return percentChange;
    }
    public void setPercent(Double percentChange){this.percentChange = percentChange;}

    public Double getNewPrice() { return newPrice; }
    public void setNewPrice(Double newPrice){this.newPrice = newPrice;}

    public void updatePrice()//gets updated price and percentage change from PriceFinder class
    {
        PriceFinder newFinder = new PriceFinder(getUrl());
        newPrice = newFinder.findPrice();
        percentChange = newFinder.percentChange(newPrice);
    }

    //==============================================parsable methods to pass item through intent===================================
    public Item(Parcel in)
    {
        readFromParcel(in);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(initPrice);
        dest.writeDouble(newPrice);
        dest.writeDouble(percentChange);
        dest.writeString(url);
    }
    private void readFromParcel(Parcel in)
    {
        //finder = in.readParcelable(PriceFinder.class.getClassLoader());


        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        id = in.readInt();
        name = in.readString();
        initPrice = in.readDouble();
        newPrice = in.readDouble();
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
