package com.ctwings.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class sendEmail extends AppCompatActivity {
    private EditText subject;
    private EditText message;
    private Button button;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        subject = (EditText) findViewById(R.id.editText_subject);
        message = (EditText) findViewById(R.id.editText_message_mail);
        button = (Button) findViewById(R.id.button_send_email);
        db= DatabaseHelper.getInstance(this);

        createCSV();

        //Default Text
        subject.setText("(Movil) backup PDA "+db.get_config_id_pda());
        message.setText("Adjunto se envia registros consultados hasta la fecha " + getCurrentDateTime());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!subject.getText().toString().isEmpty()) {
                    if (!message.getText().toString().isEmpty()) {
                        emailFill();
                    } else
                        message.setError("Este campo es requerido");
                } else
                    subject.setError("Este campo es requerido");

            }
        });

    }

    protected void emailFill() {
        String[] TO = {"support@axxezo.com"};
        File Consulted = new File(Environment.getExternalStorageDirectory()+ File.separator + "ExportRegisters.csv");
        Uri path= Uri.fromFile(Consulted);
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());


        try {
            startActivity(Intent.createChooser(emailIntent, "Seleccione Cliente Email"));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No existe Cliente de Correo Instalado en el Equipo.", Toast.LENGTH_LONG).show();
        }
    }

    public String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        return date.format(currentLocalTime);
    }

    //new async task for file export to csv
    public void createCSV() {
        List<Record> list = db.getRecordsDump(); //My Method to get all data from database

        if (!list.isEmpty()) {
            File productsConsulted = new File(this.getFilesDir() + File.separator + "ExportRegisters.csv");
            if (!productsConsulted.isFile()) {
                try {
                    productsConsulted.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (productsConsulted.exists()) {
                CsvFileWriter.writeCsvFile(this,list);
            }
        } else
            Toast.makeText(this, "No existen datos consultados", Toast.LENGTH_LONG).show();
    }
}
