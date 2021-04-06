INSERT INTO ARTIST VALUES ('23129390abdc', 'http://artist1.com/image_lg.png', 'http://artist1.com/image_md.png', 'http://artist1.com/image_sm.png', 'Some Super Fancy Artist', 90);
INSERT INTO ARTIST VALUES ('93hdqcda39ds', 'http://artist2.com/image_lg.png', 'http://artist2.com/image_md.png', 'http://artist2.com/image_sm.png', 'Another Super Fancy Artist', 93);

INSERT INTO ALBUM (id, albumArtLarge, albumArtMedium, albumArtSmall, albumType, allTracksNotExplicit, allTracksPlayable, assumedLanguage, copyright, label, name, popularity, preview, releaseDate, storyType, totalDurationMs, totalTracks) VALUES ('990023ace67a', 'http://albumart.de/image1_lg.png', 'http://albumart.de/image1_md.png', 'http://albumart.de/image1_sm.png', 'ALBUM', 1, 1, 'DE', 'Copyright owner 1', 'Some Fancy Label', 'Fancy Album', 60, 'http://previewurl.de/sample.mp3', '2020-08-01', 'UNABRIDGED',  9078934, 10);
INSERT INTO ALBUM (id, albumArtLarge, albumArtMedium, albumArtSmall, albumType, allTracksNotExplicit, allTracksPlayable, assumedLanguage, copyright, label, name, popularity, preview, releaseDate, storyType, totalDurationMs, totalTracks) VALUES ('2847a676de8b', 'http://albumart.de/image2_lg.png', 'http://albumart.de/image2_md.png', 'http://albumart.de/image2_sm.png', 'COMPILATION', 1, 1, 'EN', 'Copyright owner 2', 'Some Fancy Label', 'Super Album', 80, 'http://previewurl.de/sample.mp3', '1992-08-03', 'ABRIDGED', 9078934, 10);

-- The first album is a cooperation of two artists
INSERT INTO ALBUM_ARTIST (ALBUM_ID, ARTISTS_ID) VALUES ('990023ace67a', '23129390abdc')
INSERT INTO ALBUM_ARTIST (ALBUM_ID, ARTISTS_ID) VALUES ('990023ace67a', '93hdqcda39ds')
INSERT INTO ALBUM_ARTIST (ALBUM_ID, ARTISTS_ID) VALUES ('2847a676de8b', '23129390abdc')

INSERT INTO CRAWL_STATS_KV VALUES ('PLAYLISTS_CONSIDERED_COUNT', '3')
INSERT INTO CRAWL_STATS_KV VALUES ('PROFILES_CONSIDERED_COUNT', '2')
INSERT INTO CRAWL_STATS_KV VALUES ('ARTISTS_CONSIDERED_COUNT', '1')
INSERT INTO CRAWL_STATS_KV VALUES ('ALBUMS_FOUND_COUNT', '0')