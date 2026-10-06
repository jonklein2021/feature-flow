package com.featureflow.generator;

import com.featureflow.core.domain.Album;
import com.featureflow.core.domain.Artist;
import com.featureflow.core.domain.Song;
import com.featureflow.core.domain.User;
import java.util.List;
import lombok.Value;

/** the static entities that listening events refer to */
@Value
public class Catalog {
    List<User> users;
    List<Artist> artists;
    List<Album> albums;
    List<Song> songs;
}
