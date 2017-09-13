
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rush
 */
//This web service is deployed on Heroku and once deployed, the url heroku provides followed by the servlet name will reach this  point.
@WebServlet(name = "Population", urlPatterns = {"/Population/*"})
public class Population extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //estabblishing a connection with the API as we need the API to return a response and not just parse it
        String answer = "";
        HttpURLConnection conn;
        String nextView = "";
        try {
            String year = request.getParameter("year");  //extracts the parameter from the request sent by Android with the parameter name as year 
            String age = (String) request.getParameter("age");  //extracts the parameter from the request sent by Android with the parameter name as year

            //this if condition checks is to check if the age and year entered by the user are valid (validity defined by the API)
            if (Integer.parseInt(year) > 2100 || Integer.parseInt(year) < 1950 || Integer.parseInt(age) > 100 || Integer.parseInt(age) < 0) {
                request.setAttribute("jsonreturned", "the API didn't return anything");
            } else {

               URL url = new URL("http://api.population.io:80/1.0/population/" + year + "/aged/" + age + "/?format=json");  //call to the 3rd party API, WorldPopulation using parameters sent by android
                conn = (HttpURLConnection) url.openConnection();  //connection is established with the url of 3rd party API

                String output = "";
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));  //reading the response sent by the API
                //read the all the lines from br to string answer
                while ((output = br.readLine()) != null) {
                    answer += output;

                }

                //if the API didn't send back any respond then pass this string to our View, else send this bigJSON string (anser) to createSmallerJson method
                //and string resturned by this method is sent to our View
                request.setAttribute("jsonreturned", createSmallerJson(answer));

                conn.disconnect();
            }
            nextView = "result.jsp";  //set the View to result.jsp
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //this method sends the bigger JSON returned by the API to the Model class in order to parse it and create a smaller JSON.
    public String createSmallerJson(String bigJson) {
        PopulationModel pm = new PopulationModel();
        String finalJson = pm.parseJson(bigJson);  //calls the Model to parse the json returned by the API
        return finalJson;

    }

}
