package com.example.xxtyl.firebasedbsave;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedActivity extends AppCompatActivity {

    public static int index = 0;
    public static boolean checked = false;
    public static String title = "";
    public static String counterTitle = "";
    public static int tvCounter = 0;
    public static int movieCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region On Create Items
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //endregion

        //region Get Index From Main Activity
        if(checked) {

        } else {
            Intent mIntent = getIntent();
            index = mIntent.getIntExtra("index", 0);
        }
        //endregion

        //region Variables

        ListView listview = (ListView) findViewById(R.id.listView);
        final ArrayList<String> movieData = new ArrayList<String>();
        final ArrayList<String> tvData = new ArrayList<String>();
        ArrayList<String> dataArray = new ArrayList<String>();
        Button tvShows = (Button) findViewById(R.id.addTV);
        Button movies = (Button) findViewById(R.id.addMovie);

        //endregion

        if(index==1){
            title = "Movie";
            counterTitle = "movie";
            dataArray = movieData;
        } else {
            title="TVShow";
            counterTitle = "tv";
            dataArray = tvData;
        }

        final String DeviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(DeviceID).child(title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataArray);
        listview.setAdapter(arrayAdapter);

        //region Update ListView with all items
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, HashMap> objectMap = (HashMap<String, HashMap>) dataSnapshot.getValue();
                Object mapToObject = objectMap.values();
                String objectToString = mapToObject.toString();
                if(index ==1) {
                    movieData.add(objectToString);
                } else {
                    tvData.add(objectToString);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //endregion

        //region Change Button Index
        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
                index = 1;
                arrayAdapter.notifyDataSetChanged();
                checked = true;
            }
        });

        tvShows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
                index = 2;
                arrayAdapter.notifyDataSetChanged();
                checked = true;
            }
        });
        //endregion

        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                int counter = 0;
                counter = position +1;
                Map<String,Object> taskMap = new HashMap<>();
                myRef.child(title + counter).removeValue();
                myRef.child(title + counter).updateChildren(taskMap);
                finish();
                startActivity(getIntent());
            }
        });

    }

}
