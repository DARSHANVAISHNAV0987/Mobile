package com.kalpeshkundanani.driver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kalpeshkundanani.driver.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
//    double lat,lng;
//    LatLng customerLatLng;
private static final String TAG = "firebase";
    private TextView tvLocation;
    Button buttonMap, addDetails, logoutDriverBtn;
    String userID;
    //    String locationUID = "37ca0946-eb08-40f8-9ba9-4897096b2898";
    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private FirebaseAuth mAuth;
//    private FirebaseUser currentUser; //for logout logic variable
//    Boolean currentLogoutDriverStatus=false; //for logout logic variable
    private Location locationSelf;
    private DatabaseReference assignedCustomerRef, assignedCustomerPickUpRef;
    private String driverID, customerID="";
    LatLng customerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Intent i=getIntent();
//        lat=i.getDoubleExtra("Latitude",0);
//        lng=i.getDoubleExtra("Longitude",0);
//        customerLatLng = i.getParcelableExtra("LatLngCustomer");

        /*testing check*/

        /*Location Code*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationSelf = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

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

        getAssignedCustomerRequest();

    }

    private void getAssignedCustomerRequest() {
        assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(customerID).child("CustomerRideID");
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
                    mMap.addMarker(new MarkerOptions().position(customerLatLng).title("PickUp Location"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*used for Logout user purpose will implement soon */
    private void DisconnectTheDriver() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(driverAvailabilityRef);
        geoFire.removeLocation(userID);
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

    //this function is of LocationRequest type and it is the logic to update location after
    private com.google.android.gms.location.LocationRequest getLocationRequest() {
        com.google.android.gms.location.LocationRequest locationRequest = new com.google.android.gms.location.LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        double lat = locationSelf.getLatitude();
        double lng = locationSelf.getLongitude();
        LatLng customerLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(customerLocation).title("Marker in Vadodara"));
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(customerLocation, 10);
        mMap.animateCamera(cameraUpdate);
    }
}