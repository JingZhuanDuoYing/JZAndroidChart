package cn.jingzhuan.lib.chart.demo;

import android.app.Application;
import android.graphics.Color;
import jp.wasabeef.takt.Seat;
import jp.wasabeef.takt.Takt;

/**
 * Application
 * Created by donglua on 12/27/17.
 */

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();
    Takt.stock(this)
        .color(Color.WHITE)
        .seat(Seat.TOP_RIGHT)
        .play();
  }

  @Override public void onTerminate() {
    Takt.finish();
    super.onTerminate();
  }
}
