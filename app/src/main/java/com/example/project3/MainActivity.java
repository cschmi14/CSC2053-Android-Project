package com.example.project3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private PokemonAdapter mAdapter;
    private LinkedList<Pokemon> pokeList = new LinkedList<Pokemon>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pokemon Bulbasaur = new Pokemon("aaa");
        pokeList.add(Bulbasaur);
        pokeList.add(new Pokemon("New guy"));
        mRecyclerView = findViewById(R.id.main_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PokemonAdapter(this, pokeList);
        mRecyclerView.setAdapter(mAdapter);
        load_data();
//
    }

    private void load_data() {
        AsyncTask<String, Void, String> task = new AsyncTask <String, Void, String>() {
            protected String getPokemonInfo(String query) throws IOException {
                //Pokemon API URL
                String apiURL = "https://pokeapi.co/api/v2/pokemon/";
                //Append query
                apiURL += query;

                //Make connection to API
                URL requestURL = new URL(apiURL);
                HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Receive the response
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                //Create a String with the response
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                String jsonString = builder.toString();
                return jsonString;
            }

            @Override
            protected String doInBackground(String... strings) {
                String jsonString = null;
                try {
                    jsonString = getPokemonInfo(strings[0]);
                    Pokemon pkmn = parseJson(jsonString);
                    pokeList.add(pkmn);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                return jsonString;
            }

            @Override
            protected void onPostExecute(String x) {
                mAdapter.notifyDataSetChanged();
                String mName = null;
//        String mDescription = null;
//        String mType = null;
//        String mDexNumber = null;
//        String mIcon = null;
//        String[] mStats = null;
                JSONObject jsonObject = null;
                JSONArray itemsArray = null;
                Log.d("FetchBookTagJsonString", x);

//        int i = 0;
                try {
                    JSONObject object = new JSONObject(x);
                    Log.d("Object name", object.getString("name"));
                    Pokemon pk = new Pokemon(object.getString("name"));
                    pokeList.add(pk);
//                    JSONObject name = object.getJSONObject("name");
//            while (i<itemsArray.length() && mName == null /* && mDescription == null && mType == null && mDexNumber == null && mIcon == null && mStats == null */) {
//                JSONObject pokemon = itemsArray.getJSONObject(i);
//                JSONObject dex = pokemon.getJSONObject("dex");
//                title = volumeInfo.getString("title");
//                author = volumeInfo.getString("authors");
//                mName.get().setText(mName);
//                mTitleText.get().setText(title);
//                i++;
//            }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private Pokemon parseJson(String json) {
                try {
                    JSONObject json_name = new JSONObject(json);
                    String pkmn_name = json_name.getString("name");
                    Pokemon pkmn = new Pokemon(pkmn_name);
                    return pkmn;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        for (int i = 1; i < 11; i++) {
            task.execute(Integer.toString(i));
        }
    }
}