package com.example.craig.webconnectdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView TextViewOfReceivedString;
    //This is the page where our web service is
    String URL = "http://craigkoch.greenrivertech.net/WebConnectDemo.php?";

    String holdHttpMethod = "";
    String dataTypeSelection = "";

    JSONObject nameJSON = new JSONObject();
    JSONObject classJSON = new JSONObject();
    JSONArray testJSONArray = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextViewOfReceivedString = (TextView)findViewById(R.id.receivedTextView);
        //button action integrated via xml
        Button getDataButton = (Button)findViewById(R.id.getDataButton);

        //add information to JSON objects and merge them into a JSON array
        try {
            nameJSON.put("FirstName","Craig");
            nameJSON.put("LastName","Koch");
            classJSON.put("ClassNumber", "IT 405");
            classJSON.put("ClassName", "Mobile Development Frameworks");
            nameJSON.put("Class Info", classJSON);
            testJSONArray.put(nameJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void myClickHandler(View view) throws IOException {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //The state of our network connection
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new ConnectMethodHolder().execute(URL);

        } else {
            // display error
            TextViewOfReceivedString.setText("Error connecting to page");
        }
    }

    public void onMethodButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioGet:
                if (checked)
                    holdHttpMethod = "GET";
                    break;
            case R.id.radioPost:
                if (checked)
                    holdHttpMethod = "POST";
                    break;
        }
    }

    public void onDataTypeButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioString:
                if (checked)
                    dataTypeSelection = "dataType=string";
                break;
            case R.id.radioJSON:
                if (checked)
                    dataTypeSelection = "dataType=JSON";
                break;
        }
    }

    //sets up the connection in a new thread.
    private class ConnectMethodHolder extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve content. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            TextViewOfReceivedString.setText(result);
        }
    }

    //perform network connection
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            if(holdHttpMethod.equals("GET")) {
                myurl += dataTypeSelection;
            }
            URL url = new URL(myurl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(holdHttpMethod);
            conn.setDoInput(true);

            //prepares post statement
            if(holdHttpMethod.equals("POST")){
                if(dataTypeSelection.equals("dataType=JSON")){
                    PrintWriter out = new PrintWriter(conn.getOutputStream());
                    out.write(testJSONArray.toString());
                    out.close();
                }
                else {
                    PrintWriter out = new PrintWriter(conn.getOutputStream());
                    out.print(dataTypeSelection);
                    out.close();
                }
            }

            // Starts the query
            conn.connect();

            is = conn.getInputStream();
            URL = "http://craigkoch.greenrivertech.net/WebConnectDemo.php?";
            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return "error";
    }

    //receive response from the server and convert it to string
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
