package com.example.sarahshaikh.goldenhourresponse;
import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class MapsActivity extends FragmentActivity implements
OnMapReadyCallback,
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
LocationListener {
private GoogleMap mMap;
private GoogleApiClient client;
private LocationRequest locationRequest;
private Location lastLocation;
private Marker currentLocationMarker;
public static final int REQUEST_LOCATION_CODE =99;
double latitude, longitude;
private TextView mtextView;
private Button mConfirm;
String add, formattedDate, current_location, LatLng;
private DatabaseReference mDatabaseReference;
private FirebaseAuth mAuth;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_maps);
mDatabaseReference =
FirebaseDatabase.getInstance().getReference().child("Location");
mtextView = (TextView) findViewById(R.id.textView);
mConfirm = (Button) findViewById(R.id.confirm_location);
mAuth = FirebaseAuth.getInstance();
mConfirm.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
Intent desc_intent = new Intent(MapsActivity.this,
DescriptionActivity.class);
startActivity(desc_intent);
finish();
}
});
if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
{
checkLocationPermission();
} // Obtain the SupportMapFragment and get notified when the map is
ready to be used.
SupportMapFragment mapFragment = (SupportMapFragment)
getSupportFragmentManager()
.findFragmentById(R.id.map);
mapFragment.getMapAsync(this);
}
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull
String[] permissions, @NonNull int[] grantResults) {
switch (requestCode)
{
case REQUEST_LOCATION_CODE:
    if(grantResults.length>0 && grantResults[0] ==
PackageManager.PERMISSION_GRANTED)
{
//permission Granted
if (ContextCompat.checkSelfPermission(this,
android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_
GRANTED) {
if (client == null)
{
buildGoogleApiClient();
} mMap.setMyLocationEnabled(true);
}
} else
{
Toast.makeText(this, "Permission Denied!!",
Toast.LENGTH_LONG).show();
} return;
}
}
/**
* Manipulates the map once available.
* This callback is triggered when the map is ready to be used.
* This is where we can add markers or lines, add listeners or move the
camera. In this case,
* we just add a marker near Sydney, Australia.
* If Google Play services is not installed on the device, the user
will be prompted to install
* it inside the SupportMapFragment. This method will only be triggered
once the user has
* installed Google Play services and returned to the app.
*/
@Override
public void onMapReady(GoogleMap googleMap) {
mMap = googleMap;
if(ContextCompat.checkSelfPermission(this,
android.Manifest.permission.ACCESS_FINE_LOCATION)==
PackageManager.PERMISSION_GRANTED)
{
buildGoogleApiClient();
mMap.setMyLocationEnabled(true);
}
} private synchronized void buildGoogleApiClient()
{
client = new GoogleApiClient.Builder(this)
.addConnectionCallbacks(this)
.addOnConnectionFailedListener(this)
.addApi(LocationServices.API)
.build();
client.connect();
  }
@Override
public void onLocationChanged(Location location) {
latitude = location.getLatitude();
longitude = location.getLongitude();
Geocoder geocoder = new Geocoder(MapsActivity.this,
Locale.getDefault());
try {
List<Address> addresses = geocoder.getFromLocation(latitude,
longitude, 1);
Address obj = addresses.get(0);
add = obj.getAddressLine(0);
Log.v("IGA", "Address" + add);
Calendar c = Calendar.getInstance();
SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd
HH:mm:ss");
formattedDate = df.format(c.getTime());
current_location = add + "\n" + "Date and Time:" +
formattedDate;
mtextView.setText(current_location);
LatLng = mAuth.getCurrentUser().getUid();
mDatabaseReference.child(LatLng).child("Loc").setValue(current_location);
mDatabaseReference.child(LatLng).child("Latitude").setValue(latitude);
mDatabaseReference.child(LatLng).child("Longitude").setValue(longitude);
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
}
lastLocation = location;
if(currentLocationMarker != null)
{
currentLocationMarker.remove();
} LatLng latLng =
new LatLng(location.getLatitude(),
location.getLongitude());
MarkerOptions markerOptions = new MarkerOptions();
markerOptions.position(latLng);
markerOptions.title("Current Location");
markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFact
ory.HUE_BLUE));
currentLocationMarker = mMap.addMarker(markerOptions);
mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                                           if(client != null)
{
LocationServices.FusedLocationApi.removeLocationUpdates(client,
this);
}
}
@Override
public void onConnected(@Nullable Bundle bundle) {
locationRequest = new LocationRequest();
locationRequest.setInterval(1000);
locationRequest.setFastestInterval(1000);
locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
;
if(ContextCompat.checkSelfPermission(this,
android.Manifest.permission.ACCESS_FINE_LOCATION)==
PackageManager.PERMISSION_GRANTED)
{
LocationServices.FusedLocationApi.requestLocationUpdates(client,
locationRequest, this);
}
} public boolean checkLocationPermission()
{
if(ContextCompat.checkSelfPermission(this,
android.Manifest.permission.ACCESS_FINE_LOCATION) !=
PackageManager.PERMISSION_GRANTED)
{
if(ActivityCompat.shouldShowRequestPermissionRationale(this ,
android.Manifest.permission.ACCESS_FINE_LOCATION))
{
ActivityCompat.requestPermissions(this, new
String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
REQUEST_LOCATION_CODE);
} else
{
ActivityCompat.requestPermissions(this, new
String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
REQUEST_LOCATION_CODE);
} return false;
} else
return true;
}
@Override
public void onConnectionSuspended(int i) {
}
@Override
                                           public void onConnectionFailed(@NonNull ConnectionResult
connectionResult) {
} @
Override
public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.main_menu, menu);
return super.onCreateOptionsMenu(menu);
}
}
