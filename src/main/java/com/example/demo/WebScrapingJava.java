/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webscrapingjava;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.json.*;
/**
 *
 * @author: Alla Ryan
 */
public class WebScrapingJava {

    public static void main(String[] args) throws IOException, JSONException {
        
        String search_url = "https://api.yelp.com/v3/businesses/search?term=";
 

        String accessToken=null;
        /* This api key was generated on my machine, it may need to be regenerated if the run fails */
        accessToken = "ZgVZtFbDzup5KFml-GRGxnuKLtQBTDH5Qb4hHsul4MnrkeNXka_5fk_fvT9-emGewhV_4LjbDplML17HXUS0He2a8vy_UXFCdkoFbm9Li4D8sc5dhzLTsqiNIsqAXnYx"; //jsonObjectToken.getString("access_token");
        ArrayList<Business> businesses = new ArrayList<Business>();       
       
            // GET /businesses/search
            OkHttpClient client = new OkHttpClient();
       // this code is for demo purposes 
       
       // valid input: city, state, both, zipcode
        System.out.print("Enter a location : ");
        Scanner scannerLoc = new Scanner(System.in);
        String location = scannerLoc.nextLine();
            
        System.out.print("Enter a category : ");
        Scanner scannerCat = new Scanner(System.in);
        String keyword = scannerCat.nextLine();
        

        
        
        /* Search against keywords */
       String category = getKeywords(keyword);
       if(category != "")
           System.out.println("Keyword read: " +keyword+ "\n" + "Using category: " + category + "\n" + "Location read: "+location);
       else
           System.out.println("Category does not exist. Using default category, the most popular places in the location"+ "\n" + "Location read: "+location);        

            //String price = "2";                         // price        1 = $, 2 = $$, 3 = $$$, 4 = $$$$ 
            int limit = 10;                              // # of restaurants to return per request
            
            /* Make a request to Yelp API */
            Request request = new Builder()
                // .url(search_url + term + "&location=" + location + "&limit=1&sort_by=rating&price="+price+"")
                    //.url(search_url + "&location=" + location + "&price=" + price + "&limit="+limit+"&sort_by=rating")
                    .url(search_url + "&location=" + location + "&categories=" + category + "&limit=" + limit + "&sort_by=rating")
                    .get()
                    .addHeader("authorization", "Bearer"+" "+accessToken)
                    .build();

            try {

                /* Get Response */
                Response response = client.newCall(request).execute();
                /* Convert response to JSON object */
                JSONObject jsonObject = new JSONObject(response.body().string().trim());
                /* Get a list of 'businesses' returned by API */
                JSONArray businessesArray = (JSONArray)jsonObject.get("businesses");

                //ArrayList<String> addressList = new ArrayList<>();
                Double lat = 0.0;
                Double lon = 0.0;
                
                /* Loop through businesses (entities) returned and fetch json object to pass into Restaurant() constructor */
                for(int i=0; i<businessesArray.length(); i++)
                {
                    JSONObject jsonBusiness = businessesArray.getJSONObject(i);
                    String name = jsonBusiness.getString("name"); 
                    String url = jsonBusiness.getString("url");   
                    String imageURL = jsonBusiness.getString("image_url");
                    float rating = (float)jsonBusiness.getDouble("rating");  
                    /* Get categories - can be more than one */
                    JSONArray categories = jsonBusiness.getJSONArray("categories");
                    ArrayList<String> categoryList = getCategories(categories);
                    /* Get Addresses */
                    JSONObject addresses = jsonBusiness.getJSONObject("location");
                    JSONArray displayAddresses = addresses.getJSONArray("display_address");
                    ArrayList<String> addressList = getDisplayAddress(displayAddresses);
                    
                    //String priceTag = business.getString("price") == null? "" : business.getString("price");
                    Double distance = jsonBusiness.getDouble("distance");

                    JSONObject latObj = jsonBusiness.getJSONObject("coordinates");
                    if(latObj !=null){
                        lat = latObj.getDouble("latitude");
                        lon = latObj.getDouble("longitude");
                    }
                    
                    /* Create Restaurant(s) object */
                    Business business = new Business(name,rating,url,imageURL,categoryList,addressList,distance,lat,lon);
                    businesses.add(business);
                }
                 //business #
                int n =1;
                for(int j=0; j<businesses.size(); j++)                  
                {   
                    System.out.println("");
                    System.out.println("Business " + n++);
                    System.out.println("Name: " + businesses.get(j).getName());
                    System.out.println("Url: " + businesses.get(j).getUrl());
                    System.out.println("Image Url: " + businesses.get(j).getImageURL());
                    System.out.println("Rating: " + businesses.get(j).getRating());
                    System.out.println("Categories: " + businesses.get(j).getCategories());
                    System.out.println("Address: " + businesses.get(j).getAddress());
                    System.out.println("Distance: " + businesses.get(j).getDistance());
                    System.out.println("Coordinates: Lattitude: " + businesses.get(j).getLat() + ", Longtitude: "+ businesses.get(j).getLon());
                }
            } 
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
  }
    
 
    
