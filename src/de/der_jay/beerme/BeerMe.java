package de.der_jay.beerme;

import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity of BeerMe.
 * 
 * @author JDegenhardt
 * 
 */
public class BeerMe extends Activity {

	/** String for accessing the SharedPreferences set for CHANGES */
	private final String CHANGES = "de.der_jay.beerme.CHANGES";

	/** String for accessing the SharedPreferences set for LARGE BEER */
	private final String LARGE_BEER = "de.der_jay.beerme.LARGE_BEER";

	/** String for accessing the SharedPreferences set for SMALL BEER */
	private final String SMALL_BEER = "de.der_jay.beerme.SMALL_BEER";

	/**
	 * Displayes the change history: 0 --> nothing to undo, 1 --> small beer, 2
	 * --> large beer
	 */
	private Stack<Integer> changes;

	/** Counter for the amount of largeBeer */
	private int largeBeer;

	/** Counter for the amount of smallBeer */
	private int smallBeer;

	/**
	 * This Method creates the Activity and sets the variables either 0 or the
	 * value they were set to the last time that this Activity was run.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changes = new Stack<Integer>();

		setContentView(R.layout.beerme);

		SharedPreferences sp = getPreferences(MODE_PRIVATE);

		smallBeer = sp.getInt(SMALL_BEER, 0);
		largeBeer = sp.getInt(LARGE_BEER, 0);
		setChanges(sp.getString(CHANGES, ""));

		if (savedInstanceState != null) {
			smallBeer = savedInstanceState.getInt(SMALL_BEER);
			largeBeer = savedInstanceState.getInt(LARGE_BEER);
			setChanges(savedInstanceState.getString(CHANGES));
		}

		TextView tw = (TextView) findViewById(R.id.small_beer_count);
		tw.setText("" + smallBeer);
		tw = (TextView) findViewById(R.id.large_beer_count);
		tw.setText("" + largeBeer);

		if (changes.isEmpty()) {
			changes.push(0);
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
		editor.putString(CHANGES, createChanges());
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
		status.putString(CHANGES, createChanges());
	}

	/**
	 * This Method is called when the Options Menu is created for the first
	 * time.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/**
	 * This Method is called whenever "invalidateOptionsMenu()" gets called.
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		// UNDO
		menu.getItem(0).setEnabled(
				!changes.isEmpty() && changes.lastElement() != 0);
		// RESET
		menu.getItem(1).setEnabled(smallBeer != 0 || largeBeer != 0);
		return true;
	}

	/**
	 * Event Handling for Individual menu item selected Identify single menu
	 * item by it's ID.
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_undo:
			undo();
			return true;
		case R.id.menu_reset:
			reset();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method that is called if the Button with the ID 'id/button_large_beer' is
	 * clicked.
	 * 
	 * @param view
	 */
	public void addLargeBeer(View view) {
		largeBeer++;
		changes.push(2);
		setTextView(R.id.large_beer_count, "" + largeBeer);
		invalidateOptionsMenu();
	}

	/**
	 * Method that is called if the Button with the ID 'id/button_small_beer' is
	 * clicked.
	 * 
	 * @param view
	 */
	public void addSmallBeer(View view) {
		smallBeer++;
		changes.push(1);
		setTextView(R.id.small_beer_count, "" + smallBeer);
		invalidateOptionsMenu();
	}

	/**
	 * This Method is called, when the Option R.id.menu_reset has been selected.
	 */
	private void reset() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_text);
		builder.setPositiveButton(R.string.dialog_confirm,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog, int id) {
						smallBeer = 0;
						largeBeer = 0;
						changes.clear();
						changes.push(0);
						setTextView(R.id.large_beer_count, "" + largeBeer);
						setTextView(R.id.small_beer_count, "" + smallBeer);
						postToast(getString(R.string.reseted));
						invalidateOptionsMenu();
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
	 * This Method is called, when the Option R.id.menu_undo has been selected.
	 */
	private void undo() {
		switch (changes.pop()) {
		case 1:
			smallBeer--;
			setTextView(R.id.small_beer_count, "" + smallBeer);
			break;
		case 2:
			largeBeer--;
			setTextView(R.id.large_beer_count, "" + largeBeer);
			break;
		}
		postToast(getString(R.string.undone));
		invalidateOptionsMenu();
	}

	/**
	 * This Method constructs a String from all the Integers stacked in the
	 * changes-Stack.
	 * 
	 * @return
	 */
	private String createChanges() {
		String s = "";
		while (!changes.isEmpty()) {
			s += changes.pop();
		}
		setChanges(s);
		return s;
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
	 * This Method parses the String s to Integer values that represent the
	 * change history and pushes the Integers into the changes-Stack.
	 * 
	 * @param s
	 */
	private void setChanges(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			changes.push(Integer.parseInt("" + s.charAt(i)));
		}
	}

	/**
	 * Method for setting the text in a TextView wih the ID textViewID to the
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
}