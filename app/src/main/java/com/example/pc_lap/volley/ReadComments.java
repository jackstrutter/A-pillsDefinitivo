package com.example.pc_lap.volley;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;


public class ReadComments extends Activity implements OnClickListener {


    Spinner spinner_aulas;
    Spinner spinner_blanco;
    Spinner Precio;
    Spinner Folder;

    /////////////////////////FTP///////////////////////////////
    private static final String TAG = "ReadComments";//para identificar la clase principal
    //private static final String TEMP_FILENAME = "practica18.docx";//archivo a subir
    private Context cntx = null; //contexto donde se ua la app
    private MyFTPClientFunctions ftpclient = null; //se crea el objeto de la lase donde estan los metodos
    private ProgressDialog pd;
    private String[] fileList; //vector para enuerar los archivos en la carpeta
    private static final int ABRIRFICHERO_RESULT_CODE = 1;
    private Button button, btnUploadFile;
    private TextView txtInfo;

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (msg.what == 0) {
                // getFTPFileList();
            } else if (msg.what == 1) {

            } else if (msg.what == 2) {
                Toast.makeText(ReadComments.this, "Archivo subido satisfactoriamente!!",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(ReadComments.this, "Disconnected Successfully!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ReadComments.this, "Unable to Perform Action!",
                        Toast.LENGTH_LONG).show();
            }

        }

    };
//////////////////////////////////Termina FTP/////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_comments);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        spinner_aulas = (Spinner) findViewById(R.id.spinner_aulas);
        spinner_blanco = (Spinner) findViewById(R.id.spinner_blanco);
        Folder = (Spinner) findViewById(R.id.folder);

/////////////////////////INICIA FTP//////////////////////////

        btnUploadFile = (Button) findViewById(R.id.btnUploadFile);
        // btnDisconnect = (Button) findViewById(R.id.btnDisconnectFtp);
        // btnExit = (Button) findViewById(R.id.btnExit);

        cntx = this.getBaseContext();


        btnUploadFile.setOnClickListener(this);

        // Crear un archivo temporal para checar la subida
        //createDummyFile();


        //llamamos los merodos de la ftp
        ftpclient = new MyFTPClientFunctions();

/////////////////////////////////////Termina FTP////////////////////////////

/*
        Spinner spinner_aulas = (Spinner) findViewById(R.id.spinner_aulas);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource(this, R.array.aulas, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_aulas.setAdapter(spinner_adapter);

        Spinner spinner_blanco = (Spinner) findViewById(R.id.spinner_blanco);
        ArrayAdapter spinner_adapter1 = ArrayAdapter.createFromResource(this, R.array.blanco, android.R.layout.simple_spinner_item);
        spinner_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_blanco.setAdapter(spinner_adapter1);


        Spinner Precio = (Spinner) findViewById(R.id.spinner_blanco);
        ArrayAdapter spinner_adapter2 = ArrayAdapter.createFromResource(this, R.array.Dinero, android.R.layout.simple_spinner_item);
        spinner_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Precio.setAdapter(spinner_adapter2);

        Spinner Folder = (Spinner) findViewById(R.id.folder);
        ArrayAdapter spinner_adapter3 = ArrayAdapter.createFromResource(this, R.array.Folder, android.R.layout.simple_spinner_item);
        spinner_adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Folder.setAdapter(spinner_adapter3);

*/
    }



    ///////////////////////////EMPIEZA FTP///////////////////////////////////////
    public void onClick(View o) { //ejecuta las acciones de cada boton
        switch (o.getId()) { //se hace switch con el id de cada boton
            case R.id.button: //si es login
                if (isOnline(ReadComments.this)) {// se manda llamar el metodo para saber si tiene coneccion a internet
                    connectToFTPAddress();//se manda llamar el metodo para conectar al servidor ftp
                    URL connectURL;


                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    startActivityForResult(intent, ABRIRFICHERO_RESULT_CODE);


                } else { //si no tiene
                    Toast.makeText(ReadComments.this,
                            "Please check your internet connection!",
                            Toast.LENGTH_LONG).show();
                }
                break;



            case R.id.btnUploadFile://si es el boton de subir
                break;//cuando termina de ejecutar el thread, se termina




        }

    }



    ////////////////////////////////////Termina FTP/////////////////////////////////////
   /* @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, ABRIRFICHERO_RESULT_CODE);

        }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ABRIRFICHERO_RESULT_CODE:
                if (resultCode == RESULT_OK) {

                    // Mostramos por pantalla la ruta del archivo seleccionado.
                    final String ruta = data.getData().getPath();


                    final String nombre = data.getData().getLastPathSegment();

                    txtInfo.setText(ruta);



                    pd = ProgressDialog.show(ReadComments.this, "", "Enviando Archivo...",
                            true, false);// muestra el cuadrito de crgando
                    new Thread(new Runnable() {//se crea el thread donde se va a ejecutar la coneccion ftp
                        public void run() {
                            boolean status = false;
                            status = ftpclient.ftpUpload(
                                    ruta ,  //Todo esto se puede cambiar por "ruta"
                                    nombre, //Obtiene el nombre de como se llamara el archivo
                                    "/public_html/archivos", cntx);//se pone la carpeta de destino y el nombre que se tendra, lo puse igual para reconocer que archivo se subio
                            if (status == true) {
                                Log.d(TAG, "Upload success"); //se crea un mensaje en el log para saber que si se subio
                                handler.sendEmptyMessage(2);//manda el id del mensaje handler para que muestre un mensaje
                            } else {
                                Log.d(TAG, "Upload failed"); //mensaje del log
                                handler.sendEmptyMessage(-1);//mensaje que aparece x el handler
                            }
                        }
                    }).start();//se cierra el thread y se ejecuta

                }
        }
    }

    /////////////////////////////Empieza FTP//////////////////////////////
    private void connectToFTPAddress() {

        final String host = "files.000webhost.com";//credenciales del ftp
        final String username = "imprefastmx";//se pueden cambiar, para que no las ingrese el usuario
        final String password = "Imprefast007";//las pones predefinidas

        if (host.length() < 1) {//validaciones en caso de que los campos esten vacios
            Toast.makeText(ReadComments.this, "Please Enter Host Address!",//los podemos aplicar en los del login, para que no truene
                    Toast.LENGTH_LONG).show();
        } else if (username.length() < 1) {
            Toast.makeText(ReadComments.this, "Please Enter User Name!",
                    Toast.LENGTH_LONG).show();
        } else if (password.length() < 1) {
            Toast.makeText(ReadComments.this, "Please Enter Password!",
                    Toast.LENGTH_LONG).show();
        } else {

            pd = ProgressDialog.show(ReadComments.this, "", "Subiendo Archivo...",//aparece el cuadro diciendo conecting
                    true, false);

            new Thread(new Runnable() {//se crea el thread para la coneccion
                public void run() { //se mete en un metodo para que deje usar los metodos de la clase
                    boolean status = false;//se inicia la variable de estado,
                    status = ftpclient.ftpConnect(host, username, password, 21);//ejecuta el metodo de coneccion que esta en la otra clase
                    if (status == true) {//se pone un if para checar el estado de la coneccion
                        Log.d(TAG, "Connection Success");// se pone un mensaje en el log para saber el estado
                        handler.sendEmptyMessage(0);//se muetras el mensaje en pantalla para saber el estado
                    } else {
                        Log.d(TAG, "Connection failed");
                        handler.sendEmptyMessage(-1);//se muetras el mensaje en pantalla para saber el estado
                    }
                }
            }).start();
        }
    }


    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}

