package com.example.lookoutforgdgc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private String username;
    private FirebaseFirestore db;

    // Top 3 podium
    private TextView nameGold, nameSilver, nameBronze;

    // RecyclerView for the list of players
    private RecyclerView recyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<LeaderboardItem> leaderboardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        db = FirebaseFirestore.getInstance();

        // Podium TextViews
        nameGold = findViewById(R.id.name_gold);
        nameSilver = findViewById(R.id.name_silver);
        nameBronze = findViewById(R.id.name_bronze);

        // RecyclerView setup
        recyclerView = findViewById(R.id.leaderboard_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardList = new ArrayList<>();
        leaderboardAdapter = new LeaderboardAdapter(leaderboardList);
        recyclerView.setAdapter(leaderboardAdapter);

        // Username passed from previous activity
        Intent i = getIntent();
        if (i != null) {
            String u = i.getStringExtra("username");
            if (u != null) username = u;
        }
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        db.collection("users")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(10) // Fetch top 10 players
                .get()
                .addOnSuccessListener(query -> {
                    int rank = 1;
                    leaderboardList.clear(); // Clear the list before adding new data

                    for (QueryDocumentSnapshot doc : query) {
                        String name = doc.getString("username");
                        Long score = doc.getLong("score");

                        if (name == null) name = "Unknown";
                        if (score == null) score = 0L;

                        // Fill UI based on rank
                        switch (rank) {
                            case 1:
                                nameGold.setText(name);
                                break;
                            case 2:
                                nameSilver.setText(name);
                                break;
                            case 3:
                                nameBronze.setText(name);
                                break;
                            default:
                                // Add to the list for RecyclerView
                                leaderboardList.add(new LeaderboardItem(rank, name, score));
                                break;
                        }
                        rank++;
                    }

                    // Notify the adapter that the data has changed
                    leaderboardAdapter.notifyDataSetChanged();
                });
    }

    // New nested class for the RecyclerView adapter
    public static class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
        private List<LeaderboardItem> items;

        public LeaderboardAdapter(List<LeaderboardItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leader, parent, false);
            return new LeaderboardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
            LeaderboardItem item = items.get(position);
            holder.rankText.setText(String.valueOf(item.getRank()) + ".");
            holder.usernameText.setText(item.getUsername());
            holder.scoreText.setText(String.valueOf(item.getScore()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        // Inner class for the ViewHolder
        public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
            public TextView rankText, usernameText, scoreText;

            public LeaderboardViewHolder(@NonNull View itemView) {
                super(itemView);
                rankText = itemView.findViewById(R.id.rankText);
                usernameText = itemView.findViewById(R.id.usernameText);
                scoreText = itemView.findViewById(R.id.scoreText);
            }
        }
    }

    // Data class to represent a leaderboard item
    public static class LeaderboardItem {
        private int rank;
        private String username;
        private long score;

        public LeaderboardItem(int rank, String username, long score) {
            this.rank = rank;
            this.username = username;
            this.score = score;
        }

        public int getRank() {
            return rank;
        }

        public String getUsername() {
            return username;
        }

        public long getScore() {
            return score;
        }
    }
}