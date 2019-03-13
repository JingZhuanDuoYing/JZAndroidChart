package cn.jingzhuan.lib.chart2.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DemoAdapterController controller = new DemoAdapterController(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setAdapter(controller.getAdapter());

        controller.requestModelBuild();
    }
}
