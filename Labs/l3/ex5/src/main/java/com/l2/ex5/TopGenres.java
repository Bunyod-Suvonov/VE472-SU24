package com.l2.ex5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TopGenres {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:/home/n1tr0/var/ex5/imdb.sqlite3";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = "SELECT tb.genres, tr.numVotes FROM title_basics tb JOIN title_ratings tr ON tb.tconst = tr.tconst";
        
        try {
            conn = DriverManager.getConnection(url);
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            Map<String, Integer> genreVotes = new HashMap<>();
            
            while (rs.next()) {
                String genres = rs.getString("genres");
                int numVotes = rs.getInt("numVotes");
                
                if (genres != null && !genres.isEmpty()) {
                    String[] genreArray = genres.split(",");
                    for (String genre : genreArray) {
                        genre = genre.trim();
                        if (genreVotes.containsKey(genre)) {
                            genreVotes.put(genre, genreVotes.get(genre) + numVotes);
                        } else {
                            genreVotes.put(genre, numVotes);
                        }
                    }
                }
            }
            
            TreeMap<Integer, String> sortedGenres = new TreeMap<>();
            for (Entry<String, Integer> entry : genreVotes.entrySet()) {
                sortedGenres.put(entry.getValue(), entry.getKey());
            }
            
            int count = 0;
            for (Entry<Integer, String> entry : sortedGenres.descendingMap().entrySet()) {
                System.out.println("Genre: " + entry.getValue() + ", Total Votes: " + entry.getKey());
                count++;
                if (count == 3) {
                    break;
                }
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
