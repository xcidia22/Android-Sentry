package com.example.meeko.sentry;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class IncidentList extends AppCompatActivity {
    FirebaseFirestore db;
    public int timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_list);
        db = FirebaseFirestore.getInstance();
        //generate list
        final ArrayList<Incident> list = new ArrayList<Incident>();
        final Handler h = new Handler();
        CollectionReference LocationsRef = db.collection("Incidents");
        LocationsRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        String IID="";
                        String Vname="";
                        double Lat=0;
                        double Long=0;
                        String time;
                        String Origin;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.get("Ongoing").toString().equals("true")) {
                                Vname = documentSnapshot.get("VictimName").toString();
                                Lat = Double.parseDouble(documentSnapshot.get("Latitude").toString());
                                Long = Double.parseDouble(documentSnapshot.get("Longitude").toString());
                                time = documentSnapshot.get("Timestamp").toString();
                                IID = documentSnapshot.get("IncidentID").toString();
                                Origin = documentSnapshot.get("IncidentOrigin").toString();

                                Incident incident = new Incident(documentSnapshot.getId(),IID,Vname, time, Lat, Long,Origin);
                                list.add(incident);
                                //instantiate custom adapter
                                CustomAdapter adapter = new CustomAdapter(list, IncidentList.this);

                                //handle listview and assign adapter
                                ListView lView = (ListView) findViewById(R.id.my_listview);
                                lView.setAdapter(adapter);
                            }
                        }
                        if(list.isEmpty()&&isNetworkConnected()){
                            Toast.makeText(IncidentList.this, "No Incidents occuring!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(IncidentList.this,PoliceHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();
                        }else if(!isNetworkConnected()){
                            Toast.makeText(IncidentList.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(IncidentList.this,PoliceHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();
                        }
                    }

                });






    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PoliceHomeActivity.class);
        startActivity(intent);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
