package com.example.sarahshaikh.goldenhourresponse;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class RegisterActivity extends AppCompatActivity {
private EditText mUser, mEmail, mPassword, mPhone;
private Button mRegisterBtn;
private static final String TAG = "Zainab";
String account = null;
private DatabaseReference mDatabase;
private FirebaseAuth mAuth;
Spinner TypeOfAccount;
ArrayAdapter adapter;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_register);
adapter = ArrayAdapter.createFromResource(this,
R.array.type_of_account, android.R.layout.simple_spinner_item);
  mAuth = FirebaseAuth.getInstance();
mDatabase =
FirebaseDatabase.getInstance().getReference().child("Location");
mEmail = (EditText) findViewById(R.id.reg_email);
mPassword = (EditText) findViewById(R.id.reg_password);
mRegisterBtn = (Button) findViewById(R.id.reg_regBtn);
mPhone = (EditText) findViewById(R.id.reg_phone);
TypeOfAccount = (Spinner)findViewById(R.id.reg_accountType);
TypeOfAccount.setAdapter(adapter);
TypeOfAccount.setOnItemSelectedListener(new
AdapterView.OnItemSelectedListener() {
@Override
public void onItemSelected(AdapterView<?> parent, View view, int
position, long id) {
account = TypeOfAccount.getSelectedItem().toString();
}
@Override
public void onNothingSelected(AdapterView<?> parent) {
}
});
mRegisterBtn.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
String email = mEmail.getText().toString();
String password = mPassword.getText().toString();
final String phone = mPhone.getText().toString();
if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)
|| !TextUtils.isEmpty(phone) ){
mAuth.createUserWithEmailAndPassword(email, password)
.addOnCompleteListener(new
OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull
Task<AuthResult> task) {
if (task.isSuccessful()) {
// Sign in success, update UI with
the signed-in user's information
switch(account){
case "User/Volunteer":{
Log.d(TAG,
"createUserWithEmail:success");
// FirebaseUser user =
mAuth.getCurrentUser();
String user = mAuth.getCurrentUser().getUid();
  mDatabase.child(user).child("phone"). setValue(phone);
mDatabase.child(user).child("account").setValue(account);
Intent intent = new
Intent(RegisterActivity.this, MainActivity.class);
startActivity(intent);
finish();
}break;
case "Resource":{
Log.d(TAG,
"createUserWithEmail:success");
// FirebaseUser user =
mAuth.getCurrentUser();
String user =
mAuth.getCurrentUser().getUid();
mDatabase.child(user).child("phone"). setValue(phone);
mDatabase.child(user).child("account").setValue(account);
Intent intent = new
Intent(RegisterActivity.this, Resource.class);
startActivity(intent);
finish();
}
}
} else {
// If sign in fails, display a
message to the user.
Log.w(TAG,
"createUserWithEmail:failure", task.getException());
Toast.makeText(RegisterActivity.this,
"Authentication failed.",
Toast.LENGTH_SHORT).show();
}
}
});
} else{
Toast.makeText(RegisterActivity.this, "Fields are
empty", Toast.LENGTH_LONG).show();
}
}
});
}
}
