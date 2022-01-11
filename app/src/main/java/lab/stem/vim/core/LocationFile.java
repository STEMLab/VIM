package lab.stem.vim.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import lab.stem.vim.R;

public class LocationFile {
	private JSONObject destinationData;
	private final SharedPreferences sharedPreferences;
	private final SharedPreferences.Editor editor;
	private InputStream inputStream;

//	public LocationFile(Context context){
//		this.context = context;
//		AssetManager assetManager = context.getAssets();
//		sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
//		editor = sharedPreferences.edit();
//		try {
//			InputStream inputStream = assetManager.open(context.getResources().getString(R.string.destination_file));
//			byte inputByteString[];
//			inputByteString = new byte[inputStream.available()];
//			inputStream.read(inputByteString);
//			JSONObject jsonObject = new JSONObject(new String(inputByteString));
//			this.destinationData = (JSONObject)((JSONObject) jsonObject.get("service_no")).get(context.getResources().getString(R.string.current_service_no));
//			setUpSetting();
//		} catch(IOException | JSONException e) {
//			Log.e("tag", "Failed to copy asset file: " , e);
//		}
//	}

	public LocationFile(String serviceNo, Context context){
		AssetManager assetManager = context.getAssets();
		sharedPreferences = context.getSharedPreferences("setting", Activity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		editor.apply();
		try {
			inputStream = assetManager.open(context.getResources().getString(R.string.location_file));
			byte[] inputByteString;
			inputByteString = new byte[inputStream.available()];
			inputStream.read(inputByteString);
			JSONObject jsonObject = new JSONObject(new String(inputByteString));
			this.destinationData = (JSONObject)((JSONObject) jsonObject.get("service_no")).get(serviceNo);
			setUpSetting();
		} catch(IOException | JSONException e) {
			Log.e("tag", "Failed to copy asset file: " , e);
		}
	}

	public void loadDestination(String serviceNo) {
		try {
			byte[] inputByteString;
			inputByteString = new byte[inputStream.available()];
			inputStream.read(inputByteString);
			JSONObject jsonObject = new JSONObject(new String(inputByteString));
			this.destinationData = (JSONObject)((JSONObject) jsonObject.get("service_no")).get(serviceNo);
			setUpSetting();
		} catch(IOException | JSONException e) {
			Log.e("tag", "Failed to copy asset file: " , e);
		}
	}

	private void setUpSetting() {
		if (sharedPreferences == null) return;
		editor.putBoolean("brailleBlocks", isBrailleBlocks());
		editor.commit();
	}

	public String getName() {
		String name = null;
		if (destinationData == null) return null;
		try {
			name = (String) destinationData.get("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return name;
	}

	public boolean isBrailleBlocks(){
		if (sharedPreferences != null && destinationData != null) {
			boolean braille_blocks;
			try {
				braille_blocks = (Boolean) destinationData.get("braille_blocks");
				return braille_blocks;
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public ArrayList<String> getFloors() {
		if (destinationData != null) {
			JSONObject floors;
			try {
				floors = (JSONObject) destinationData.get("floors");
				ArrayList<String> keys = new ArrayList<>();
				for (Iterator<String> iterator = floors.keys(); iterator.hasNext();) {
					keys.add(iterator.next());
				}
				return keys;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public HashMap<String, String> getAllState(String language) {
		HashMap<String, String> allState = new HashMap<>();
		ArrayList<String> keysOfFloors = getFloors();
		try {
			JSONObject floors = (JSONObject) destinationData.get("floors");

			for (String KeyOfFloor : keysOfFloors) {
				JSONObject floor = (JSONObject) floors.get(KeyOfFloor);

				JSONObject states = (JSONObject)floor.get("states");

				for (Iterator<String> iterator = states.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();

					JSONObject nameOfState = (JSONObject) states.get(key);
					String value = ((JSONObject) nameOfState.get("name")).getString(language);

					allState.put(key, value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return allState.size() > 0 ? allState : null;
	}

	public HashMap<String, DestinationInfo> getAllDestination() {
		HashMap<String, DestinationInfo>allDestination = new HashMap<>();
		ArrayList<String> keysOfFloors = getFloors();
		try {
			JSONObject floors = (JSONObject) destinationData.get("floors");

			for (String KeyOfFloor : keysOfFloors) {
				JSONObject floor = (JSONObject) floors.get(KeyOfFloor);

				JSONObject states = (JSONObject)floor.get("states");

				for (Iterator<String> iterator = states.keys(); iterator.hasNext();) {
					String id = (String) iterator.next();
					if (states.has(id)) {
						JSONObject state = (JSONObject) states.get(id);
						allDestination.put(id, new DestinationInfo(id, state));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return allDestination.size() > 0 ? allDestination : null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return destinationData != null ? destinationData.toString(): "null";
	}
}
