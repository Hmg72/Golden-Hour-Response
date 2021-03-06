package com.example.sarahshaikh.goldenhourresponse;
import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
public class GeoFenceMap extends FragmentActivity implements
OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
LocationListener {
private GoogleMap mMap;
double latitude;
double longitude;
private int PROXIMITY_RADIUS = 2000;
GoogleApiClient mGoogleApiClient;
Location mLastLocation;
Marker mCurrLocationMarker;
LocationRequest mLocationRequest;
Circle myCircle;
static double a,b;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_geo_fence_map);
// Obtain the SupportMapFragment and get notified when the map is
ready to be used.
if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
checkLocationPermission();
}
//Check if Google Play Services Available or not
if (!CheckGooglePlayServices()) {
Log.d("onCreate", "Finishing test case since Google Play
Services are not available");
finish();
} else { Log.d("onCreate","Google Play Services available.");
}
// Obtain the SupportMapFragment and get notified when the map is
ready to be used.
SupportMapFragment mapFragment = (SupportMapFragment)
      getSupportFragmentManager()
.findFragmentById(R.id.map);
mapFragment.getMapAsync(this);
} private boolean CheckGooglePlayServices() {
GoogleApiAvailability googleAPI =
GoogleApiAvailability.getInstance();
int result = googleAPI.isGooglePlayServicesAvailable(this);
if(result != ConnectionResult.SUCCESS) {
if(googleAPI.isUserResolvableError(result)) {
googleAPI.getErrorDialog(this, result,
0).show();
} return false;
} return true;
}
@Override
public void onMapReady(GoogleMap googleMap) {
mMap = googleMap;
mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//Initialize Google Play Services
if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
if (ContextCompat.checkSelfPermission(this,
android.Manifest.permission.ACCESS_FINE_LOCATION)
== PackageManager.PERMISSION_GRANTED) {
buildGoogleApiClient();
mMap.setMyLocationEnabled(true);
}
} else {
buildGoogleApiClient();
mMap.setMyLocationEnabled(true);
}
Button btnHospital = (Button) findViewById(R.id.btnHospital);
btnHospital.setOnClickListener(new View.OnClickListener() {
String Hospital = "hospital";
@Override
public void onClick(View v) {
Log.d("onClick", "Button is Clicked");
mMap.clear();
String url = getUrl(latitude, longitude, Hospital);
Object[] DataTransfer = new Object[2];
DataTransfer[0] = mMap;
DataTransfer[1] = url;
Log.d("onClick", url);
GetNearbyPlaces getNearbyPlacesData = new GetNearbyPlaces();
getNearbyPlacesData.execute(DataTransfer);
Toast.makeText(GeoFenceMap.this,"Nearby Hospitals",
Toast.LENGTH_LONG).show();
}
  });
} protected synchronized void buildGoogleApiClient() {
mGoogleApiClient = new GoogleApiClient.Builder(this)
.addConnectionCallbacks(this)
.addOnConnectionFailedListener(this)
.addApi(LocationServices.API)
.build();
mGoogleApiClient.connect();
} @
Override
public void onConnected(Bundle bundle) {
mLocationRequest = new LocationRequest();
mLocationRequest.setInterval(1000);
mLocationRequest.setFastestInterval(1000);
mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
);
if (ContextCompat.checkSelfPermission(this,
Manifest.permission.ACCESS_FINE_LOCATION)
== PackageManager.PERMISSION_GRANTED) {
LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
mLocationRequest, this);
}
} private String getUrl(double latitude, double longitude, String
nearbyPlace) {
StringBuilder googlePlacesUrl = new
StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json
?");
googlePlacesUrl.append("location=" + latitude + "," + longitude);
googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
googlePlacesUrl.append("&type=" + nearbyPlace);
googlePlacesUrl.append("&sensor=true");
googlePlacesUrl.append("&key=" +
"AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
Log.d("getUrl", googlePlacesUrl.toString());
return (googlePlacesUrl.toString());
} @
Override
public void onConnectionSuspended(int i) {
} @
Override
public void onLocationChanged(Location location) {
Log.d("onLocationChanged", "entered");
mLastLocation = location;
if (mCurrLocationMarker != null) {
mCurrLocationMarker.remove();
}
//Place current location marker
latitude = location.getLatitude();
  longitude = location.getLongitude();
a=latitude;
b=longitude;
LatLng latLng = new LatLng(location.getLatitude(),
location.getLongitude());
MarkerOptions markerOptions = new MarkerOptions();
markerOptions.position(latLng);
markerOptions.title("Current Position");
markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFact
ory.HUE_GREEN));
mCurrLocationMarker = mMap.addMarker(markerOptions);
//move map camera
mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
Toast.makeText(GeoFenceMap.this,"Your Current Location",
Toast.LENGTH_LONG).show();
CircleOptions circleOptions = new CircleOptions()
.center(latLng) //set center
.radius(2000) //set radius in meters
.fillColor(0x40ff0000)
.strokeColor(Color.YELLOW)
.strokeWidth(5);
myCircle = mMap.addCircle(circleOptions);
Log.d("onLocationChanged", String.format("latitude:%.3f
longitude:%.3f",latitude,longitude));
//stop location updates
if (mGoogleApiClient != null) {
LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
this);
Log.d("onLocationChanged", "Removing Location Updates");
} Log.d("onLocationChanged", "
Exit");
}
public static double a(){
return a;
}
public static double b(){
return b;
}
@Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
}
public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
public boolean checkLocationPermission(){
if (ContextCompat.checkSelfPermission(this,
Manifest.permission.ACCESS_FINE_LOCATION)
!= PackageManager.PERMISSION_GRANTED)
if (ActivityCompat.shouldShowRequestPermissionRationale(this,
Manifest.permission.ACCESS_FINE_LOCATION)) {
ActivityCompat.requestPermissions(this,
new
String[]{Manifest.permission.ACCESS_FINE_LOCATION},
MY_PERMISSIONS_REQUEST_LOCATION);
} else {
// No explanation needed, we can request the permission.
ActivityCompat.requestPermissions(this,
new
String[]{Manifest.permission.ACCESS_FINE_LOCATION},
MY_PERMISSIONS_REQUEST_LOCATION);
} return false;
} else {
return true;
}
} @
Override
public void onRequestPermissionsResult(int requestCode,
String permissions[], int[]
grantResults) {
switch (requestCode) {
case MY_PERMISSIONS_REQUEST_LOCATION: {
// If request is cancelled, the result arrays are empty.
if (grantResults.length > 0
&& grantResults[0] ==
PackageManager.PERMISSION_GRANTED) {
if (ContextCompat.checkSelfPermission(this,
Manifest.permission.ACCESS_FINE_LOCATION)
== PackageManager.PERMISSION_GRANTED) {
if (mGoogleApiClient == null) {
buildGoogleApiClient();
} mMap.setMyLocationEnabled(true);
}
} else {
Toast.makeText(this, "permission denied",
Toast.LENGTH_LONG).show();
}
return;
}
}
}
}
