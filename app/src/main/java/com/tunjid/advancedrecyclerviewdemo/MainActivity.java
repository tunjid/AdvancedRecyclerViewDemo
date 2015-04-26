package com.tunjid.advancedrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements SimpleDraggableSortableAdapter.AdapterListener {

    public final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, TAG + " online.");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onModelObjectRemoved(int position) {
        Toast.makeText(this, "You clicked deleted the object at " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onModelObjectMoved() {
    }

    @Override
    public void onModelObjectSwiped(ModelObject modelObject) {
        String stringToShow;

        if (modelObject.getTitle().charAt(3) == ',') {
            stringToShow = new StringBuilder(modelObject.getTitle()).reverse().toString();
        }

        else {
            stringToShow = modelObject.getTitle();
        }

        Toast.makeText(this, "You swiped " + stringToShow, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onModelObjectClicked(ModelObject modelObject) {
        String stringToShow;

        if (modelObject.getTitle().charAt(3) == ',') {
            stringToShow = new StringBuilder(modelObject.getTitle()).reverse().toString();
        }

        else {
            stringToShow = modelObject.getTitle();
        }
        Toast.makeText(this, "You clicked " + stringToShow, Toast.LENGTH_SHORT).show();
    }
}
