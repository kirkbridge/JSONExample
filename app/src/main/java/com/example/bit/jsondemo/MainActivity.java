package com.example.bit.jsondemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected ArrayList<ListItem> games;
    protected ListItemAdapter listAdapter;
    protected ListView lvSchedule;
    protected Button btnLoadGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadGames = (Button) findViewById(R.id.btnLoadGame);

        games = new ArrayList<ListItem>();

        lvSchedule = (ListView) findViewById(R.id.lvSchedule);

        lvSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem item = listAdapter.getItem(i);
                Toast.makeText(MainActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void loadGames(View v)
    {
        ProcessJson task = new ProcessJson();

        task.execute();
    }

    class ProcessJson extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            String home = null;
            String away = null;
            String gameDate = null;
            String arena = null;
            String time = null;
            try {
                // Need to create a inputstream to read in the file, file is located in the asset folder
                InputStream stream = getAssets().open("schedule.json");

                // Create the JSONReader which will use the input stream created previously
                JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));

                // The first element in the file is an object
                reader.beginObject();
                // iterate through the object till there are no more elements
                while (reader.hasNext())
                {   // get the name of the property
                    String name = reader.nextName();

                    // find the first element (only element) in the object
                    if(name.equals("gameentry"))
                    {
                        // the games are stored in an array so we must call beginArray
                        reader.beginArray();
                        while(reader.hasNext())
                        {
                            // We are now at the game object
                            reader.beginObject();
                            while(reader.hasNext())
                            {
                                // iterate through game object to get values
                                name = reader.nextName();
                                if(name.equals("date"))
                                {
                                    gameDate = reader.nextString();
                                }
                                else if (name.equals("time"))
                                {
                                    time = reader.nextString();
                                }
                                else if (name.equals("awayTeam"))
                                {
                                    // awayteam is an embedded object in the game so lets call a method
                                    // to process the away team
                                    away = getTeamName(reader);
                                }
                                else if (name.equals("homeTeam"))
                                {
                                    // hometeam is an embedded object in the game so lets call a method
                                    // to process the home team
                                    home = getTeamName(reader);
                                }
                                else if (name.equals("location"))
                                {
                                    arena = reader.nextString() ;
                                }
                                else
                                {
                                    // if the name of the property doesn't match any of the values we want then skip it
                                    reader.skipValue();
                                }
                            }
                            // we have reached the end of the game object we must end it now
                            reader.endObject();
                            // add our new game to our list
                            games.add(new ListItem(home, away, gameDate, arena, time));
                        }
                        // all the games have been processed we can close the array
                        reader.endArray();

                    }

                }
                // reached the end of the root object
                reader.endObject();

                // close the reader we are done processing the file
                reader.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }

            return games;
        }

        /**
         * Description: Process the team object
         * @param reader JsonReader that is reading the elements in the file
         * @return  return the team name
         * @throws IOException
         */
        private String getTeamName(JsonReader reader) throws IOException
        {
            String team = null;
            String name;

            // the team is a new object so we have to open it
            reader.beginObject();
            while(reader.hasNext())
            {
                // iterate through properties in the object till the abbreviated
                // name property is found
                name = reader.nextName();
                if(name.equals("Abbreviation"))
                {
                    team = reader.nextString();
                }
                else
                {
                    // skip ever other property as we do not need it
                    reader.skipValue();
                }
            }
            // close the object
            reader.endObject();
            // return the abbreviated team name.
            return team;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            listAdapter = new ListItemAdapter(MainActivity.this, R.layout.list_item, games) ;
            lvSchedule.setAdapter(listAdapter);

            Toast.makeText(MainActivity.this, "There are " + games.size() + " games tonight", Toast.LENGTH_LONG).show();
        }
    }


    class ListItemAdapter extends ArrayAdapter<ListItem> {
        private ArrayList<ListItem> items;

        public ListItemAdapter(Context context, int textViewResourceId, ArrayList<ListItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            ListItem item = items.get(position);
            if(item != null)
            {
                TextView tvHome = (TextView) v.findViewById(R.id.tvHome);
                TextView tvAway = (TextView) v.findViewById(R.id.tvAway);
                TextView tvArena = (TextView) v.findViewById(R.id.tvArena);
                TextView tvTime = (TextView) v.findViewById(R.id.tvTime);

                if(tvHome != null)
                {
                    tvHome.setText(item.getHomeTeam());
                }
                if(tvAway != null)
                {
                    tvAway.setText(item.getAwayTeam());
                }
                if(tvArena != null)
                {
                    tvArena.setText(item.getArena());
                }
                if(tvTime != null)
                {
                    tvTime.setText(item.getGameDate() + " @ " + item.getTime());
                }
            }

            return v;
        }

    }
}

