package pt.ulisboa.tecnico.peromas.peromas;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sampaio on 27-06-2015.
 */
public class CheckCredencialsPreference extends Preference {

    private TextView status_service;
    private boolean check = false;
    private String color = null;

    public CheckCredencialsPreference(Context context) {
        super(context);
    }

    /*public ServiceStartPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    public CheckCredencialsPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckCredencialsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
// constructors

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Log.d("teste", "onBindView: ");

        status_service = (TextView) view.findViewById(R.id.status_service);
        if(color != null){
            changeColor(color);
        }
        check = true;
    }

    public TextView getTextView() {
        return status_service;
    }

    public void setColor(String color){
        this.color = color;
    }

    public void is(){
        Log.d("teste", "is: " + check);
    }

    private void changeColor(String color){
        GradientDrawable bgShape = (GradientDrawable)status_service.getBackground();
        bgShape.setColor(Color.parseColor(color));
        if(color.equals(Constants.RED))
            status_service.setText("OFF");
        if(color.equals(Constants.GREEN)) {
            status_service.setText("ON");
        }
    }
}
