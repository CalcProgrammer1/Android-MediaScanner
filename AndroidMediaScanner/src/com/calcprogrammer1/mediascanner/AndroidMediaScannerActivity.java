package com.calcprogrammer1.mediascanner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.tag.*;

import com.calcprogrammer1.mediascanner.R;

public class AndroidMediaScannerActivity extends Activity
{
    ListView mainlist;
    String rootDirectory;
    ArrayList<libraryElementArtist> myLibrary;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainlist = (ListView) findViewById(R.id.listView1);
    }
    
    public void selectDirectoryButton(View view)
    {
        Intent intent = new Intent("org.openintents.action.PICK_DIRECTORY");
        startActivityForResult(intent, 1);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Uri dirname = data.getData();
        rootDirectory = dirname.getPath();
        File f = new File(rootDirectory);
        File[] files = FileOperations.selectFilesOnly(f.listFiles());
        myLibrary = scanMedia(files);
        drawList(myLibrary);
    }
    
    public void updateFolderButton(View view)
    {
        organizeFiles(rootDirectory, myLibrary);
        File f = new File(rootDirectory);
        File[] files = FileOperations.selectFilesOnly(f.listFiles());
        myLibrary = scanMedia(files);
        drawList(myLibrary);
    }
    
    public ArrayList<libraryElementArtist> scanMedia(File[] files)
    {
        ArrayList<libraryElementArtist> libraryData = new ArrayList<libraryElementArtist>();
        for(int i = 0; i < files.length; i++)
        {
            AudioFile f;
            try
            {
                f = AudioFileIO.read(files[i]);
                Tag tag = f.getTag();
                String song_artist = tag.getFirstArtist();
                String song_album = tag.getFirstAlbum();
                String song_title = tag.getFirstTitle();
                String song_year = tag.getFirstYear();
                String song_num = tag.getFirstTrack();
                
                int artistIndex = -1;
                int j = 0;
                
                //check if artist exists
                for(; j < libraryData.size(); j++)
                {
                    if(libraryData.get(j).name.equals(song_artist))
                    {
                        artistIndex = j;
                    }
                }
                //if artist not in list
                if(artistIndex == -1)
                {
                    libraryElementArtist newEntry = new libraryElementArtist();
                    newEntry.name = song_artist;
                    newEntry.albums = new ArrayList<libraryElementAlbum>();
                    libraryData.add(newEntry);
                    artistIndex = j;
                }
                
                //check if album exists
                int albumIndex = -1;
                j = 0; 
                for(; j < libraryData.get(artistIndex).albums.size(); j++)
                {
                    if(libraryData.get(artistIndex).albums.get(j).name.equals(song_album))
                    {
                        albumIndex = j;
                    }
                }
                if(albumIndex == -1)
                {
                    libraryElementAlbum newEntry = new libraryElementAlbum();
                    newEntry.name = song_album;
                    newEntry.year = song_year;
                    newEntry.songs = new ArrayList<libraryElementSong>();
                    libraryData.get(artistIndex).albums.add(newEntry);
                    albumIndex = j;
                }
                
                //check if song exists
                int songIndex = -1;
                j = 0;
                for(; j < libraryData.get(artistIndex).albums.get(albumIndex).songs.size(); j++)
                {
                    if(libraryData.get(artistIndex).albums.get(albumIndex).songs.get(j).name.equals(song_title))
                    {
                        songIndex = j;
                    }
                }
                if(songIndex == -1)
                {
                    libraryElementSong newEntry = new libraryElementSong();
                    newEntry.name = song_title;
                    newEntry.filename = files[i].getAbsolutePath();
                    newEntry.num = Integer.parseInt(song_num);
                    libraryData.get(artistIndex).albums.get(albumIndex).songs.add(newEntry);
                }
            }
            catch(Exception e)
            {
                
            }
        }
        return libraryData;
    }
    
    public void drawList(ArrayList<libraryElementArtist> libraryData)
    {
        
        // create the grid item mapping
        String[] from = new String[] {"artist", "album", "song"};
        int[] to = new int[] { R.id.textView1, R.id.textView2, R.id.textView3};

        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < libraryData.size(); i++)
        {
            HashMap<String, String> amap = new HashMap<String, String>();
            amap.put("artist", libraryData.get(i).name);
            amap.put("album", "");
            amap.put("song", "");
            fillMaps.add(amap);
            for(int j = 0; j < libraryData.get(i).albums.size(); j++)
            {
                HashMap<String, String> bmap = new HashMap<String, String>();
                bmap.put("artist", "");
                bmap.put("album", libraryData.get(i).albums.get(j).name);
                bmap.put("song", "");
                fillMaps.add(bmap);
                for(int k = 0; k < libraryData.get(i).albums.get(j).songs.size(); k++)
                {
                    HashMap<String, String> cmap = new HashMap<String, String>();
                    cmap.put("artist", "");
                    cmap.put("album", "");
                    cmap.put("song", libraryData.get(i).albums.get(j).songs.get(k).name);
                    fillMaps.add(cmap); 
                }
            }
        }

        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.listentry, from, to);
        mainlist.setAdapter(adapter);
    }
    
    public void organizeFiles(String rootDir, ArrayList<libraryElementArtist> libraryData)
    {
        for(int i = 0; i < libraryData.size(); i++)
        {
            //create artist folder
            File artistDir = new File(rootDir + File.separator + libraryData.get(i).name);
            artistDir.mkdir();
            artistDir.setExecutable(true, false);
            for(int j = 0; j < libraryData.get(i).albums.size(); j++)
            {
                //create album folder
                File albumDir = new File(rootDir + File.separator + libraryData.get(i).name + File.separator + libraryData.get(i).albums.get(j).name);
                albumDir.mkdir();
                albumDir.setExecutable(true, false);
                for(int k = 0; k < libraryData.get(i).albums.get(j).songs.size(); k++)
                {
                    //move song into folder
                    FileOperations.moveFile(libraryData.get(i).albums.get(j).songs.get(k).filename, rootDir + "/" + libraryData.get(i).name + "/" + libraryData.get(i).albums.get(j).name);
                }
            }
        }
    }
}