package com.example.aggarwals.nsithangout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by AGGARWAL'S on 9/5/2015.
 */
public class ChoiceDisplay extends AppCompatActivity {


    private final static String MAIN_HTTP = "https://maps.googleapis.com/maps/api/place/";
    private final static String NEARBYPLACES = "nearbysearch";
    private final static String LOCATION = "/json?location=";
    private String TEXTSEARCH = "";
    private final static String QUERY = "/xml?query=";
    private final static String RADIUS = "&radius=";
    private int radius = 500;
    private final static String TYPE = "&types=";
    private final static String KEY = "&key=AIzaSyBAuY7uwzJkS1d1Cp8WLYphhs4UuAZ7ZL4";


    private int choice;
    private int progress = 0;
    private Integer[] colors = {R.color.Lime,
    R.color.Brown,
    R.color.Orange,
    R.color.ClaretRed,
    R.color.Red,
    R.color.Magenta,
    R.color.Emerald,
    R.color.Cyan,
    R.color.Mauve,
    R.color.Steel,
    R.color.Purple};

    private String[] titles = {"Restauarnt","Cafes","Night Clubs","Shopping Malls","Bowling","Movie","Food","Amusement Park's","Park","College Spots","Search"};
    private String[] searches = {"restaurant","cafe","night_club","shopping_mall","bowling_alley","movie_theater","food","amusement_park","park"};

    private ProgressBar progressBar;
    private ListView display_list;
    private ArrayList<ChoiceDisplayObject> objects ;
    private ChoiceDisplayAdapter displayAdapter;
    private SeekBar radiusSeekBar;
    private TextView radiusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_display_activity);
        objects = new ArrayList<ChoiceDisplayObject>();
        displayAdapter = new ChoiceDisplayAdapter(this,objects,this);


        choice = getIntent().getIntExtra(ChoiceFragment.CHOICE,-1);
        getSupportActionBar().setTitle(titles[choice]);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(colors[choice])));
        ForceMenuKeyField();

        progressBar = (ProgressBar)findViewById(R.id.progress);
        display_list = (ListView)findViewById(R.id.display_list);
        display_list.setAdapter(displayAdapter);


        if(Utils.isNetworkAvailable(this)&&choice<9)
            new HttpGetInfo().execute();
        else
            Toast.makeText(getApplicationContext(),"Cannot connect to Internet",Toast.LENGTH_LONG).show();

        display_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh :      if(Utils.isNetworkAvailable(this)&&choice<9)
                                        new HttpGetInfo().execute();
                                    else
                                    Toast.makeText(getApplicationContext(),"Cannot connect to Internet",Toast.LENGTH_LONG).show();
                                    break;
            case R.id.change_radius :   setradiusvalue();
                                        break;
        }
        return  super.onOptionsItemSelected(item);
    }

    private void ForceMenuKeyField(){
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) {
            // presumably, not relevant
        }
    }

    public void setradiusvalue(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ChoiceDisplay.this);
        final View radiusview = getLayoutInflater().inflate(R.layout.radius_layout,null);
        radiusSeekBar = (SeekBar)radiusview.findViewById(R.id.radius_seek);
        radiusTextView = (TextView)radiusview.findViewById(R.id.radius_display);
        radiusSeekBar.setProgress(radius);
        radiusTextView.setText("Radius :"+radius+"(m)");
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                radiusTextView.setText("Radius :"+progress+"(m)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        alert.setTitle("Set Radius")
                .setView(radiusview)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            radius = progress;
                            Toast.makeText(getApplicationContext(),"Radius :"+radius,Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }

    private class HttpGetInfo extends AsyncTask<Void,Void,Void>{

        private String text;
        private String URL;

        HttpGetInfo(){
            text = null;
            GPSGetLocation gpsGetLocation = new GPSGetLocation(ChoiceDisplay.this);
            if (gpsGetLocation.canGetLocation() == true)
                URL = MAIN_HTTP + NEARBYPLACES +LOCATION + gpsGetLocation.getLatitude() + "," + gpsGetLocation.getLongitude()
                        + RADIUS + String.valueOf(radius) + TYPE + searches[choice] + KEY;
        }
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            objects.clear();
            displayAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(URL!=null){
                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    text = Client.execute(httpget, responseHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jplaceobject = new JSONObject(text);
                    JSONArray jplacearray = jplaceobject.getJSONArray("results");

                    if (jplaceobject.getString("status").equals("ZERO_RESULTS"))
                        Toast.makeText(getApplicationContext(),"No "+titles[choice]+ " Present in Current Radius",Toast.LENGTH_SHORT).show();

                    int i;
                    for (i=0;i<jplacearray.length();i++){
                        try {


                                String name = null;
                                String icon = null;
                                String place_id = null;
                                String phtotref = null;
                                String longi = null;
                                String latti = null;
                                String vicinity = null;
                                boolean opennow = false;
                                float photowidth = 0;
                                float photoheight = 0;
                                float rating = -1;

                                if (jplacearray.getJSONObject(i).has("icon"))
                                icon = jplacearray.getJSONObject(i).getString("icon");
                                else
                                icon = null;

                                if (jplacearray.getJSONObject(i).has("place_id"))
                                place_id = jplacearray.getJSONObject(i).getString("place_id");
                                else
                                place_id = null;

                                if (jplacearray.getJSONObject(i).has("name"))
                                name = jplacearray.getJSONObject(i).getString("name");
                                else
                                name = null;

                                if (jplacearray.getJSONObject(i).has("rating"))
                                rating = Float.parseFloat(jplacearray.getJSONObject(i).getString("rating"));
                                else
                                rating = 0;

                                if (jplacearray.getJSONObject(i).has("vicinity"))
                                vicinity = jplacearray.getJSONObject(i).getString("vicinity");
                                else
                                vicinity = null;

                                if (jplacearray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").has("lng")) {
                                    JSONObject object = jplacearray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                                    longi = object.getString("lng");
                                    latti = object.getString("lat");
                                }
                                if (jplacearray.getJSONObject(i).has("photos")) {
                                    JSONObject obj = jplacearray.getJSONObject(i).getJSONArray("photos").getJSONObject(0);
                                    phtotref = obj.getString("photo_reference");
                                    photoheight = Float.parseFloat(obj.getString("height"));
                                    photowidth = Float.parseFloat(obj.getString("width"));
                                }
                                else {
                                    phtotref = null;
                                    photoheight = 0;
                                    photowidth = 0;
                                }

                                if (jplacearray.getJSONObject(i).has("opening_hours")){
                                    String bool = jplacearray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now");
                                    if (bool.equals("false"))
                                        opennow = false;
                                    else
                                        opennow = true;
                                }

                            objects.add(new ChoiceDisplayObject(name, icon, place_id, phtotref, longi, latti,vicinity, opennow, photowidth, photoheight, rating,colors[choice]));

                        }catch (Exception e){}
                    }

                }catch (Exception e){}
            displayAdapter.notifyDataSetChanged();
        }
    }
}
