package com.example.pc_lap.volley;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener{
    private EditText user, pass, nom, tel;

    private EditText name, age, suff, all,his;
    private Button mRegister;


    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //si lo trabajan de manera local en xxx.xxx.x.x va su ip local
    // private static final String REGISTER_URL = "http://xxx.xxx.x.x:1234/cas/register.php";

    //testing on Emulator:
    private static final String REGISTER_URL = "http://imprefastmx.000webhostapp.com/TEST/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        nom = (EditText)findViewById(R.id.NOMBRE_COMPLETO);
        user = (EditText)findViewById(R.id.CORREO_ELECTRONICO);
        pass = (EditText)findViewById(R.id.PASSWORD);
        tel = (EditText)findViewById(R.id.TELEFONO);
        name = (EditText) findViewById(R.id.patient_name2);
        age = (EditText) findViewById(R.id.age2);
        suff = (EditText) findViewById(R.id.sufferings2);
        all = (EditText) findViewById(R.id.allergies2);
        his = (EditText) findViewById(R.id.history2);

        mRegister = (Button)findViewById(R.id.registerB2);
        mRegister.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        new CreateUser().execute();

    }

    class CreateUser extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String NOMBRE_COMPLETO = nom.getText().toString();
            String CORREO_ELECTRONICO = user.getText().toString();
            String PASSWORD = pass.getText().toString();
            String TELEFONO = tel.getText().toString();
            String patient_name = name.getText().toString();
            String patient_age = age.getText().toString();
            String sufferings = suff.getText().toString();
            String allergies = all.getText().toString();
            String history = his.getText().toString();

            try {
                // Building Parameters
                List params = new ArrayList();

                params.add(new BasicNameValuePair("NOMBRE_COMPLETO", NOMBRE_COMPLETO));
                params.add(new BasicNameValuePair("CORREO_ELECTRONICO", CORREO_ELECTRONICO));
                params.add(new BasicNameValuePair("PASSWORD", PASSWORD));
                params.add(new BasicNameValuePair("TELEFONO", TELEFONO));
                params.add(new BasicNameValuePair("patient_name", patient_name));
                params.add(new BasicNameValuePair("patient_age", patient_age));
                params.add(new BasicNameValuePair("sufferings", sufferings));
                params.add(new BasicNameValuePair("allergies", allergies));
                params.add(new BasicNameValuePair("history", history));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    Intent intent = new Intent(Register.this,MainActivity.class);
                    startActivity(intent);
                    return json.getString(TAG_MESSAGE);

                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();

            }
        }
    }
}