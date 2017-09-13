
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Rush
 */
@WebServlet(name = "PopulationDash", urlPatterns = {"/PopulationDash", "/getDashBoard"})
public class PopulationDash extends HttpServlet {

    PopulationDashModel pm = new PopulationDashModel();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //estabblishing a connection with the API as we need the API to return a response and not just parse it
        //need to note the time when android app sends an http request to the web service
        DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss");
        DateFormat df2 = new SimpleDateFormat("yyyy");
        Date dateobj = new Date();
        String androidCallTime = (df.format(dateobj) + " EST " + df2.format(dateobj));  //to change the format of the date

        String answer = "";
        String upath = request.getServletPath();  //gets the url path of the request made
        String nextView = "";

        if (upath.equals("/getDashBoard")) {

            Map<Integer, List> finaldash = pm.dashboardOperations();  //a map is created to display the log information on the dashboard jsp page, and the value is List
            ArrayList <String> analytics= pm.doAnalytics();
            request.setAttribute("analyticsoperation",analytics);
            request.setAttribute("populationmap", finaldash);
            nextView = "dashboard.jsp";
        } else {  //if upath isnt getDashBoard, then we need to call API, fetch JSON response and send back miniJSON to android

            HttpURLConnection conn;
            try {

                String user = request.getHeader("User-Agent");  //extracts User-Agent attribute from the request header
                String host = request.getHeader("Host");
                String totalCountries = "";
                String androidCountries = "";
                String year = request.getParameter("year");  //extracts the parameter from the request sent by Android with the parameter name as year
                String age = (String) request.getParameter("age");  //extracts the parameter from the request sent by Android with the parameter name as year
               
                //this if condition checks is to check if the age and year entered by the user are valid (validity defined by the API)
                if (Integer.parseInt(year) > 2100 || Integer.parseInt(year) < 1950 || Integer.parseInt(age) > 100 || Integer.parseInt(age) < 0) {
                   
                    request.setAttribute("jsonreturned", "the API didn't return anything");
                    totalCountries = "0";
                    pm.createJSON(user, host, androidCallTime, year, age, "0", "0");  //if no result was returned then call createJSON and save these values in MongoDB database
                } else {

                    
                    URL url = new URL("http://api.population.io:80/1.0/population/" + year + "/aged/" + age + "/?format=json");  //call to the 3rd party API, WorldPopulation using parameters sent by android
                    conn = (HttpURLConnection) url.openConnection();  //connection is established with the url
                    String output = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                    //read the all the lines from br to string answer
                    while ((output = br.readLine()) != null) {
                        answer += output;

                    }
                    String miniJSON = createSmallerJson(answer);  //pass the response read from InputStream to be parsed and create miniJSON
                    System.out.println("the miniJSON IS: " + miniJSON);
                    int len = miniJSON.length();  //variable to get the number of JSONObjects returned to the android
                    try {
                        JSONArray minor = new JSONArray(miniJSON);
                        androidCountries = String.valueOf(minor.length());  //get the number of JSONObjects returned to the android
                    } catch (JSONException ex) {
                        Logger.getLogger(PopulationDash.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //if the API didn't send back any respond then pass this string to our View, else send this bigJSON string (anser) to createSmallerJson method
                    //and string resturned by this method is sent to our View
                    //if (answer == null) {  
                    //request.setAttribute("jsonreturned", answer);
                    request.setAttribute("jsonreturned", miniJSON);
                    try {
                        JSONArray sum = new JSONArray(answer);
                        totalCountries = String.valueOf(sum.length());  //get the number of JSONObjects returned by the PAI for a set of parameters
                    } catch (JSONException ex) {
                        Logger.getLogger(PopulationDash.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    pm.createJSON(user, host, androidCallTime, year, age, totalCountries, androidCountries);  //pass values to createJSON

                    conn.disconnect();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            nextView = "result.jsp";  //set the view to result.jsp
        }
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }

    public String createSmallerJson(String bigJson) {

        String finalJson = pm.parseJson(bigJson);  //calls the Model to parse the json returned by the API
        return finalJson;

    }

}
