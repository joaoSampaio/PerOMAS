package pt.ulisboa.tecnico.peromas.peromas;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PerOMASPreferencesFragment extends PreferenceFragment {

       public PerOMASPreferencesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings);

    }


    @Override
    public void onResume() {
        super.onResume();
           }

    @Override
    public void onPause() {
        super.onPause();
            }


}
