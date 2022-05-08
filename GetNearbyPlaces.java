package com.example.sarahshaikh.goldenhourresponse;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import com.example.sarahshaikh.goldenhourresponse.POJO.Example;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
/**
* Created by Sarah Shaikh on 08-02-2018.
*/
public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
String googlePlacesData;
GoogleMap mMap;
String url;
double lati, longi;
LatLng dest, origin;
Polyline line;
GeoFenceMap mapsActivity = new GeoFenceMap();
@Override
protected String doInBackground(Object... params) {
try {
Log.d("GetNearbyPlacesData", "doInBackground entered");
mMap = (GoogleMap) params[0];
url = (String) params[1];
DownloadUrl downloadUrl = new DownloadUrl();
googlePlacesData = downloadUrl.readUrl(url);
Log.d("GooglePlacesReadTask", "doInBackground Exit");
} catch (Exception e) {
Log.d("GooglePlacesReadTask", e.toString());
  } return googlePlacesData;
}
@Override
protected void onPostExecute(String result) {
Log.d("GooglePlacesReadTask", "onPostExecute Entered");
List<HashMap<String, String>> nearbyPlacesList = null;
DataParser dataParser = new DataParser();
nearbyPlacesList = dataParser.parse(result);
ShowNearbyPlaces(nearbyPlacesList);
Log.d("GooglePlacesReadTask", "onPostExecute Exit");
}
private void ShowNearbyPlaces(List<HashMap<String, String>>
nearbyPlacesList) {
for (int i = 0; i < nearbyPlacesList.size(); i++) {
Log.d("onPostExecute","Entered into showing locations");
MarkerOptions markerOptions = new MarkerOptions();
HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
double lat = Double.parseDouble(googlePlace.get("lat"));
double lng = Double.parseDouble(googlePlace.get("lng"));
String placeName = googlePlace.get("place_name");
String vicinity = googlePlace.get("vicinity");
String reference = googlePlace.get("reference");
LatLng latLng = new LatLng(lat, lng);
markerOptions.position(latLng);
markerOptions.title(placeName + ":\n" + vicinity + ":\n" +
reference);
mMap.addMarker(markerOptions);
mMap.setOnMarkerClickListener(new
GoogleMap.OnMarkerClickListener() {
@Override
public boolean onMarkerClick(Marker marker) {
dest = marker.getPosition();
build_retrofit_and_get_response("driving");
return true;
}
});
markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFact
ory.HUE_YELLOW));
//move map camera
mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
}
}
private void build_retrofit_and_get_response(String type) {
lati = mapsActivity.a();
  longi = mapsActivity.b();
origin = new LatLng(lati,longi);
MarkerOptions mk = new MarkerOptions();
mk.position(origin);
mk.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIO
LET));
String url = "https://maps.googleapis.com/maps/";
Retrofit retrofit = new Retrofit.Builder()
.baseUrl(url)
.addConverterFactory(GsonConverterFactory.create())
.build();
RetrofitMaps service = retrofit.create(RetrofitMaps.class);
Call<Example> call = service.getDistanceDuration("metric",
origin.latitude + "," + origin.longitude,dest.latitude + "," +
dest.longitude, type);
call.enqueue(new Callback<Example>() {
@Override
public void onResponse(Response<Example> response, Retrofit
retrofit) {
try {
//Remove previous line from map
if (line != null) {
line.remove();
} // This loop will go through all the results and add
marker on each location.
for (int i = 0; i < response.body().getRoutes().size();
i++) {
String distance =
response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
String time =
response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
//
mapsActivity.showDistanceDuration.setText("Distance:" + distance + ",
Duration:" + time);
String encodedString =
response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
List<LatLng> list = decodePoly(encodedString);
line = mMap.addPolyline(new PolylineOptions()
.addAll(list)
.width(20)
.color(Color.CYAN)
.geodesic(true)
);
}
} catch (Exception e) {
Log.d("onResponse", "There is an error");
e.printStackTrace();
}
                                          }
@Override
public void onFailure(Throwable t) {
Log.d("onFailure", t.toString());
}
});
}
private List<LatLng> decodePoly(String encoded) {
List<LatLng> poly = new ArrayList<LatLng>();
int index = 0, len = encoded.length();
int lat = 0, lng = 0;
while (index < len) {
int b, shift = 0, result = 0;
do {
b = encoded.charAt(index++) - 63;
result |= (b & 0x1f) << shift;
shift += 5;
} while (b >= 0x20);
int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
lat += dlat;
shift = 0;
result = 0;
do {
b = encoded.charAt(index++) - 63;
result |= (b & 0x1f) << shift;
shift += 5;
} while (b >= 0x20);
int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
lng += dlng;
LatLng p = new LatLng( (((double) lat / 1E5)),
(((double) lng / 1E5) ));
poly.add(p);
}
return poly;
}
}
