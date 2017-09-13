package rushabhs.cmu.edu.dashproject4task2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PopulationDash extends AppCompatActivity {
    public String year;
    public String age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_population_dash);  //the main layout will be activity_main which includes content_main.xml

        final PopulationDash po = this;
        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);

        // Add a listener to the send button
            submitButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View viewPar){
                    year = ((EditText)findViewById(R.id.year)).getText().toString();  //1st parameter to sent to the web service
                    age = ((EditText)findViewById(R.id.age)).getText().toString();  //2nd parameter to sent to the web service
                    if(year.equals("") || age.equals(""))
                        displayResult(null,0);
                    else {

                        rushabhs.cmu.edu.dashproject4task2.DashCountries dc = new rushabhs.cmu.edu.dashproject4task2.DashCountries();  //calling model here
                        dc.extract(year, age, po); // Done asynchronously in another thread.
                    }

            }
        });

    }

    public void displayResult(String toDisplay, int dummy){
        TextView answerView = (TextView)findViewById(R.id.answer);
        //the result returned by the webservice is then parsed in model
        //and an appropriate string to be shown is generated and this string is displayed on the TextView answerView

        //if the result returned by the webservice was null, then show the default message or else the generated result after parsing
        if(toDisplay != null)
        {
            answerView.setText(toDisplay);
        }
        else
        {
            if(dummy == 0)
                //dummy variable is to check if submit button was pressed before either entering year or age
                //if either of them was null then  show the below message, and the inputs were invalid then show the other message
                answerView.setText("Please enter age and year as well. Try re-running the app then.");
            else
                answerView.setText("No results found for this values. Please enter age between 0 and 100 and year between 1950 and 2100.");

        }

    }

}
