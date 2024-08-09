package com.l2.ex5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AverageTimeSpan {

    public static void main(String[] args) {
        String DATABASE_URL = "jdbc:sqlite:/home/n1tr0/var/ex5/imdb.sqlite3";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            if (conn != null) {
                String nameQuery = "SELECT nconst, primaryName, knownForTitles FROM name LIMIT 20";
                String titleQuery = "SELECT startYear, endYear FROM title_basics WHERE tconst = ?";

                try (PreparedStatement nameStmt = conn.prepareStatement(nameQuery);
                    PreparedStatement titleStmt = conn.prepareStatement(titleQuery);
                    ResultSet nameRs = nameStmt.executeQuery()) {

                    while (nameRs.next()) {
                        String primaryName = nameRs.getString("primaryName");
                        String knownForTitles = nameRs.getString("knownForTitles");

                        if (knownForTitles != null && !knownForTitles.isEmpty()) {
                            String[] titles = knownForTitles.split(",");

                            int totalSpan = 0;
                            int count = 0;
                            for (String tconst : titles) {
                                titleStmt.setString(1, tconst);
                                try (ResultSet titleRs = titleStmt.executeQuery()) {
                                    if (titleRs.next()) {
                                        String endYearStr = titleRs.getString("endYear");
                                        if(endYearStr == "\\N" || endYearStr == "") continue;

                                        int startYear = titleRs.getInt("startYear");
                                        int endYear = titleRs.getInt("endYear");
                                        count++;
                                        if(!(endYear > 1700)) continue;

                                        if (titleRs.wasNull()) {
                                            endYear = startYear; // If endYear is null, assume the title is still ongoing
                                            continue;
                                        }

                                        int span = endYear - startYear;
                                        totalSpan += span;
                                        count++;
                                    }
                                }
                            }

                            if (count > 0) {
                                double averageSpan = (double) totalSpan / count;
                                System.out.println("Name: " + primaryName + ", Average Time Span: " + averageSpan);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
