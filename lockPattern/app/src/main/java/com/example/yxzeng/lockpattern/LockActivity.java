package com.example.yxzeng.lockpattern;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        SharedPreferences sp = getSharedPreferences("password", MODE_PRIVATE);
        final String password = sp.getString("password", "");

        final GestureLock lock = (GestureLock)findViewById(R.id.view);
        lock.setOnDrawFinishedListener(new GestureLock.OnDrawFinishedListener() {
            @Override
            public boolean OnDrawFinished(List<Integer> passList) {
                StringBuilder sb = new StringBuilder();
                for(Integer i:passList){
                    sb.append(i);
                }
                if(sb.toString().equals(password)){
                    Toast.makeText(LockActivity.this, "Password correct", Toast.LENGTH_SHORT).show();
                    return true;
                }else{
                    Toast.makeText(LockActivity.this, "Password wrong", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lock, menu);
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
}
