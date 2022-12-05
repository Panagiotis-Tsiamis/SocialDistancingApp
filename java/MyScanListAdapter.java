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

public class MyScanListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> mAndroidIDArray;
    private ArrayList<Integer> mRssisArray;
    private ArrayList<String> mWhiteListArray;
    private Context mContext;

    public MyScanListAdapter(ArrayList<String> androidIDArray, ArrayList<Integer> rssisArray,
                             ArrayList<String> whiteListArray, Context context) {
        this.mAndroidIDArray = androidIDArray;
        this.mRssisArray = rssisArray;
        this.mWhiteListArray = whiteListArray;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return mAndroidIDArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View mView = convertView;

        if (mView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(R.layout.scanlist_item_layout, null);
        }

        TextView elemAndroidID = (TextView) mView.findViewById(R.id.androidID_text);
        TextView elemRssi = (TextView) mView.findViewById(R.id.rssi_text);
        ImageButton elemAddButton = (ImageButton) mView.findViewById(R.id.add_button);

        if(mAndroidIDArray.get(0) != "N/A") {
            elemAndroidID.setText("Android ID : " + mAndroidIDArray.get(position));
            elemRssi.setText("Rssi value : " + String.valueOf(mRssisArray.get(position)));
            if(!mWhiteListArray.contains(mAndroidIDArray.get(position))){
                elemAddButton.setClickable(true);
                elemAddButton.setVisibility(View.VISIBLE);
                elemAddButton.setImageResource(R.mipmap.ic_add_to_list_large);
            }
            else {
                elemAddButton.setClickable(false);
                elemAddButton.setVisibility(View.VISIBLE);
                elemAddButton.setImageResource(R.mipmap.ic_list_checked);
            }
        }
        else {
            elemAndroidID.setText("No devices found");
            elemRssi.setText("");
            elemAddButton.setClickable(false);
            elemAddButton.setVisibility(View.INVISIBLE);
        }

        elemAddButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                if(!mWhiteListArray.contains(mAndroidIDArray.get(position))) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Add to whitelist")
                            .setMessage("Do you want to add this device to whitelist?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ImageButton tmpElemAddButton = (ImageButton) v.findViewById(R.id.add_button);
                                    try {
                                        MyScanListAdapter.this.mWhiteListArray.add(mAndroidIDArray.get(position));
                                        tmpElemAddButton.setClickable(false);
                                        tmpElemAddButton.setImageResource(R.mipmap.ic_list_checked);
                                        Toast.makeText(mContext, "Added to whitelist", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
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
            }
        });

        return mView;
    }
}
