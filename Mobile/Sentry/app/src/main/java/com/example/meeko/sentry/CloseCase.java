package com.example.meeko.sentry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meeko.sentry.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.maps.android.MarkerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CloseCase extends AppCompatActivity {
    public String selectedItem="null";
    public String directory="";
    public String origin;
    public int Temp=0;
    public String name="";
    public String[] property;
    public String[] violence;
    public String IID;
    boolean finish=false;
    public String[] communitydisturbance;
    public String category;
    boolean btndone=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_case);
        Bundle extras = getIntent().getExtras();
        directory= extras.getString("directory");
        origin = extras.getString("IncidentOrigin");
        name = extras.getString("name");
        IID = extras.getString("IID");




        // Get reference of widgets from XML layout
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        final Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        final TextView textView = (TextView)findViewById(R.id.SelectedItem) ;


        //FirestoreReference
        // Initializing a String Array
        violence = new String[]{
                "Violence",
                "Assault",
                "Murder",
                "Homicide",
                "Kidnapping",
                "Robbery",
                "Sexual Assault",
                "Sexual Offense"
        };
        property = new String[]{
                "Property",
                "Breaking and Entering",
                "Property Crime",
                "Theft"
        };
        communitydisturbance = new String[]{
                "Community Disturbance",
                "Disorder",
                "Quality of Life",
                "Liquor",
                "Drugs",
                "False Alarm"
        };

        //SPINNER 1
        final List<String> violenceList = new ArrayList<>(Arrays.asList(violence));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,violenceList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    selectedItem=selectedItemText;
                    textView.setText(selectedItemText);
                    category="Violence";
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //SPINNER 2
        final List<String> propertyList = new ArrayList<>(Arrays.asList(property));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,propertyList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner2.setAdapter(spinnerArrayAdapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    selectedItem=selectedItemText;
                    textView.setText(selectedItemText);
                    category="Property";
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //SPINNER 3
        final List<String> misdeamenor = new ArrayList<>(Arrays.asList(communitydisturbance));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,misdeamenor){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter3.setDropDownViewResource(R.layout.spinner_item);
        spinner3.setAdapter(spinnerArrayAdapter3);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    selectedItem=selectedItemText;
                    textView.setText(selectedItemText);
                    category="Community Disturbance";
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onBtnDone(View v) {
        if (!selectedItem.equals("null")) {
            btndone=true;
            UpdateValues();
            Log.v("start","NOW");
            Intent intent = new Intent(CloseCase.this,PoliceHomeActivity.class);
            intent.putExtra("methodName","closedCase");
            //DeleteData(directory);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Select a category first!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,IncidentList.class);
        startActivity(intent);
        finish();
    }
    private void UpdateData() {
        Map<String, Object> IncidentPush = new HashMap<>();
        IncidentPush.put("Ongoing", false);
        IncidentPush.put("Case", selectedItem);
        IncidentPush.put("Category",category);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference update = db.collection("Incidents").document(directory);
        update.set(IncidentPush,SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CloseCase.this, "No Internet Connection\n Failed to store data!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //LOCATION OF INCIDENT UPDATES
    public String tempID="NO VALUE";
    boolean trigger = true;
    public void UpdateValues(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference LocationsRef = db.collection("LocationsData");
        Log.v("Check Name: ",name);
        Log.v("Check Origin: ",origin);
        LocationsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (origin.equals(documentSnapshot.get("name"))) {
                            tempID = documentSnapshot.getId();
                            Temp = Integer.parseInt(documentSnapshot.get("Code").toString());
                                UpdateData();
                            if (Arrays.asList(violence).contains(selectedItem)) {
                                Temp += 3;
                            } else if (Arrays.asList(property).contains(selectedItem)) {
                                Temp += 3;
                            } else if (Arrays.asList(communitydisturbance).contains(selectedItem)) {
                                if (selectedItem.equals(communitydisturbance[0])) {
                                    Temp += 1;
                                } else if (selectedItem.equals(communitydisturbance[1])) {
                                    Temp += 1;
                                }
                                else if(selectedItem.equals(communitydisturbance[5])) {

                                }else {
                                    Temp += 3;
                                }


                            }else{

                            }


                            DocumentReference update = db.collection("LocationsData").document(tempID);
                            update.update("Code", Temp)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CloseCase.this, "Update Error\n Failed to store data!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            DocumentReference transfer = db.collection("Incidents").document(directory);
                            transfer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String, Object> getIncident = new HashMap<>();
                                    getIncident.putAll(documentSnapshot.getData());
                                    Log.v("works", "" + getIncident.size());
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("ClosedIncidents").add(getIncident);
                                }


                            });

                            //IF OUTSIDE BACOLOD AREA
                        }else if(origin.equals("OutsideBacolodArea")&&trigger) {
                            trigger=false;
                            final DocumentReference transfer = db.collection("Incidents").document(directory);
                            UpdateData();
                            transfer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.get("IncidentID").equals(IID)&&name.equals(documentSnapshot.get("VictimName"))) {
                                        Map<String, Object> getIncident = new HashMap<>();
                                        Log.v("MEH", "WORKING ALAS22");
                                        getIncident.putAll(documentSnapshot.getData());
                                        Log.v("works", "" + getIncident.size());
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("ClosedIncidents").add(getIncident);
                                    }
                                }


                            });

                        }else {
                            Log.v("MEH",name);
                            Log.v("ELEMENTS", documentSnapshot.getData().toString());
                        }


                    }
                }


            }
        });



    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(btndone) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference del = db.collection("Incidents").document(directory);
            del.delete();
        }


    }
    }




