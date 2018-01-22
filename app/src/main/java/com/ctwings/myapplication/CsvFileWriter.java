package com.ctwings.myapplication;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by jmora on 20/11/2017.
 */

public class CsvFileWriter {

    //Delimiter used in CSV file
    private static final String DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = System.getProperty("line.separator");;

    //CSV file header
    private static final String FILE_HEADER = "ID;Mongo ID; RUN ; Tipo Registro ;Fecha ;Sincronizado?";

    public static void writeCsvFile(Context context,List<Record> records) {

        File productsConsulted = new File(Environment.getExternalStorageDirectory() + File.separator + "ExportRegisters.csv");



        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(productsConsulted);

            //Write the CSV file header
            fileWriter.append(FILE_HEADER);

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new student object list to the CSV file
            for (Record record : records) {
                fileWriter.append(record.getRecord_id()+"");
                fileWriter.append(DELIMITER);
                fileWriter.append(record.getPerson_mongo_id());
                fileWriter.append(DELIMITER);
                fileWriter.append(record.getRecord_person_rut());
                fileWriter.append(DELIMITER);
                fileWriter.append(record.getRecord_type());
                fileWriter.append(DELIMITER);
                fileWriter.append(record.getRecord_date()+"");
                fileWriter.append(DELIMITER);
                fileWriter.append(record.getRecord_sync()+"");
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
            Toast.makeText(context,"El archivo CSV ha sido creado Satisfactoriamente.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            System.out.println("Error al intentar armar el CSV");
            e.printStackTrace();
        } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.flush();
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.getStackTrace();
                }
        }
    }
}