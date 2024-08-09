-- Age of songs in the database

SELECT max(2024 - year) AS age
  FROM songs
 WHERE year > 0;

SELECT min(2024 - year) AS age
  FROM songs
 WHERE year > 0;

-- the hottest song, shortest, highest energy, lowest tempo
SELECT song_id,
       title
  FROM songs
 WHERE song_hotness <> 'NaN'
 ORDER BY song_hotness DESC,
          duration ASC,
          energy DESC,
          tempo ASC
 LIMIT 5;

-- the album with the most songs
SELECT album_id,
       album_name,
       count(album_id) AS numSongs
  FROM songs
 GROUP BY album_id,
          album_name
 ORDER BY numSongs DESC
 LIMIT 1;

-- the band with longest song
SELECT artist_name, 
       title, 
       duration 
  FROM songs
 ORDER BY duration DESC
 LIMIT 1;

-- Top 5 hot songs of given band
SELECT 
    title, 
    artist_name, 
    song_hotness
  FROM 
    songs
 WHERE
    artist_name = 'The Beatles'
 ORDER BY 
    song_hotness DESC 
 LIMIT 10;