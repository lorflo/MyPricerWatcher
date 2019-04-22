package cs4330.cs.utep.edu;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.concurrent.ExecutionException;

public class PriceFinder extends AppCompatActivity
{
    private String siteText = "";
    double initPrice = 1.23,newPrice = 0.00;

    public PriceFinder()//basic constructor
    {
    }
    public PriceFinder(String url)//Constructor will set items initial price
    {
       setSiteText(url);//sets websites text info
       initPrice = findPrice();
    }
    public void setSiteText(String url)
    {
        Websites website = new Websites();
        website.execute(url);//starts background thread to connect to the url
        try {
            siteText = website.get();//gets the websites text info

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }}

    public Double getInitPrice()
    {
        return initPrice;
    }//gets initial price

    public Double findPrice()
    {
        StringBuilder pri = new StringBuilder();//to hold price as text
        for(int i =0; i < siteText.length();i++) {//iterate through site text
            if (siteText.charAt(i) == '$') {//look for dollar sign
                int count =0;
                for (int j = i; j < siteText.length(); j++) {//loop through site text after dollar sign
                    int n = j + 1;//skip dollar sign
                    StringBuilder p = new StringBuilder();//to hold potential price
                    while (siteText.charAt(n) != '.' && count < 15) {//checking for start of cents or if too large probably not the price
                        p.append(siteText.charAt(n));//saving dollar portion of price
                        n++;
                        count++;
                    }
                    if(count > 14)//dollar portion too large probably not price
                        break;// reset search
                    pri.append(p);//attatch potential values to final price dollar portion
                    pri.append(siteText.charAt(n));//attatch '.'
                    pri.append(siteText.charAt(n + 1));//attatch cents portion
                    pri.append(siteText.charAt(n + 2));//
                    newPrice = Double.valueOf(pri.toString());//set price from string
                    return newPrice;
                }
            }
        }
        return initPrice;//if no price was found return the default price
    }
    public Double percentChange(Double price)
    {
        return ((price - initPrice)/initPrice) *100 ;//percentage change
    }
//====================================================== webpage scraping ===============================================
    public class Websites extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings)
        {

            String words = "Malformed url";
            try
            {
                Document doc = Jsoup.connect(strings[0]).get();//conncet
                words = doc.text();//get website text
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return words;
        }

    }
    private void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }


}
