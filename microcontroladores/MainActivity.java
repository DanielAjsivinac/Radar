package com.example.microcontroladores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    /*
    Declaración de variables llamadas desde activity main.
    */

    TextView txtTemperatura;
    Button btn_desconectar, btn_conectar;
    EditText envio;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    /*
        Necesarias para BT
    */

    Handler bluetoothIn;
    String dat = "";
    public static Bundle nuevoBundle;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIn = new StringBuilder();
    private ConnectedThread miConexionBT;
    private  final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;
    private Integer gradosR1 = 0;
    private Integer gradosR2 = 0;
    private Integer distanciaR1 = 0;
    private Integer distanciaR2 = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpView();
        setUpViewPagerAdapter();


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {          //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    if(readMessage.equals(";")){
                        Log.d("MY_APP_DEBUG_TAG2",dat);
                        String[] tmp =dat.split(",");

                        if(tmp.length == 3){
                            if(tmp[0].equals("1")){
                                gradosR1 = Integer.parseInt(tmp[1]);
                                distanciaR1 = Integer.parseInt(tmp[2]);
                                Log.d("MY_APP_DEBUG_TAG2",String.valueOf(gradosR1));
                                Radar1Fragment.agregarPunto(tmp[1],tmp[2]);
                            }else if(tmp[0].equals("2")){
                                gradosR2 = Integer.parseInt(tmp[1]);
                                distanciaR2 = Integer.parseInt(tmp[2]);
                                Log.d("MY_APP_DEBUG_TAG2",String.valueOf(gradosR2));
                                Radar2Fragment.agregarPunto(tmp[1],tmp[2]);
                            }else{
                                Radar1Fragment.limpiarRadar();
                                Radar2Fragment.limpiarRadar();
                            }
                        }else{
                            Radar1Fragment.limpiarRadar();
                            Radar2Fragment.limpiarRadar();
                        }
                        dat  = "";
                    }else if(!readMessage.equals("/")){
                        dat += readMessage;
                    }

                    //Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_LONG).show();
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        //Llamada a los controladores del XML
/*        txtTemperatura = findViewById(R.id.txtTemperatura);
        btn_desconectar = findViewById(R.id.btnDesconectar);
        btn_conectar = findViewById(R.id.btnConectar);
        envio = findViewById(R.id.txtEntrada);
*/

        /*Listener para dispositivos BT*/

        /*btn_conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miConexionBT.write(envio.getText().toString());
                //txtTemperatura.setText("Envia "+ envio.getText().toString());
            }
        });*/

        /*btn_desconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btSocket!=null){
                    try {
                        btSocket.close();
                    }catch (IOException e){
                        Toast.makeText(getBaseContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });*/

    }


    private void setUpView(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        nuevoBundle = new Bundle();
        nuevoBundle.putInt("grados", gradosR1);
        nuevoBundle.putInt("distancia", distanciaR1);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), nuevoBundle);
    }

    private void setUpViewPagerAdapter(){
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();
        Fragment R1 = new Radar1Fragment();
        Fragment R2 = new Radar2Fragment();
        bundle1.putInt("grados", gradosR1);
        bundle2.putInt("grados", gradosR2);
        bundle1.putInt("distancia", distanciaR1);
        bundle2.putInt("distancia", distanciaR2);
        R1.setArguments(bundle1);
        R2.setArguments(bundle2);
        viewPagerAdapter.addFragment(R1, "Superior");
        viewPagerAdapter.addFragment(R2, "Inferior");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException{
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent newIntent =  getIntent();
        address =  newIntent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device =  btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        }catch (IOException e ){
            Toast.makeText(getBaseContext(), "La creación del socket fallo", Toast.LENGTH_SHORT).show();
        }

        try {
            btSocket.connect();
        }catch (IOException e ){
            try {
                btSocket.close();
            }catch (IOException e2){

            }

        }

        miConexionBT = new ConnectedThread(btSocket);
        miConexionBT.start();

    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            btSocket.close();
        }catch (IOException e){

        }
    }

    private  void VerificarEstadoBT(){
        if(btAdapter == null){
            Toast.makeText(getBaseContext(),"El dispositivo no soporta BT", Toast.LENGTH_LONG).show();

        }else{
            if(!btAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,1);
            }
        }
    }

    private class ConnectedThread extends  Thread{
        private static final String TAG = "MY_APP_DEBUG_TAG";

        private  final InputStream miInputStream;
        private  final OutputStream miOutputStream;

        public  ConnectedThread(BluetoothSocket socket){
            InputStream tmpIn=null;
            OutputStream tmpOut = null;
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException e){

            }
            miInputStream = tmpIn;
            miOutputStream = tmpOut;
        }

        public  void run(){
            int noBytes;
            byte []  bytes_in  = new byte[1];
            while (true){
                try {/*
                    noBytes = miInputStream.read(bytes_in);

                    char ch =  (char) bytes_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                    Log.d(TAG, String.valueOf(ch));
*/

                    noBytes = miInputStream.read(bytes_in);         //read bytes from input buffer
                    String readMessage = new String(bytes_in, 0, noBytes);
                    Log.d(TAG, readMessage);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, noBytes, -1, readMessage).sendToTarget();
                }catch (IOException e){
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(String input){
            try {
                miOutputStream.write(input.getBytes());
            }catch (IOException e){
                Toast.makeText(getBaseContext(), "La conexion fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}
