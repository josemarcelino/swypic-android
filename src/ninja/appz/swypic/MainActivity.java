package ninja.appz.swypic;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get cocktails JSON
    private static String url = "http://appzninja.io/swypic/api/populate.json";

    // JSON Node names
    private static final String TAG_POSTS = "posts";
    private static final String TAG_ID = "id";
    private static final String TAG_PHOTO = "photoUrl";
    private static final String TAG_DESC = "desc";
    private static final String TAG_DATE = "createdDate";
    private static final String TAG_UP = "upVote";
    private static final String TAG_DOWN = "downVote";
    private static final String TAG_TIMER = "timer";

    // posts JSONArray
    JSONArray posts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> postsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postsList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        // Calling async task to get json
        new GetPosts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetPosts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            swypicAPI sh = new swypicAPI();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, swypicAPI.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    posts = jsonObj.getJSONArray(TAG_POSTS);

                    // looping through All Contacts
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject c = posts.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String photoUrl = c.getString(TAG_PHOTO);
                        String desc = c.getString(TAG_DESC);
                        String createdDate = c.getString(TAG_DATE);

                        // tmp hashmap for single contact
                        HashMap<String, String> posts = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        posts.put(TAG_ID, id);
                        posts.put(TAG_PHOTO, photoUrl);
                        posts.put(TAG_DESC, desc);
                        posts.put(TAG_DATE, createdDate);

                        // adding posts to posts list
                        postsList.add(posts);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, postsList,
                    R.layout.list_item, new String[] { TAG_PHOTO, TAG_DESC,
                    TAG_DATE }, new int[] { R.id.photoUrl,
                    R.id.desc, R.id.createdDate });

            setListAdapter(adapter);
        }

    }
}