 private static String getKeywords(String kWord)
 {
     String strReturn = "";
     /* may read from a .json file here */
     List<Category> keywords = new ArrayList<Category>();
     
    keywords.add(new Category("restaurants", new ArrayList() { { add("food"); add("eat"); add("dinner"); add("lunch"); add("diner"); add("hungry"); add("breakfast"); }} ));
    keywords.add(new Category("bars", new ArrayList() { { add("snack"); add("drink"); add("beer"); add("wine"); add("fingerfood"); add("happyhour"); }} ));
    keywords.add(new Category("movietheaters", new ArrayList() { { add("driveintheater"); add("outdoormovies"); add("cinema"); add("theater"); }} ));
    // new cats added need more keywords
    keywords.add(new Category("active", new ArrayList() { { add("active"); add("Tours"); add("bike"); add("skate"); add("Playgrounds");}} ));
    keywords.add(new Category("adult", new ArrayList() { { add("adult"); add("lingerie"); add("video"); }} ));
    keywords.add(new Category("hotels", new ArrayList() { { add("hotel"); add("sleep"); add("crush"); }} ));
    keywords.add(new Category("museums", new ArrayList() { { add("museums"); add("art"); add(""); }} ));
    keywords.add(new Category("tours", new ArrayList() { { add("tours"); add("excursion"); add("tour"); }} ));
     keywords.add(new Category("gyms", new ArrayList() { { add("sport"); add("gym"); add("trainer"); add("yoga");}} ));
     keywords.add(new Category("fashion", new ArrayList() { { add("fashion"); add("designer"); add("cloth"); add("");}} ));
     keywords.add(new Category("parks", new ArrayList() { { add("jogging"); add("walking"); add("park"); add("nature");}} ));
    
    for(int i = 0; i < keywords.size(); i++)
    {
        if(keywords.get(i).getSubcategories().contains(kWord)) 
        {
            return keywords.get(i).getCategory();
        }
    }
             
    return strReturn;
 }
 private static ArrayList<String> getCategories(JSONArray jArray)
 {
     ArrayList<String> listData = new ArrayList<>();
     
     try{
        if (jArray != null) {           
           for (int i=0;i<jArray.length();i++){ 
           String title = jArray.getJSONObject(i).getString("title");
           listData.add(title);
          } 
       } 
     }
     catch(JSONException ex){ ex.printStackTrace();}
     
     return listData;
 }
 
 private static ArrayList<String> getAddress(JSONObject json)
 {
     ArrayList<String> listData = new ArrayList<>();
     
     try{
        if (json != null) {           
           /* To Do: loop through "display_address" array */
           //for (int i=0;i<jArray.length();i++){ 
           String address1 = json.getString("address1");
           String address2 = json.getString("address2");
           String address3 = json.isNull("address3")? "": json.getString(("address3"));
           String city = json.getString("city");
           String state = json.getString("state");
           String zip = json.getString("zip_code");
           String country = json.getString("country");
           listData.add(address1 + (address2==null? "": ", "+address2) + (address3==null? "": ", "+address3) + ", "+city + ", "+state + ", "+zip + ", " +country );
          //} 
       } 
     }
     catch(JSONException ex){ 
         ex.printStackTrace();
     }
     
     return listData;
 }
 
 private static ArrayList<String> getDisplayAddress(JSONArray jArray)
 {
     ArrayList<String> listData = new ArrayList<>();
     String address = "";
     try{
        if (jArray != null) {           
           for (int i=0;i<jArray.length();i++)
           { 
               //if(jArray.getString(i) != null && jArray.getString(i).length() !=0)
                address += jArray.getString(i)+ ", ";
           }
           if (address.endsWith(", ")) {address = address.substring(0, address.length()-2);}
           listData.add(address);
       } 
     }
     catch(JSONException ex){ ex.printStackTrace();}
     
     return listData;
 }
    
 private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  

}