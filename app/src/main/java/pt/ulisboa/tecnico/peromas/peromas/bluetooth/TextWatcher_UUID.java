package pt.ulisboa.tecnico.peromas.peromas.bluetooth;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by sampaio on 27-06-2015.
 */
public class TextWatcher_UUID implements TextWatcher {
    private EditText edit_uuid;
    public TextWatcher_UUID(EditText edit_uuid){
        this.edit_uuid = edit_uuid;
    }


    public void onTextChanged(CharSequence s, int start,int before, int count)
    {
        // TODO Auto-generated method stub

    }
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTextChanged(Editable s) {
        //if(mBlockCompletion)
            //return;

        String text = edit_uuid.getText().toString();
        String noDash = "";
        if(text.length() > 0){
            noDash = text.replace("-","");
        }
        if(noDash.length() >= 21 ){
            noDash = noDash.substring(0,20) + "-" + noDash.substring(20,noDash.length());
        }
        if(noDash.length() >= 17 ){
            noDash = noDash.substring(0,16) + "-" + noDash.substring(16,noDash.length());
        }
        if(noDash.length() >= 13 ){
            noDash = noDash.substring(0,12) + "-" + noDash.substring(12,noDash.length());
        }
        if(noDash.length() >= 9 ){
            noDash = noDash.substring(0,8) + "-" + noDash.substring(8,noDash.length());
        }

        /*edit_uuid.removeTextChangedListener(this);
        int pos = edit_uuid.getSelectionStart();
        edit_uuid.setText(noDash);
        if(noDash.length() == (8+2) || noDash.length() == (12+3) || noDash.length() == (16+4) || noDash.length() == (20+5)){
            if(noDash.length() >= (pos+1))
                pos +=1;
        }
        if(noDash.length() < (pos))
            pos = noDash.length();
        edit_uuid.setSelection(pos);
        edit_uuid.addTextChangedListener(this);*/

        edit_uuid.removeTextChangedListener(this);
        int pos = edit_uuid.getSelectionStart();
        edit_uuid.getText().clear();
        edit_uuid.append(noDash);
        if(noDash.length() == (8+2) || noDash.length() == (12+3) || noDash.length() == (16+4) || noDash.length() == (20+5)){
            if(noDash.length() >= (pos+1))
                pos +=1;
        }
        if(noDash.length() < (pos))
            pos = noDash.length();
        edit_uuid.setSelection(pos);
        edit_uuid.addTextChangedListener(this);


    }
}
