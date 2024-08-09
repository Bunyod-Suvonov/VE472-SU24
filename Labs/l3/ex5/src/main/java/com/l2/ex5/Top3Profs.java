package com.l2.ex5;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Top3Profs {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:/home/n1tr0/var/ex5/imdb.sqlite3"; // Update this path to your actual database path

        Connection cn = null;
        Statement stat = null;
        ResultSet rs = null;
        ResultSet rsLifespan = null;

        try {
            cn = DriverManager.getConnection(url);
            if (cn != null) {
                stat = cn.createStatement();

                String topProfessionsQuery = "SELECT primaryProfession FROM name";
                rs = stat.executeQuery(topProfessionsQuery);
                Map<String, Integer> profCount = new HashMap<>();

                while (rs.next()) {
                    String professions = rs.getString("primaryProfession");
                    if (professions != null && !professions.isEmpty()) {
                        String[] professionArray = professions.split(",");
                        for (String profession : professionArray) {
                            String trimmedProf = profession.trim();
                            if (!trimmedProf.contains("miscellaneous")){
                                if(profCount.containsKey(trimmedProf)){
                                    int cnt = profCount.get(trimmedProf) + 1;
                                    profCount.put(trimmedProf, cnt);
                                }
                                else{
                                    profCount.put(trimmedProf, 1);
                                }
                            }
                        }
                    }
                }
                rs.close();

                Map<String, Integer> sortedProfessions = new TreeMap<>(new ProfessionCountComparator(profCount));
                sortedProfessions.putAll(profCount);

                int count = 0;
                for (String profession : sortedProfessions.keySet()) {
                    if (count >= 3) break;

                    String avgLifespanQuery = "SELECT AVG(CAST(deathYear AS INTEGER) - CAST(birthYear AS INTEGER)) AS avgLifespan FROM name " +
                                              "WHERE primaryProfession LIKE '%" + profession + "%' " +
                                              "AND deathYear != '\\N' AND birthYear != '\\N'";

                    rsLifespan = stat.executeQuery(avgLifespanQuery);
                    if (rsLifespan.next()) {
                        double avgLifespan = rsLifespan.getDouble("avgLifespan");
                        System.out.printf("Profession: %s, Average Lifespan: %.2f years%n", profession, avgLifespan);
                    }
                    count++;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class ProfessionCountComparator implements java.util.Comparator<String> {
        Map<String, Integer> base;

        public ProfessionCountComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            int countCompare = Integer.compare(base.get(b), base.get(a));
            return (countCompare != 0) ? countCompare : a.compareTo(b);
        }
    }
    /*                 */
    /*  I'm dying x_x  */
    /*                 */
}
