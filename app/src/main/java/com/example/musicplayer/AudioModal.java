package com.example.musicplayer;

public class AudioModal
{
    private String path ;
    private String name;
    private String artist;

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
    public String getArtist() {
        return artist;
    }

    public AudioModal(String path, String name, String artist) {
        this.path = path;
        this.name = name;
        this.artist = artist;
    }
}
