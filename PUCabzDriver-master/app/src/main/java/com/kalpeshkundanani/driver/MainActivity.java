package com.kalpeshkundanani.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;

import android.util.Log;
import android.view.View;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kalpeshkundanani.driver.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "firebase";
    private TextView tvLocation;
    Button buttonMap, addDetails, logoutDriverBtn;
    String userID;
//    String locationUID = "37ca0946-eb08-40f8-9ba9-4897096b2898";
    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Boolean currentLogoutDriverStatus=false;
    private Location location;
    private DatabaseReference assignedCustomerRef, assignedCustomerPickUpRef;
    private String driverID, customerID="";
    LatLng customerLatLng;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //code begins from here below line will ask for permission if not given
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        init();

        /*Driver Logout Logic*/
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        driverID = mAuth.getCurrentUser().getUid();

        logoutDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLogoutDriverStatus = true;
                DisconnectTheDriver();
                mAuth.signOut();
                LogOutDriver();
            }
        });

        /*Location Code*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(getApplicationContext() != null){
                    /*Logic to add available drivers in firebase*/
                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //the below line will either bring the reference of required Location>child id or if not present then it will create one in database and return the reference
                    DatabaseReference driverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                    GeoFire geoFireAvailability = new GeoFire(driverAvailabilityRef);

                    DatabaseReference driverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
                    GeoFire geoFireWorking = new GeoFire(driverWorkingRef);

                    // adding a switch to customer id. it means driver is free.
                    switch (customerID){
                        case "":
                            geoFireWorking.removeLocation(userID);
                            geoFireAvailability.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                        default:
                            geoFireAvailability.removeLocation(userID);
                            geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            }
        };
        //Code for Continuous Location Update
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    /*Logic to add available drivers in firebase*/
                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //the below line will either bring the reference of required Location>child id or if not present then it will create one in database and return the reference
                    DatabaseReference driverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                    GeoFire geoFire = new GeoFire(driverAvailabilityRef);
                    geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                }
            }
        };

        /*til here*/

        /*add details in firestore Intent logic*/
        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity2.class);
                //send details
                startActivity(intent);
            }
        });

//        buttonMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase db = FirebaseDatabase.getInstance();
//                DatabaseReference ref = db.getReference("Location").child(locationUID);
//                ref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        HashMap value = (HashMap) snapshot.getValue();
//                        double latitude = (double) value.get("lat");
//                        double longitude = (double) value.get("lng");
//                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                        intent.putExtra("Latitude", latitude);
//                        intent.putExtra("Longitude", longitude);
//                        intent.putExtra("LatLngCustomer", customerLatLng);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Failed to read value
//                        Log.d(TAG, "onCancelled: " + Log.getStackTraceString(error.toException()));
//                    }
//                });
//            }
//        });

        tvLocation = (TextView) findViewById(R.id.tv_location);
        /*Old code of getting data from firebase and setting it to text view for realtime update*/
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
////        DatabaseReference myRef = database.getReference("Location").child(locationUID);
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                HashMap value = (HashMap) dataSnapshot.getValue();
//                //we can use both String and Object. using String we have to explicit typecast, as it returns Object type value so no need to typecast in case of Object.
////                String lat = (String) value.get("lat");
//                Object lat = value.get("lat");
//                Object lng = value.get("lng");
//                tvLocation.setText("Lat: " + lat + " Lng: " + lng);
//                Log.d(TAG, "onDataChange: " + lat);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.d(TAG, "onCancelled: " + Log.getStackTraceString(error.toException()));
//            }
//        });

        getAssignedCustomerRequest();

    }

    private void getAssignedCustomerRequest() {
        assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverID).child("CustomerRideID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            //here we retrive customer ID
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customerID = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickUpLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAssignedCustomerPickUpLocation() {
        assignedCustomerPickUpRef =  FirebaseDatabase.getInstance().getReference().child("Customer Requests").child(customerID).child("l");

        assignedCustomerPickUpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> customerLocationMap = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0, locationLng = 0;

                    if(customerLocationMap.get(0) !=null){
                        locationLat = Double.parseDouble(customerLocationMap.get(0).toString());
                    }
                    if(customerLocationMap.get(1) !=null){
                        locationLng = Double.parseDouble(customerLocationMap.get(1).toString());
                    }
                    customerLatLng = new LatLng(locationLat, locationLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DisconnectTheDriver() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(driverAvailabilityRef);
        geoFire.removeLocation(userID);
    }

    private void LogOutDriver() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        /*we have to stop current driver from available drivers list in fire base when he's not active*/
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(driverAvailabilityRef);
        geoFire.removeLocation(userID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, null);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //the below line will either bring the reference of required Location>child id or if not present then it will create one in database and return the reference
        DatabaseReference driverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(driverAvailabilityRef);
        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
    }

    private void init() {
        addDetails = findViewById(R.id.addDetails);
        buttonMap=findViewById(R.id.buttonMap);
        logoutDriverBtn = findViewById(R.id.logout_Btn);
    }

    //this function is of LocationRequest type and it is the logic to update location after
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
