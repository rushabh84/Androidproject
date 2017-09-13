import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rush
 */
public class PopulationModel {

    public String parseJson(String mainJson) {
        JSONArray result = new JSONArray();  //JSONArray to store the miniJSON after parsing
        try {
            JSONArray j1 = new JSONArray(mainJson);  //since the bigJSON returned by API was a string array we convert the mainJSON string to JSONArray
            //the elements in the JSONArray are JSONObjects
            JSONObject toAdd;
            for (int i = 0; i < j1.length(); i++) {
                JSONObject toextract = j1.getJSONObject(i);  //extracting JSONObjects at index i
                /*
                for this project, using the WorldPopulation API I will be just returning the JSONObjects corresponding to country as India, United States, Australia
                if the country of the json object 'toextract' equals any of the 3 nations, then extract country, male population, female population for that JSONObject
                 and the total population and add those values to the new jsonObject toAdd with the same ids- country, males, females and total
                */
                
                //this if loop creates new JSONObject to be added in the miniJSONArray which will be sent to Android
                if (toextract.get("country").equals("Australia") || toextract.get("country").equals("India") || toextract.get("country").equals("United States"))
                {
                    toAdd= new JSONObject(); 
                    toAdd.put("country", toextract.get("country"));
                    toAdd.put("males", toextract.get("males"));
                    toAdd.put("females", toextract.get("females"));
                    toAdd.put("total", toextract.get("total"));
                    result.put(toAdd);  //add json object toAdd to the JSONArray result

                }
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(PopulationModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.toString();  //return JSONArray result after converting it to string
    }

}
