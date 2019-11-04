package com.example.meeko.sentry;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;


public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    DrawerLayout mDrawerLayout;
    Switch heatmapSwitch;

    public Marker[] IncidentMarker;
    public Marker[] OriginMarker;
    public int MarkerInctemp=0;
    public boolean LocationProvider = true;
    public LatLng temp = new LatLng(0,0);
    Handler h = new Handler();
    public PolygonTest mpolt = new PolygonTest();
    private GoogleMap mMap;
    Marker mReports;
    private Marker[] PoliceMarker;
    private Marker userMark;
    LatLng center = new LatLng(10.664000, 122.95);
    public static boolean ONGOING=true;
    public static String CURRENT_USER="Tempo User";
    public int Markertemp=0;
    public boolean IncidentFound=false;
    boolean witnessReport=false;
    boolean nearIncident = false;

    //Parallel Arrays
    int[] LocationValues = new int[8];
    final Polygon[] polys = new Polygon[8];
    public LatLng[][] LocationController =  new LatLng[8][];
    final String[] LocationNames = new String[8];
    String userEmail;


    //Constructors
    boolean login = true;
    protected LocationManager locationManager;
    NotificationCompat.Builder mBuilder;
    protected Context context;
    TextView txtLat;
    FirebaseFirestore db;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ImageView aboutView, heatMaplegend;
    Dialog settingsDialog;
    FirebaseUser user;


    //Runnable
    Runnable checkstats = new Runnable() {
        @Override
        public void run() {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            CollectionReference CheckInc = db.collection("Incidents");
            CheckInc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    witnessReport=true;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String Witness = documentSnapshot.get("Witnesses").toString();
                            String[] dissect = new String[Witness.split(",").length];
                            dissect = Witness.split(",");
                        for (int x = 0; x<dissect.length;x++){
                            Log.v("DissectLength",""+dissect.length);
                            if( dissect[x].equals(userEmail)){
                                witnessReport=false;
                                Log.v("WitnessCHeck2","Checkpoint");
                            }else{
                                Log.v("WitnessCheck3","Checkpoint");
                            }
                        }
                    }

                }

            });
            if(pref.getBoolean("LoggedIn",true)) {
                h.postDelayed(this, 3000);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home);
        startService(new Intent(getApplicationContext(), BootDeviceReceiver.class));
        db = FirebaseFirestore.getInstance();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("LoggedIn",true);
        edit.putString("policeLoggedIn","false");
        edit.commit();
        Log.v("POWER222",pref.getString("policeLoggedIn",""));
        FacebookSdk.sdkInitialize(this);

        if(ONGOING==true) {
            at.markushi.ui.CircleButton Reportbutton = (at.markushi.ui.CircleButton)findViewById(R.id.btnReport) ;
            Reportbutton.setEnabled(false);
        }



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 10, this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        h.postDelayed(new Runnable() {
            public void run() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 18));
            }
        }, 1000);




        aboutView= (ImageView) findViewById(R.id.aboutView);


        heatMaplegend = (ImageView) findViewById(R.id.aboutView);

        String name = pref.getString("userName", "");
        String email = pref.getString("userEmail", "");
        userEmail=email;
        String imageUrl = pref.getString("userUrl", "");
        CURRENT_USER = name;




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View navHeaderView = navigationView.inflateHeaderView(R.layout.header);
        TextView headerName = (TextView) navHeaderView.findViewById(R.id.userName);
        TextView headerEmail = (TextView) navHeaderView.findViewById(R.id.userEmail);
        headerName.setText(name);
        headerEmail.setText(email);


        new HomeActivity.DownloadImage((ImageView)navHeaderView.findViewById(R.id.profileImage)).execute(imageUrl);

        mDrawerLayout = findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        int id = menuItem.getItemId();

                        if (id == R.id.ice) {
                            Intent iceIntent = new Intent(HomeActivity.this, ICEActivity.class);
                            startActivity(iceIntent);
                        }
                        else if (id == R.id.incidents) {
                            Toast.makeText(getApplicationContext(),"This is for officers only", Toast.LENGTH_SHORT).show();

                        }
                        else if (id == R.id.setup_pass) {
                            Intent lockScreenIntent = new Intent(HomeActivity.this, LockScreenActivity.class);
                            startActivity(lockScreenIntent);

                        }
                        else if (id == R.id.encrypt_device) {
                            startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS),0);
                        }
                        else if (id == R.id.help_centre) {
                            Intent helpIntent = new Intent(HomeActivity.this, HelpCentreActivity.class);
                            startActivity(helpIntent);

                        }
                        else if (id == R.id.about) {
                            settingsDialog = new Dialog(HomeActivity.this);
                            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.about
                                    , null));
                            settingsDialog.show();



                        }

                        return true;
                    }
                });
        h.post(checkstats);
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage){
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try{
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result){
            bmImage.setImageBitmap(result);
        }
    }

    public void onClickLogout(View v){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        h.removeCallbacksAndMessages(checkstats);
        edit.putBoolean("LoggedIn",false);
        edit.clear().commit();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        login = false;
        Intent login = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(login);
        Toast.makeText(getApplicationContext(), "Logout Successful!", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //GETTING THE CURRENT VALUES IN THE DATABASE
        CollectionReference LocationsRef = db.collection("LocationsData");
        LocationsRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        int x = 0;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            LocationValues[x] = Integer.parseInt(documentSnapshot.get("Code").toString());
                            x++;
                        }
                    }
                });
        CollectionReference LocationsRef2 = db.collection("Incidents");
        LocationsRef2
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        Log.v("HELPME",""+queryDocumentSnapshots.size());
                        if (MarkerInctemp != queryDocumentSnapshots.size()) {
                            for(int x=0;x<MarkerInctemp;x++) {
                                if (IncidentMarker[x] != null) {
                                    IncidentMarker[x].remove();
                                }
                                if (OriginMarker[x] != null) {
                                    OriginMarker[x].remove();
                                }
                            }
                            IncidentMarker = new Marker[queryDocumentSnapshots.size()];
                            OriginMarker = new Marker[queryDocumentSnapshots.size()];
                            MarkerInctemp = queryDocumentSnapshots.size();
                        }
                        String Vname="";
                        double Lat=0;
                        double Long=0;
                        int x = 0;
                        int length;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.get("Ongoing").toString().equals("true")) {
                                Vname = documentSnapshot.get("VictimName").toString();
                                Lat = Double.parseDouble(documentSnapshot.get("Latitude").toString());
                                Long = Double.parseDouble(documentSnapshot.get("Longitude").toString());
                                String Witness = documentSnapshot.get("Witnesses").toString();
                                String[] dissect = new String[Witness.split(",").length];
                                length = dissect.length;
                                if(Witness.equals("")) {
                                    length = 0;
                                }
                                dissect = Witness.split(",");
                                if(IncidentMarker[x]==null  ) {
                                    if (IncidentMarker[x] == null) {
                                        IncidentMarker[x] = mMap.addMarker(new MarkerOptions().position(new LatLng(Lat, Long)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                                .title(Vname).snippet("Witness Count: " + length));
                                        if (documentSnapshot.get("OLatitude") != null) {
                                            double olat = documentSnapshot.getDouble("OLatitude");
                                            double olong = documentSnapshot.getDouble("OLongitude");
                                            OriginMarker[x] = mMap.addMarker(new MarkerOptions().position(new LatLng(olat, olong)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(Vname).snippet("Origin"));

                                        }
                                    } else {
                                        IncidentMarker[x].setPosition(new LatLng(Lat, Long));
                                    }
                                }else{
                                }
                            }
                            x++;

                        }
                    }
                });
        //DETERMINING IF THE USER HAS AN ONGOING CASE
        CollectionReference LocationsRef3 = db.collection("Incidents");
        LocationsRef3.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.v("WORKINGTAG","MEHHH");
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String userName = documentSnapshot.getString("VictimName");
                    boolean status = documentSnapshot.getBoolean("Ongoing");
                    if (userName.equals(CURRENT_USER) && status) {
                        ONGOING=status;
                        Log.v("WORKINGTAG",""+status);
                        return;
                    } else if (userName.equals(CURRENT_USER)) {
                        ONGOING=status;
                        Log.v("WORKINGTAG",""+status);
                    } else {
                        ONGOING=false;
                        Log.v("WORKINGTAG",""+ONGOING);
                    }
                }
            }
        });

        CollectionReference LocationsRef4 = db.collection("Officers");

        LocationsRef4
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (Markertemp != queryDocumentSnapshots.size()) {

                            PoliceMarker = new Marker[queryDocumentSnapshots.size()];
                            Markertemp = queryDocumentSnapshots.size();
                        }

                        int x = 0;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String Pname = doc.getString("name");
                            Double Lat = Double.parseDouble(doc.get("Latitude").toString()), Long = Double.parseDouble(doc.get("Longitude").toString());
                            if (doc.get("hidden").equals(false)) {
                                if (PoliceMarker[x] == null) {
                                    PoliceMarker[x] = mMap.addMarker(new MarkerOptions().position(new LatLng(Lat, Long)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .title("p\n" + Pname + "\n" + doc.getString("mobile")));
                                } else {
                                    PoliceMarker[x].setPosition(new LatLng(Lat, Long));
                                }
                            } else {
                                PoliceMarker[x]=null;
                            }
                            x++;
                        }
                    }

                });


    }


    //GOOGLE MAPS API
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().toLowerCase().startsWith("p")){
                    String[] dissect = new String[marker.getTitle().split("\n").length];
                    dissect = marker.getTitle().split("\n");
                    String Phonenumber=dissect[2];
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Phonenumber));
                    try {
                        startActivity(callIntent);
                    }catch(Exception e){
                        Toast.makeText(HomeActivity.this,"Please Turn On Call Permissions!", Toast.LENGTH_SHORT);

                    }

                }
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow

            @Override
            public View getInfoContents(final Marker arg0) {
                if(arg0.getTitle().toLowerCase().startsWith("p")) {
                    String[] dissect = new String[arg0.getTitle().split("\n").length];

                    dissect = arg0.getTitle().split("\n");
                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();

                    // Getting reference to the TextView to set latitude
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                    // Getting reference to the TextView to set longitude
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                    // Setting the latitude
                    tvLat.setText(dissect[1]);

                    // Setting the longitude
                    tvLng.setText(dissect[2]);
                    // Returning the view containing InfoWindow contents
                    return v;
                }
                return null;
            }
        });

        userMark = mMap.addMarker(new MarkerOptions().position(temp).title("Your Location"));


        mMap.setMinZoomPreference(12);
        mMap.setMaxZoomPreference(18);
        // Create a LatLngBounds that includes the city of Bacolod.
        final LatLngBounds BACOLOD = new LatLngBounds(
                new LatLng(10.613618 , 122.912933), new LatLng(10.72531    , 123.029415));
        //LocationNames
        LocationNames[0]="NOHS Area";
        LocationNames[1]="Libertad Area";
        LocationNames[2]="Barangay33";
        LocationNames[3]="Barangay38";
        LocationNames[4]="Roxas Area";
        LocationNames[5]="UNOR Area";
        LocationNames[6]="Barangay36";
        LocationNames[7]="Barangay16";

        //Locations by Polygon
        polys[0] = GeofenceQuad(new LatLng(10.661855, 122.947214), new LatLng(10.659604, 122.946088), new LatLng(10.66145672, 122.94232517), new LatLng(10.663608   , 122.943417));
        polys[1] = GeofenceQuad(new LatLng(10.661855, 122.947214), new LatLng(10.659604, 122.946088), new LatLng(10.658033, 122.949555), new LatLng(10.659215, 122.953263));
        polys[2] = GeofenceQuad(new LatLng(10.661855, 122.947214), new LatLng(10.659215 , 122.953263), new LatLng(10.661333 , 122.954585), new LatLng(10.664423, 122.948214));
        polys[3] = GeofenceQuad(new LatLng(10.666213  ,122.944749), new LatLng(10.664423, 122.948214), new LatLng(10.661855, 122.947214) ,new LatLng(10.663608  , 122.943417));
        polys[4] = GeofenceQuad(new LatLng(10.659604 , 122.946088), new LatLng(10.66145672 , 122.94232517), new LatLng(10.657209  , 122.940076), new LatLng(10.655139 , 122.944627));
        polys[5] = GeofenceQuad(new LatLng(10.658033 , 122.949555), new LatLng(10.659604 , 122.946088), new LatLng(10.655139  , 122.944627), new LatLng(10.654169  , 122.947428));
        polys[6] = GeofenceQuad(new LatLng(10.668013 , 122.940928), new LatLng(10.666213, 122.944749), new LatLng(10.66145672, 122.94232517), new LatLng(10.663683  , 122.937615));
        polys[7] = GeofenceQuad(new LatLng(10.668013, 122.940928), new LatLng(10.669014 , 122.938691) , new LatLng(10.669242  , 122.92824), new LatLng(10.663683, 122.937615));
        //Locations to LatLngArray
        for (int z = 0; z < polys.length; z++) {
            LocationController[z] = new LatLng[]{polys[z].getPoints().get(0), polys[z].getPoints().get(1), polys[z].getPoints().get(2), polys[z].getPoints().get(3)};
        }
        final TextView txttxt = (TextView)findViewById(R.id.textView1) ;


        //RECURSION UPDATE FOR NOTIFICATIONS
        h.postDelayed(new Runnable() {

            int conditional=0;
            public void run() {
                at.markushi.ui.CircleButton button = (at.markushi.ui.CircleButton)findViewById(R.id.btnReport);
                if(ONGOING==true) {
                    button.setEnabled(false);
                } else if(conditional%10==0&&!txttxt.getText().toString().equals("")&&ONGOING==true) {
                        Toast.makeText(HomeActivity.this, "You have an ongoing incident!", Toast.LENGTH_SHORT).show();
                    } else{
                    button.setEnabled(true);
                }
                conditional++;
                //NOTIFICATION
                if(txttxt.getText().toString()!=""&temp!=null) {
                    for (int i = 0; i < LocationController.length; i++) {
                        if (mpolt.PointIsInRegion(temp.latitude, temp.longitude, LocationController[i])&LocationValues[i]>6) {
                            showNotification(HomeActivity.this,"You are in a crimezone!","Caution is advised!",new Intent(HomeActivity.this,HomeActivity.class));

                        } else {

                        }
                        if(mReports!=null){
                            if(mpolt.PointIsInRegion(mReports.getPosition().latitude,mReports.getPosition().longitude,LocationController[i])){

                                if(LocationValues[i]==0) {
                                    polys[i].setFillColor(Color.TRANSPARENT);
                                }else if(LocationValues[i]>0&LocationValues[i]<=3){
                                    polys[i].setFillColor(Color.argb(100,238,130,238));
                                }else if(LocationValues[i]>3&LocationValues[i]<=6){
                                    polys[i].setFillColor(Color.argb(100,0,0,255));
                                }else if(LocationValues[i]>6&LocationValues[i]<=9){
                                    polys[i].setFillColor(Color.argb(100,255,255,0));
                                }else if(LocationValues[i]>9&LocationValues[i]<=12){
                                    polys[i].setFillColor(Color.argb(100,255,165,0));
                                }else{
                                    polys[i].setFillColor(Color.argb(100,255,0,0));
                                }
                            }
                        }
                    }
                }else{

                }

                if(login) {
                    h.postDelayed(this, 8000);
                }

            }
        }, 3000);
        //End of Recursion
        h.postDelayed(new Runnable() {
            public void run() {
                if(LocationProvider) {
                    CollectionReference CheckInc = db.collection("Incidents");
                    CheckInc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            nearIncident=false;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.get("Ongoing").equals(true)) {
                                    float dist = getDistancBetweenTwoPoints(temp.latitude, temp.longitude, documentSnapshot.getDouble("Latitude"), documentSnapshot.getDouble("Longitude"));
                                    Log.v("DISTANCE", "" + (int) dist + "meters");
                                    if (dist < 150) {
                                        nearIncident=true;

                                        showNotificationNearIncident(HomeActivity.this, "You are near an Incident!", "Nearest Incident: " + (int) dist + "meters", new Intent(HomeActivity.this, HomeActivity.class));
                                    }
                                }
                            }
                        }
                    });


                    Log.v("HOW MANY INSTANCES", " " + abc++);
                }
                if(login) {
                    h.postDelayed(this, 8000);
                }
                //FOR CHECK IF ALREADY REGISTERED TO AN INCIDENT

                Log.v("WitnessCheck",""+witnessReport);
            }
        }, 4000);
    }

    public Polygon GeofenceQuad(LatLng coor1,LatLng coor2,LatLng coor3,LatLng coor4) {
        Polygon pol = null;
        return  pol = mMap.addPolygon(new PolygonOptions().add(coor1, coor2, coor3, coor4).strokeWidth(0.5f).fillColor(Color.argb(0, 0, 0, 0)).strokeColor(Color.TRANSPARENT));


    }

    boolean ColorisActive=false;
    public void onClickHeatmap(View v) { // FUNCTION FOR HEATMAP
        if(ColorisActive==true) {
            for (int i = 0; i < polys.length; i++) {
                polys[i].setFillColor(Color.TRANSPARENT);
            }
            ColorisActive=false;
        }else{
            for (int i = 0; i < polys.length; i++) {
                if(LocationValues[i]==0) {
                    polys[i].setFillColor(Color.TRANSPARENT);
                }else if(LocationValues[i]>0&LocationValues[i]<=3){
                    polys[i].setFillColor(Color.argb(100,238,130,238));
                }else if(LocationValues[i]>3&LocationValues[i]<=6){
                    polys[i].setFillColor(Color.argb(100,0,0,255));
                }else if(LocationValues[i]>6&LocationValues[i]<=9){
                    polys[i].setFillColor(Color.argb(100,255,255,0));
                }else if(LocationValues[i]>9&LocationValues[i]<=12){
                    polys[i].setFillColor(Color.argb(100,255,165,0));
                }else{
                    polys[i].setFillColor(Color.argb(100,255,0,0));
                }
            }
            ColorisActive=true;
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
        finish();
        startActivity(intent);
    }



    @Override
    public void onProviderDisabled(String provider) {
        LocationProvider = false;
    }

    int abc;
    @Override
    public void onLocationChanged(Location location) {
        if(userMark!=null) {
            userMark.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        txtLat = (TextView) findViewById(R.id.textView1);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        if(txtLat.getText().toString()!="TextView") {
            temp = new LatLng(location.getLatitude(), location.getLongitude());

        }else if(txtLat.getText().toString()==""){
            Toast.makeText(this,"No Available GSM", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onResume(){
        super.onResume();
        at.markushi.ui.CircleButton btn = (at.markushi.ui.CircleButton)findViewById(R.id.btnReport);
        btn.setEnabled(false);
    }

    public void ReportBtn(View view) {
        if (isNetworkConnected()&&LocationProvider&&temp!=null) {
            Log.v("hehe "+nearIncident,""+witnessReport);
            if(nearIncident&&witnessReport){
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("An Incident is already located near you!");
                builder2.setMessage("Would you like to register as a witness to the crime?");
                builder2.setCancelable(true);
                builder2.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final CollectionReference CheckInc = db.collection("Incidents");
                        CheckInc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                nearIncident=false;
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (documentSnapshot.get("Ongoing").equals(true)) {
                                        float dist = getDistancBetweenTwoPoints(temp.latitude, temp.longitude, documentSnapshot.getDouble("Latitude"), documentSnapshot.getDouble("Longitude"));
                                        Log.v("DISTANCE", "" + (int) dist + "meters");
                                        if (dist <= 50) {
                                            witnessReport=false;
                                            nearIncident=true;
                                            String temp= documentSnapshot.get("Witnesses").toString();
                                            if(temp!="") {
                                                temp = temp + "," + userEmail;
                                            }else{
                                                temp=userEmail;
                                            }
                                            CheckInc.document(documentSnapshot.getId()).update("Witnesses",temp);

                                        }else {

                                        }
                                    }
                                }

                            }
                        });
                    }
                });
                builder2.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                        builder1.setTitle("Report Incident!");
                        builder1.setMessage("Do you want to enter a new Incident instead?");
                        builder1.setCancelable(true);

                        builder1.setNegativeButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String temporaryloc = "OutsideBacolodArea";
                                        if (!temp.equals(null) && isNetworkConnected()) {
                                            Map<String, Object> newIncident = new HashMap<>();
                                            String Username = CURRENT_USER;
                                            boolean status = true;
                                            long time = System.currentTimeMillis();
                                            String ts = getDate(time);
                                            newIncident.put("VictimName", Username);
                                            newIncident.put("Latitude", temp.latitude);
                                            newIncident.put("Longitude", temp.longitude);
                                            newIncident.put("IncidentID", Username.charAt(0) + harmonyRound(temp.latitude));
                                            newIncident.put("Timestamp", ts);
                                            newIncident.put("Case", "N/A");
                                            newIncident.put("Category", "N/A");
                                            newIncident.put("Ongoing", status);
                                            newIncident.put("Witnesses","");
                                            for (int i = 0; i < LocationController.length; i++) {
                                                if (mpolt.PointIsInRegion(temp.latitude, temp.longitude, LocationController[i])) {
                                                    newIncident.put("IncidentOrigin", LocationNames[i]);
                                                    temporaryloc = LocationNames[i];

                                                }
                                            }
                                            if (temporaryloc == "OutsideBacolodArea") {
                                                newIncident.put("IncidentOrigin", temporaryloc);
                                            }
                                            db.collection("Incidents").add(newIncident);
                                            at.markushi.ui.CircleButton Reportbutton = (at.markushi.ui.CircleButton) findViewById(R.id.btnReport);
                                            Reportbutton.setEnabled(false);
                                            Reportbutton.setClickable(false);
                                        }
                                    }

                                });

                        builder1.setPositiveButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
            }else {
                do {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle("Report Incident!");
                    builder1.setMessage("Are you sure you want to enter a new incident?");
                    builder1.setCancelable(true);

                    builder1.setNegativeButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String temporaryloc = "OutsideBacolodArea";
                                    if (!temp.equals(null) && isNetworkConnected()) {
                                        Map<String, Object> newIncident = new HashMap<>();
                                        String Username = CURRENT_USER;
                                        boolean status = true;
                                        long time = System.currentTimeMillis();
                                        String ts = getDate(time);
                                        newIncident.put("VictimName", Username);
                                        newIncident.put("Latitude", temp.latitude);
                                        newIncident.put("Longitude", temp.longitude);
                                        newIncident.put("IncidentID", Username.charAt(0) + harmonyRound(temp.latitude));
                                        newIncident.put("Timestamp", ts);
                                        newIncident.put("Case", "N/A");
                                        newIncident.put("Category", "N/A");
                                        newIncident.put("Ongoing", status);
                                        newIncident.put("Witnesses","");
                                        for (int i = 0; i < LocationController.length; i++) {
                                            if (mpolt.PointIsInRegion(temp.latitude, temp.longitude, LocationController[i])) {
                                                newIncident.put("IncidentOrigin", LocationNames[i]);
                                                temporaryloc = LocationNames[i];

                                            }
                                        }
                                        if (temporaryloc == "OutsideBacolodArea") {
                                            newIncident.put("IncidentOrigin", temporaryloc);
                                        }
                                        db.collection("Incidents").add(newIncident);
                                        at.markushi.ui.CircleButton Reportbutton = (at.markushi.ui.CircleButton) findViewById(R.id.btnReport);
                                        Reportbutton.setEnabled(false);
                                        Reportbutton.setClickable(false);
                                    }
                                }

                            });

                    builder1.setPositiveButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } while (!isNetworkConnected());
            }
        }else {
            Toast.makeText(HomeActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }
    public void OnClickRefresh(View view){
        finish();
        startActivity(getIntent());
    }
    public void onBtnCenterView(View view){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(temp.latitude,temp.longitude), mMap.getMaxZoomLevel()));
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("MM-dd-yyyy hh:mm:ss", cal).toString();
        return date;
    }
    public String harmonyRound(double value){
        DecimalFormat threeDForm = new DecimalFormat("######");
        String stringValue = threeDForm.format(value * 100000);
        stringValue.replaceAll(".", "");
        return stringValue;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onProviderEnabled(String provider) {
        LocationProvider = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
    protected void onNewIntent(Intent intent){
    super.onNewIntent(intent);
    if(intent.getStringExtra("methodName").equals("closedCase")){
        Toast.makeText(context, "Case closed successfully!", Toast.LENGTH_SHORT).show();
    }
    }

    //NOTIFICATION METHOD FOR VERSIONS 4.0 to OREO COMPATABILITY
    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Sentry";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
    public void showNotificationNearIncident(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 2;
        String channelId = "channel-02";
        String channelName = "SentryIncidents";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
    private float getDistancBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

        return distance[0];
    }
    private void getDeviceLocation(){

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        temp = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                }
            });

        }catch (SecurityException e){
        }
    }

}