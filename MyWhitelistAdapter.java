package com.example.social_distancing_assistant;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class MyWhitelistAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> myArray;
    private Context myContext;

    public MyWhitelistAdapter(ArrayList<String> array, Context context) {
        this.myArray = array;
        this.myContext = context;
    }

    @Override
    public int getCount() {
        return myArray.size();
    }

    @Override
    public String getItem(int position) {
        return myArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View mView = convertView;

        if (mView == null) {
            LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(R.layout.whitelist_item_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listElem = (TextView) mView.findViewById(R.id.list_elem_text);
        listElem.setText(myArray.get(position));

        //Handle buttons and add onClickListeners
        ImageButton myDeleteButton = (ImageButton) mView.findViewById(R.id.delete_btn);

        myDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Remove device")
                        .setMessage("Do you want to remove this device from the whitelist?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    MyWhitelistAdapter.this.myArray.remove(MyWhitelistAdapter.this.getItem(position));
                                    MyWhitelistAdapter.this.notifyDataSetChanged();
                                    Toast.makeText(myContext, "Removed from whitelist", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(myContext, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        return mView;
    }
}
