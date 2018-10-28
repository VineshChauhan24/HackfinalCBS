package org.thosp.HackCBS;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ledControl extends AppCompatActivity {

    Button btnDis, btnReceive, btnReceiveFirebaseData;
    String address = null;
    TextView lumn;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    StringBuilder sb = new StringBuilder();

    NewThread newThread;

    //firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int rpm;
    Double probability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Rohini");

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_led_control);

        btnReceive = (Button) findViewById(R.id.btn_receive);
        btnReceiveFirebaseData = findViewById(R.id.btn_receive_fb_data);
        btnDis = findViewById(R.id.button4);

        btnReceiveFirebaseData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiveFirebaseData();

            }
        });

        lumn = (TextView) findViewById(R.id.textView2);

        new ConnectBT().execute();



        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //receiveSignal();
                newThread = new NewThread(ledControl.this);

                newThread.start();
            }
        });
    }

    private void receiveFirebaseData() {

        sendSignal("connecting.....................");
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                rpm = dataSnapshot.child("rpm").getValue(Integer.class);
                probability = dataSnapshot.child("probability").getValue(Double.class);
             //   Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
               // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




    }

    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private char sendChar(String c){

        if (btSocket!=null){

            try {
                btSocket.getOutputStream().write(c.toString().getBytes());
            }catch (IOException e){
                msg("Error");
            }
        }

        return '0';


    }


    class NewThread extends Thread {

        Context context;

        public NewThread(Context context){

            this.context = context;

        }


        @Override
        public void run() {

            receiveSignal();


        }
    }



    private void receiveSignal() {

        //INTENT_________________________________________________________________________
//        Intent i = new Intent(ledControl.this,dangerActivity.class);
  //      startActivity(i);


        InputStream inputStream = null;

        if (btSocket != null) {
            try {
                inputStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] buffer = new byte[1024];
            int bytes;
          //  String incomingMessage = "";
            double speed = 0.0;
            while (true) {

                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    sb.append(incomingMessage);
                    int endOfLineIndex = sb.indexOf("\r\n");
                    // speed = 2.14 * Integer.parseInt(incomingMessage) * 0.001885;
                    Log.e("TAG", "Incoming Message : " + incomingMessage);
                    if (endOfLineIndex > 0) {

                        String sbPrint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        Log.e("TAG", "Sb Print Data : " + sbPrint);

                        if (Integer.parseInt(sbPrint) > rpm) {

                          //  sendSignal("Overspeeding...................");
                            sendSignal("Overspeeding. Probab :" + probability);
                            sendSignal("1");
                            sendSignal("                                ");


                            this.runOnUiThread(new Runnable() {
                                public void run() {

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ledControl.this);
                                    builder1.setMessage("Overspeeding");
                                    builder1.setCancelable(true);
                                    builder1.show();
                                    builder1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }
                                    });

                                 //   sendSignal("on");
                                   // sendSignal(" ");

                                }
                            });

                            Log.e("TAG", "Speed Zero");

                        } else {


                          //  sendChar("off");
                           // Toast.makeText(this, "OverSpeeding", Toast.LENGTH_SHORT).show();
                        }

                    }
                  //  Toast.makeText(this, "INcoming msg : " + incomingMessage, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        }

    }

    private void showToast() {

 //       Toast.makeText(this, "Overspeeding", Toast.LENGTH_SHORT).show();
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
}
