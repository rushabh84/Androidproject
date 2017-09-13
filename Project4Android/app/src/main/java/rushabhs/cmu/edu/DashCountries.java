package rushabhs.cmu.edu.dashproject4task2;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

import rushabhs.cmu.edu.dashproject4task2.PopulationDash;


/**
 * Created by Rush on 4/8/2017.
 */

public class DashCountries
{
    int dummy =1;
    PopulationDash p = null;

    public void extract(String year, String age, PopulationDash p)
    {

        this.p = p;
        new AsyncPopulationSearch().execute(year, age);

    }


    /*
 * AsyncTask provides a simple way to use a thread separate from the UI thread in which to do network operations.
 * doInBackground is run in the helper thread.
 * onPostExecute is run in the UI thread, allowing for safe UI updates.
 */
    private class AsyncPopulationSearch extends AsyncTask<String, Void, String>
    {


        protected String doInBackground(String... urls) {
            return extract(urls[0],urls[1]);  //urls[0] is year and the other is age, 2 parameters passed to doInBackground
        }

        protected void onPostExecute(String finalValue) {
            p.displayResult(finalValue, dummy);
        }


        private String extract(String year, String age)
        {
            //send a HTTP request to the web service deployed on heroku and read the response returned using BufferedReader
            BufferedReader br=null;
            HttpURLConnection conn;
            String answer = "";
            String androidResult = "";
            try
            {

                //URL url = new URL("https://polar-basin-68077.herokuapp.com/Population?year="+year+"&age="+age);
                URL url = new URL("https://gentle-temple-29720.herokuapp.com/PopulationDash?year="+year+"&age="+age);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "text/json");
                String output = "";
                InputStreamReader i = new InputStreamReader(conn.getInputStream());  //reads the response from connection
                br = new BufferedReader(i);

                while ((output = br.readLine()) != null)
                {
                    answer += output;

                }
                /*since the response from web service has population statistics for 3 countries which includes India OR
                    a message saying the  API didn't find anything. So using any of the country to see what is the response sent by the
                    web service. So if it has no countries, means API found no data for it, and display some default message and not
                    the miniJSON sent by the web service. */
                if(!answer.contains("India"))
                {
                    androidResult = null;
                }
                else{
                    androidResult = parsing(answer);  //pass the result ro parsing method to parse the JSON array returned by web service
                }
                conn.disconnect();
                br.close();

            }
            catch(IOException e){
                androidResult = "";
                e.printStackTrace();
            }
            return androidResult;  //return the result to onPostExecute

        }

        private String parsing(String jsonReturned)
        {
            String final_one="";
            JSONArray result = new JSONArray();
            try {
                JSONArray j1 = new JSONArray(jsonReturned);  //converting the string in JSON array format into a JSONArray
                for (int i = 0; i < j1.length(); i++) {
                    JSONObject obj = j1.getJSONObject(i);  //extracting the JSON Objects
                    //extracting values from JSON objects and appening it to the result string in appropriate format
                    String add = "Population statistics for " + obj.get("country") + " are:\n" + "Females: " + obj.get("females") + "\nMales: " + obj.get("males") + "\nTotal: " + obj.get("total")+"\n";
                    final_one = final_one + add;
                }
            }
            catch(JSONException ex) {
                Logger.getLogger(DashCountries.class.getName()).log(Level.SEVERE, null, ex);
            }
            return final_one;  //return the result back to the extract method
        }

    }
}


