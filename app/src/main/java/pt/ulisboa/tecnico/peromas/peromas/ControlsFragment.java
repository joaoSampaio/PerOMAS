package pt.ulisboa.tecnico.peromas.peromas;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ControlsFragment extends Fragment {

	public ControlsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_controls, container, false);
         
        return rootView;
    }
}
