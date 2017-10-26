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
            try {
                InputStream stream = getAssets().open("schedule.json");
                JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
                reader.beginObject();
                while (reader.hasNext())
                {   String name = reader.nextName();
                    if(name.equals("gameentry"))
                    {
                        reader.beginArray();
                        while(reader.hasNext())
                        {
                            reader.beginObject();
                            while(reader.hasNext())
                            {
                                name = reader.nextName();
                                if(name.equals("date"))
                                {
                                    gameDate = reader.nextString();
                                }
                                else if (name.equals("awayTeam"))
                                {
                                    away = getTeamName(reader);
                                }
                                else if (name.equals("homeTeam"))
                                {
                                    home = getTeamName(reader);
                                }
                                else if (name.equals("location"))
                                {
                                    arena = reader.nextString() ;
                                }
                                else
                                {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                            games.add(new ListItem(home, away, gameDate, arena));
                        }
                        reader.endArray();

                    }

                }
                reader.endObject();
//                reader.endArray();
                reader.close();
            }


            catch(IOException ex)
            {
                ex.printStackTrace();
            }

            return games;
        }

        private String getTeamName(JsonReader reader) throws IOException
        {
            String team = null;
            String name;
            reader.beginObject();
            while(reader.hasNext())
            {
                name = reader.nextName();
                if(name.equals("Abbreviation"))
                {
                    team = reader.nextString();
                }
                else
                {
                    reader.skipValue();
                }
            }
            reader.endObject();

            return team;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            listAdapter = new ListItemAdapter(MainActivity.this, R.layout.list_item, games) ;
            lvSchedule.setAdapter(listAdapter);
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
                    tvTime.setText(item.getGameDate());
                }
            }

            return v;
        }

    }
}

