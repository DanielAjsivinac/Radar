package com.example.microcontroladores;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class DispositivosBT extends AppCompatActivity {

    private static final String TAG  ="Dispositivos BT";
    ListView listaDispositivosBT;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter miBT;
    private ArrayAdapter misDispositivosVinculados;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_b_t);
    }

    @Override
    protected void onResume() {
        super.onResume();

        VerificarEstadoBT();
        misDispositivosVinculados = new ArrayAdapter(this, R.layout.dispositivos_encontrados);
        listaDispositivosBT = (ListView) findViewById(R.id.listaDispositivosBT);
        listaDispositivosBT.setAdapter(misDispositivosVinculados);
        listaDispositivosBT.setOnItemClickListener(miDispositivoClickListener);
        miBT = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosVinculados = miBT.getBondedDevices();

        if(dispositivosVinculados.size()>0){
            for (BluetoothDevice device : dispositivosVinculados){
                misDispositivosVinculados.add(device.getName()+"\n"+device.getAddress());
            }
        }
    }

    private AdapterView.OnItemClickListener miDispositivoClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address =  info.substring(info.length()-17);
            finishAffinity();

            Intent newIntent  =  new Intent(DispositivosBT.this, MainActivity.class);
            newIntent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(newIntent);
        }
    };

    private void VerificarEstadoBT(){
        miBT = BluetoothAdapter.getDefaultAdapter();
        if(miBT==null){
            Toast.makeText(getBaseContext(),"El dispositivo no soporta Bluetooth", Toast.LENGTH_LONG).show();
        }else{
            if(miBT.isEnabled()){
                Log.d(TAG,"Bluetooth activado");

            }else{
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,1);
            }
        }
    }
}