package pt.ulisboa.tecnico.peromas.peromas.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.Map;

import pt.ulisboa.tecnico.peromas.peromas.MainActivity;
import pt.ulisboa.tecnico.peromas.peromas.R;
import pt.ulisboa.tecnico.peromas.peromas.model.BeaconWithDate;

public class BeaconsDetectedAdapter extends BaseAdapter{

    private MainActivity activity;
    private Map<String, BeaconWithDate> beacons;

    public BeaconsDetectedAdapter(MainActivity activity, Map<String, BeaconWithDate> beacons) {
        this.activity = activity;
        this.beacons = beacons;
    }

    public void setBeacons(Map<String, BeaconWithDate> beacons){
        this.beacons = beacons;
    }

    @Override
    public int getCount() {
        return this.beacons.size();
    }

    @Override
    public Object getItem(int position) {

        String key = (String)beacons.keySet().toArray()[position];
        return beacons.get(key).getBeacon();
        //return this.beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row,
                    parent, false);
            holder = new ViewHolder();

            holder.ID1 = (TextView) convertView.findViewById(R.id.ID1);
            holder.ID2 = (TextView) convertView.findViewById(R.id.ID2);
            holder.ID3 = (TextView) convertView.findViewById(R.id.ID3);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Beacon beacon = (Beacon)getItem(position);
        Log.i("teste", beacon.getId1() + "");
        Log.i("teste2", beacon.getId2() + "");
        holder.ID1.setText(beacon.getId1() + "");
        holder.ID2.setText(beacon.getId2() + "");
        holder.ID3.setText(beacon.getId3() + "");

        holder.distance.setText(beacon.getDistance() + "");


        return convertView;
    }


    static class ViewHolder {

        TextView ID1;
        TextView ID2;
        TextView ID3;
        TextView distance;
    }

}
