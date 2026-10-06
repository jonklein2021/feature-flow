package com.featureflow.core.domain;

/** kinds of listener interaction captured in the event stream */
public enum EventType {
    PLAY,
    SKIP,
    LIKE,
    UNLIKE,
    ADD_TO_PLAYLIST
}
