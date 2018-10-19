package yzq.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import yzq.com.myapplication.view.MyView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      final  MyView view=findViewById(R.id.view);
      final Runnable runnable=new Runnable() {
          @Override
          public void run() {
            view.addImageView();
          }
      };
      new Thread(){
          @Override
          public void run() {
              while (true){
                  try {
                      Thread.sleep(1000);
                      runOnUiThread(runnable);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

              }
          }
      }.start();

    }
}
