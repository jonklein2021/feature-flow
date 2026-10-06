package com.featureflow.generator;

import com.featureflow.core.domain.Album;
import com.featureflow.core.domain.Artist;
import com.featureflow.core.domain.EventType;
import com.featureflow.core.domain.ListeningEvent;
import com.featureflow.core.domain.Song;
import com.featureflow.core.domain.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

/**
 * produces a reproducible catalog and listening history.
 *
 * <p>this is a deliberately simple first model: uniform entity popularity, fixed event type
 * weights and strictly increasing timestamps. realism (popularity skew, sessions, late and
 * duplicate events) is meant to be layered on here.
 */
@RequiredArgsConstructor
public class SyntheticEventGenerator {

    private static final String[] GENRES = {"rock", "pop", "jazz", "hip-hop", "electronic", "classical"};
    private static final String[] COUNTRIES = {"US", "GB", "DE", "BR", "JP", "IN"};
    private static final int SONGS_PER_ALBUM = 10;

    private final GeneratorConfig config;

    /** builds users, artists, albums and songs from the configured seed */
    public Catalog generateCatalog() {
        var random = new Random(config.getSeed());

        var users = new ArrayList<User>(config.getUserCount());
        for (int i = 1; i <= config.getUserCount(); i++) {
            users.add(User.builder()
                    .id(i)
                    .country(COUNTRIES[random.nextInt(COUNTRIES.length)])
                    .createdAt(config.getStartTime().minusSeconds(random.nextInt(365 * 24 * 3600)))
                    .build());
        }

        var artists = new ArrayList<Artist>(config.getArtistCount());
        for (int i = 1; i <= config.getArtistCount(); i++) {
            artists.add(Artist.builder().id("artist_" + i).name("Artist " + i).build());
        }

        var albums = new ArrayList<Album>();
        var songs = new ArrayList<Song>(config.getSongCount());
        for (int i = 1; i <= config.getSongCount(); i++) {
            Artist artist = artists.get(random.nextInt(artists.size()));
            int albumNumber = (i - 1) / SONGS_PER_ALBUM + 1;
            if (albumNumber > albums.size()) {
                albums.add(Album.builder()
                        .id("album_" + albumNumber)
                        .title("Album " + albumNumber)
                        .artistId(artist.getId())
                        .build());
            }
            Album album = albums.get(albumNumber - 1);
            songs.add(Song.builder()
                    .id("song_" + i)
                    .title("Song " + i)
                    .artistId(album.getArtistId())
                    .albumId(album.getId())
                    .genre(GENRES[random.nextInt(GENRES.length)])
                    .durationSeconds(120 + random.nextInt(240))
                    .build());
        }

        return new Catalog(users, artists, albums, songs);
    }

    /**
     * lazily generates events in timestamp order. the stream is stateful, so consume it once and
     * call this method again for a fresh, identical sequence.
     */
    public Stream<ListeningEvent> generateEvents(Catalog catalog) {
        var random = new Random(config.getSeed() + 1);
        long totalNanos = config.getSpan().toNanos();
        long stepNanos = Math.max(1, totalNanos / config.getEventCount());

        return LongStream.range(0, config.getEventCount())
                .mapToObj(index -> nextEvent(catalog, random, config.getStartTime().plusNanos(index * stepNanos)));
    }

    private ListeningEvent nextEvent(Catalog catalog, Random random, Instant timestamp) {
        List<Song> songs = catalog.getSongs();
        Song song = songs.get(random.nextInt(songs.size()));
        User user = catalog.getUsers().get(random.nextInt(catalog.getUsers().size()));
        EventType type = pickEventType(random);

        Integer duration = switch (type) {
            case PLAY -> song.getDurationSeconds();
            case SKIP -> 1 + random.nextInt(30);
            default -> null;
        };

        return ListeningEvent.builder()
                .eventId(new UUID(random.nextLong(), random.nextLong()).toString())
                .userId(user.getId())
                .songId(song.getId())
                .artistId(song.getArtistId())
                .type(type)
                .timestamp(timestamp)
                .durationSeconds(duration)
                .build();
    }

    private static EventType pickEventType(Random random) {
        int roll = random.nextInt(100);
        if (roll < 65) {
            return EventType.PLAY;
        } else if (roll < 85) {
            return EventType.SKIP;
        } else if (roll < 93) {
            return EventType.LIKE;
        } else if (roll < 96) {
            return EventType.UNLIKE;
        }
        return EventType.ADD_TO_PLAYLIST;
    }
}
