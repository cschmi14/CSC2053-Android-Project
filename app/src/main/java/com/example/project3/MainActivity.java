package com.example.project3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

        mRecyclerView = findViewById(R.id.main_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PokemonAdapter(this, pokeList);
        mRecyclerView.setAdapter(mAdapter);

        for (int i = 1; i <= 151; i++) {
            load_data(i);
        }


    }

    private void load_data(int i) {
        @SuppressLint("StaticFieldLeak")
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
//                    Pokemon pkmn = parseJson(jsonString);
//                    Log.d("name", pkmn.getName());
//                    pokeList.add(pkmn);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                return jsonString;
            }

            @Override
            protected void onPostExecute(String x) {
                Pokemon pkmn = parseJson(x);
                Log.d("TYPEA", pkmn.getTypeA());
                if (pkmn.getTypeB() != null)
                    Log.d("TYPEB", pkmn.getTypeB());
                pokeList.add(pkmn);
                mAdapter.notifyDataSetChanged();
            }

            private Pokemon parseJson(String json) {
                try {
                    JSONObject json_name = new JSONObject(json);
                    Pokemon pkmn = new Pokemon();
                    String pkmn_name = json_name.getString("name");
                    pkmn.setName(pkmn_name);

                    JSONArray json_stats = json_name.getJSONArray("stats");
                    int[] stats = new int[6];
                    for (int i = 0; i < 6; i++) {
                        JSONObject stat_cat = json_stats.getJSONObject(i);
                        String stat_value = stat_cat.getString("base_stat");
                        stats[i] = Integer.parseInt(stat_value);
                    }
                    pkmn.setStats(stats);

                    JSONArray typeArr = json_name.getJSONArray("types");
                    JSONObject firstTypeArr = typeArr.getJSONObject(0);
                    String firstType = firstTypeArr.getJSONObject("type").getString("name");
                    pkmn.setTypeA(firstType);

                    if (typeArr.length() == 2)
                    {
                        JSONObject secondTypeArr = typeArr.getJSONObject(1);
                        String secondType = secondTypeArr.getJSONObject("type").getString("name");
                        pkmn.setTypeB(secondType);
                    }

                    JSONObject sprites = json_name.getJSONObject("sprites");
                    String front_default = sprites.getString("front_default");
                    pkmn.setIcon(front_default);

                    String dexNumber = json_name.getString("id");
                    pkmn.setDexNumber(Integer.parseInt(dexNumber));
                    return pkmn;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute(Integer.toString(i));
    }
}