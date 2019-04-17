package cs4330.cs.utep.edu;

import android.os.Parcel;
import android.os.Parcelable;

public class PriceFinder
{
    private double newPrice = 0;
    String itemName;
    double initPrice;

    public PriceFinder()//basic constructor
    {
    }
    public PriceFinder(String url)//Constructor will parse a url and find values
    {
     initPrice = (Math.random()+ 1) * 100;
     itemName = url;
    }
    public Double getInitPrice()
    {
        return initPrice;
    }
    public String getItemName()
    {
        return itemName;
    }

    public double findPrice()
    {
        newPrice = Math.random() * 100;
        return newPrice;
    }
    public double percentChange(double price)

    {
        return ((price - initPrice)/initPrice) *100 ;
    }

}
