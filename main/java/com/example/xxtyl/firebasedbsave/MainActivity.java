package com.example.xxtyl.firebasedbsave;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static String[] labelArray,answerArray;
    public static int currentIndex = 1;
    public static int movieCounter = 0;
    public static int tvCounter = 0;
    public static boolean movieCheck = false;
    public static boolean tvCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region OnstartStuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Movie Saver");
        //endregion

        //region Variables
        final String DeviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        final Button addMovie = (Button) findViewById(R.id.addMovie);
        final Button addTVShow = (Button) findViewById(R.id.addTV);
        final TextView labelText = (TextView) findViewById(R.id.label);
        final EditText answerText = (EditText) findViewById(R.id.answer);
        final Button addBtn = (Button) findViewById(R.id.addButton);
        final Button viewItems = (Button) findViewById(R.id.viewItems);
        //endregion

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(DeviceID);

        //region Movie Change Click
        addMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelText.setText("Movie Title");
                labelText.setVisibility(View.VISIBLE);
                answerText.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.VISIBLE);
                currentIndex = 1;
            }
        });
        //endregion

        //region TV Change Click
        addTVShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelText.setText("TV Title");
                labelText.setVisibility(View.VISIBLE);
                answerText.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.VISIBLE);
                currentIndex = 2;
            }
        });
        //endregion

        //region Create TV/Movie Counter Stuff
        DatabaseReference getRef = FirebaseDatabase.getInstance().getReference()
                .child(DeviceID).child("MovieCounter");

        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Map<String, HashMap> objectMap = (HashMap<String, HashMap>) dataSnapshot.getValue();
                    Object mapToObject = objectMap.get("counter");
                    String objectToString = mapToObject.toString();
                    movieCounter = Integer.parseInt(objectToString);
                    movieCheck = true;

                } else {

                    movieCounter = 0;
                    Map<String,Object> counterTaskMap = new HashMap<>();
                    counterTaskMap.put("counter", movieCounter);
                    myRef.child("MovieCounter").updateChildren(counterTaskMap);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Failed to read value.");
            }
        });


        DatabaseReference tvRef = FirebaseDatabase.getInstance().getReference()
                .child(DeviceID).child("TVCounter");

        tvRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Map<String, HashMap> objectMap = (HashMap<String, HashMap>) dataSnapshot.getValue();
                    Object mapToObject = objectMap.get("counter");
                    String objectToString = mapToObject.toString();
                    tvCounter = Integer.parseInt(objectToString);
                    tvCheck = true;

                } else {

                    tvCounter = 0;
                    Map<String,Object> counterTaskMap = new HashMap<>();
                    counterTaskMap.put("counter", tvCounter);
                    myRef.child("TVCounter").updateChildren(counterTaskMap);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Failed to read value.");
            }
        });
        //endregion

        //region Add Movie/TV to DB
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String answer;
                String title = "";
                String counterTitle = "";
                int counter = 0;
                answer = answerText.getText().toString();
                Map<String,Object> taskMap = new HashMap<>();
                Map<String,Object> counterTaskMap = new HashMap<>();

                if(currentIndex ==1) {

                    if(movieCheck) {
                        movieCounter++;
                    }
                    title = "Movie";
                    counter = movieCounter;
                    counterTitle = "MovieCounter";

                } else {

                    if(tvCheck){
                        tvCounter++;
                    }
                    title = "TVShow";
                    counter = tvCounter;
                    counterTitle = "TVCounter";

                }

                taskMap.put(title, answer);
                myRef.child(title).child(title + counter).updateChildren(taskMap);
                counterTaskMap.put("counter", counter);
                myRef.child(counterTitle).updateChildren(counterTaskMap);
                answerText.setText("");

            }
        });
        //endregion

        //region View Movies/TV on other page
        viewItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SavedActivity.class);
                intent.putExtra("index", currentIndex);
                startActivity(intent);
            }
        });
        //endregion

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
