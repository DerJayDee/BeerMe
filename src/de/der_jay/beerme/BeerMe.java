package de.der_jay.beerme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class BeerMe extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beerme);
	}
	
    
	public void addSmallBeer(View view){
		System.out.println("+small beer");
	}
	
	public void addLargeBeer(View view){
		System.out.println("+large beer");
	}
	
	protected void reset(View view){
		System.out.println("rese all counters");
	}
}