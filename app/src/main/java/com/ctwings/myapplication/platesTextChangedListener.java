package com.ctwings.myapplication;

/**
 * Created by Juan Morales on 15/11/2017.
 */
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

public class platesTextChangedListener implements TextWatcher{
    Context context;

    public platesTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        //Log.e(TAG, "User input: " + userInput);

        DatabaseHelper db = DatabaseHelper.getInstance(context);
        MainActivity mainActivity = ((MainActivity) context);

        // update the adapter
        mainActivity.myAdapter.notifyDataSetChanged();
        mainActivity.myAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line,db.getPlatesFromDb(userInput.toString()));
        //mainActivity.plates.setAdapter(mainActivity.myAdapter);

    }

}
