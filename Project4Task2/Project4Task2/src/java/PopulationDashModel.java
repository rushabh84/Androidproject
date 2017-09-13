import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Rush
 */
public class PopulationDashModel {
    
    Map<Integer, List> dashMap = new HashMap<>();

    public int totalpop = 0;
    public int femaleTotal = 0;
    public int maleTotal = 0;
    
    //  Source to create connection to the MongoDB database on mlab: http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/
    MongoClientURI uri = new MongoClientURI("mongodb://rushpopulation:Population@ds161210.mlab.com:61210/worldpopulation");  //connects to my MongoDB database on mlab
    MongoClient client = new MongoClient(uri);  //creates a client that interacts with the database at the uri specified
    MongoDatabase statdatabase = client.getDatabase("worldpopulation");  //gets the database from MongoClinet on which the operations need to be performed
    MongoCollection<Document> statcollection = statdatabase.getCollection("Thepopulation");  //gets the collection from the database on which operations need to be performed

    public String parseJson(String mainJson) {
        JSONArray result = new JSONArray();
        try {
            JSONArray j1 = new JSONArray(mainJson);
            JSONObject toAdd;
            for (int i = 0; i < j1.length(); i++) {
                JSONObject toextract = j1.getJSONObject(i);

                /*for this project, using the API I am just returning the JSONObjects for India, United States, Australia
                if the country of the json object toextract equals any of the 5 nations, then extract country, male population, female population
                 and the total population and add those values to the new json object toAdd with the same ids- country, males, females and total
                 */
                //this if loop creates new JSONObject to be added in the miniJSONArray which will be sent to Android
                if (toextract.get("country").equals("Australia") || toextract.get("country").equals("India") || toextract.get("country").equals("United States")) {
                    toAdd = new JSONObject();
                    toAdd.put("country", toextract.get("country"));
                    toAdd.put("males", toextract.get("males"));
                    toAdd.put("females", toextract.get("females"));
                    toAdd.put("total", toextract.get("total"));
                    result.put(toAdd);  //add json object toAdd to the JSONArray result
                    //summing up the total, females and males population,  for all three specified countries
                    totalpop = totalpop + Integer.parseInt(toextract.get("total").toString());  //summing up the total population for all three specified countries
                    maleTotal = femaleTotal + Integer.parseInt(toextract.get("males").toString());
                    femaleTotal = femaleTotal + Integer.parseInt(toextract.get("females").toString());
                    System.out.println("the odododod is: " + femaleTotal);
                }
            }
            System.out.println(result);

        } catch (JSONException ex) {
            Logger.getLogger(PopulationDashModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.toString();  //return JSONArray result after converting it to string
    }

    public void createJSON(String user, String host, String requestTime, String year, String age, String total, String minor) {
        //with the parameters passed to this method, create a JSON string 
        if(Integer.parseInt(total) == 0)  //means API didn't return any resut, then total of all population should be made 0
        {
            totalpop=0;
            maleTotal=0;
            femaleTotal=0;
        }
        String logJSON = "{ \"User-agent\": \"" + user + "\" ,"
                + "\"Host\": \"" + host + "\" ,"
                + "\"Time\": \"" + requestTime + " \" ,"
                + "\"year\": \"" + year + "\" ,"
                + "\"age\": \"" + age + " \" ,"
                + "\"total\": \"" + total + " \" ,"
                + "\"minor\": \"" + minor + " \" ,"
                + "\"totalpop\": \"" + String.valueOf(totalpop) + " \" ,"
                + "\"femaleTotal\": \"" + String.valueOf(femaleTotal) + " \" ,"
                + "\"maleTotal\": \"" + String.valueOf(maleTotal) + " \" ,"
                + "},";

        Document d = Document.parse(logJSON);  //since a Document can be stored in the MongoDB collection
        //and we can pass a json string to a document by parsing the JSON string
        statcollection.insertOne(d);  //insert the document containing the log information in the MongoDB collection 

    }

    public Map dashboardOperations() {
        
        //Source:http://stackoverflow.com/questions/41916428/how-to-retrieve-find-all-elements-of-a-nested-array-in-mongodb-java
        //Source: http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/
        List<Document> dash = (List<Document>) statcollection.find().into(new ArrayList<Document>());  //extract all Documents from the collection an create a List of those Documents
        //hashmap is created to store all the documents and the value is a List object
        
         //List of Strings to be stored as a value in HashMap
        int i = 0;
        //for all documents in the List dash, extract all elements of Document and add it to the List whhich then is added as a value to the HashMap
        for (Document d : dash) {
            List<String> dashList = new ArrayList<String>(); 
            dashList.add(d.getString("User-agent"));
            dashList.add(d.getString("Host"));
            dashList.add(d.getString("Time"));
            dashList.add(d.getString("year"));
            dashList.add(d.getString("age"));
            dashList.add(d.getString("total"));
            dashList.add(d.getString("minor"));
            dashList.add(d.getString("totalpop"));
            dashList.add(d.getString("femaleTotal"));
            dashList.add(d.getString("maleTotal"));
            dashMap.put(i, dashList);
            System.out.println("the list vala is"+dashList);
            i = i + 1;  //increment the key for HashMap

        }
        return dashMap;

    }
    
    public ArrayList<String> doAnalytics(){
        //Source: https://docs.mongodb.com/manual/reference/operator/meta/max/
        ArrayList<String> analytics = new ArrayList<String>();
        //aggregate function returms a document, and the below opeartion sorts all the document in descending order on the basis of totalpop and finds document 
        //with the max value of totalpop
        Document dtotal = statcollection.aggregate(
                Arrays.asList(
                        Aggregates.sort(Sorts.descending("totalpop")),
                        Aggregates.limit(1)
                )
        ).first();
        
        //aggregate function returms a document, and the below opeartion sorts all the document in descending order on the basis of femaleTotal and finds document 
        //with the max value of femaleTotal
        Document dfemale = statcollection.aggregate(
                Arrays.asList(
                        Aggregates.sort(Sorts.descending("femaleTotal")),
                        Aggregates.limit(1)
                )
        ).first();
        
        //aggregate function returms a document, and the below opeartion sorts all the document in descending order on the basis of maleTotal and finds document 
        //with the max value of maleTotal
        Document dmale = statcollection.aggregate(
                Arrays.asList(
                        Aggregates.sort(Sorts.descending("maleTotal")),
                        Aggregates.limit(1)
                )
        ).first();
        //adds the population stats, year and age for each of the 3 maximum value in analytics arraylist
        analytics.add("The total popultaion of "+dtotal.get("totalpop")+" was the highest for year "+dtotal.get("year")+" and for age "+dtotal.get("age"));
        analytics.add("The total female popultaion of "+dfemale.get("femaleTotal")+" was the highest for year "+dfemale.get("year")+" and for age "+dfemale.get("age"));
        analytics.add("The total male popultaion of "+dmale.get("maleTotal")+" was the highest for year "+dmale.get("year")+" and for age "+dmale.get("age"));
        
        
       return analytics;
    }
       
        
    
}
