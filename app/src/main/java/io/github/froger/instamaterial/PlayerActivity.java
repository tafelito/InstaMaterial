package io.github.froger.instamaterial;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.SongArrangementFragment;

public class PlayerActivity extends FragmentActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new SongArrangementFragment()).commit();
		}
	}
	
}
