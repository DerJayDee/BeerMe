package de.der_jay.beerme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity of BeerMe.
 * 
 * @author JDegenhardt
 * 
 */
public class BeerMe extends Activity {

	/** String for accessing the SharedPreferences set for SMALL BEER */
	private final String SMALL_BEER = "de.der_jay.beerme.SMALL_BEER";

	/** String for accessing the SharedPreferences set for LARGE BEER */
	private final String LARGE_BEER = "de.der_jay.beerme.LARGE_BEER";

	/** String for accessing the SharedPreferences set for LAST_CHANGED */
	private final String LAST_CHANGED = "de.der_jay.beerme.LAST_CHANGED";

	/** Counter for the amount of smallBeer */
	private int smallBeer;

	/** Counter for the amount of largeBeer */
	private int largeBeer;

	/**
	 * Displayes what was last changed: 0 --> nothing to undo, 1 --> small beer,
	 * 2 --> large beer
	 */
	private int lastChanged;

	/**
	 * This Method creates the Activity and sets the variables either 0 or the
	 * value they were set to the last time that this Activity was run.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.beerme);

		SharedPreferences sp = getPreferences(MODE_PRIVATE);

		smallBeer = sp.getInt(SMALL_BEER, 0);
		largeBeer = sp.getInt(LARGE_BEER, 0);
		lastChanged = sp.getInt(LAST_CHANGED, 0);

		if (savedInstanceState != null) {
			smallBeer = savedInstanceState.getInt(SMALL_BEER);
			largeBeer = savedInstanceState.getInt(LARGE_BEER);
			lastChanged = savedInstanceState.getInt(LAST_CHANGED);
		}

		TextView tw = (TextView) findViewById(R.id.small_beer_count);
		tw.setText("" + smallBeer);
		tw = (TextView) findViewById(R.id.large_beer_count);
		tw.setText("" + largeBeer);

		if (lastChanged == 0) {
			disableUndoButton();
		}
		if(smallBeer == 0 && largeBeer == 0){
			disableResetButton();
		}
	}

	/**
	 * This Method is called whenever the Activity is paused, exited or
	 * destroyed. It saves the current set variables in the SharedPreferences.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(LARGE_BEER, largeBeer);
		editor.putInt(SMALL_BEER, smallBeer);
		editor.putInt(LAST_CHANGED, lastChanged);
		editor.commit();
	}

	/**
	 * This Method is called whenever the Activity has to be recreated due to
	 * switching to landscape or something like that. It saves the current
	 * variables to the Bundle that the onCreate-Method will receive when the
	 * Activity is being rebuild.
	 */
	@Override
	protected void onSaveInstanceState(Bundle status) {
		super.onSaveInstanceState(status);
		status.putInt(SMALL_BEER, smallBeer);
		status.putInt(LARGE_BEER, largeBeer);
		status.putInt(LAST_CHANGED, lastChanged);
	}

	/**
	 * Method that is called if the Button with the ID 'id/button_small_beer' is
	 * clicked.
	 * 
	 * @param view
	 */
	public void addSmallBeer(View view) {
		smallBeer++;
		lastChanged = 1;
		setTextView(R.id.small_beer_count, "" + smallBeer);
		enableButtons();
	}

	/**
	 * Method that is called if the Button with the ID 'id/button_large_beer' is
	 * clicked.
	 * 
	 * @param view
	 */
	public void addLargeBeer(View view) {
		largeBeer++;
		lastChanged = 2;
		setTextView(R.id.large_beer_count, "" + largeBeer);
		enableButtons();
	}

	public void undo(View view) {
		switch (lastChanged) {
		case 1:
			smallBeer--;
			setTextView(R.id.small_beer_count, "" + smallBeer);
			lastChanged = 0;
			break;
		case 2:
			largeBeer--;
			setTextView(R.id.large_beer_count, "" + largeBeer);
			lastChanged = 0;
			break;
		}
		postToast(getString(R.string.undone));
		disableUndoButton();
		if(smallBeer == 0 && largeBeer == 0){
			disableResetButton();
		}
	}

	/**
	 * Method that is called if the Button with the ID 'id/button_reset' is
	 * clicked.
	 * 
	 * @param view
	 */
	public void reset(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_text);
		builder.setPositiveButton(R.string.dialog_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						smallBeer = 0;
						largeBeer = 0;
						setTextView(R.id.large_beer_count, "" + largeBeer);
						setTextView(R.id.small_beer_count, "" + smallBeer);
						disableButtons();
						postToast(getString(R.string.reseted));
					}
				});
		builder.setNegativeButton(R.string.dialog_disconfirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Method for displaying Toasts showing the String s.
	 * 
	 * @param s
	 *            The String that should be shown in the Toast.
	 */
	private void postToast(String s) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, s, duration);
		toast.show();
	}

	/**
	 * Method for setting the text in a TextView wihthe ID textViewID to the
	 * String s.
	 * 
	 * @param textViewID
	 *            The ID of the TextView that the String s should be set to.
	 * @param s
	 *            The String that should be displayed in the TextView.
	 */
	private void setTextView(int textViewID, String s) {
		TextView tw = (TextView) findViewById(textViewID);
		tw.setText(s);
	}
	
	/**
	 * This Method enables the Undo-Button.
	 */
	private void enableUndoButton(){
		Button btn = (Button)findViewById(R.id.button_undo);
		btn.setEnabled(true);
	}

	/**
	 * This Method disables the Undo-Button.
	 */
	private void disableUndoButton(){
		Button btn = (Button)findViewById(R.id.button_undo);
		btn.setEnabled(false);
	}
	
	/**
	 * This Method enables the Reset-Button.
	 */
	private void enableResetButon(){
		Button btn = (Button)findViewById(R.id.button_reset);
		btn.setEnabled(true);
	}
	
	/**
	 * This Method disables the Reset-Button.
	 */
	private void disableResetButton(){
		Button btn = (Button)findViewById(R.id.button_reset);
		btn.setEnabled(false);
	}
	
	/**
	 * This Method enables the Undo- and the Reset-Button.
	 */
	private void enableButtons(){
		enableResetButon();
		enableUndoButton();
	}
	
	/**
	 * This Method disables the Undo- and the Reset-Button.
	 */
	private void disableButtons(){
		disableResetButton();
		disableUndoButton();
	}
}