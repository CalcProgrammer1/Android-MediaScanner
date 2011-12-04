package com.calcprogrammer1.mediascanner;

import java.io.File;
import java.util.ArrayList;

public class FileOperations
{
    public static File[] selectFilesOnly(File[] unsorted_files)
    {
        ArrayList<File> file_list = new ArrayList<File>();
        for(int i = 0; i < unsorted_files.length; i++)
        {
            if(!unsorted_files[i].isDirectory())
            {
                file_list.add(unsorted_files[i]);
            }
        }
        File[] files = new File[file_list.size()];
        for(int i = 0; i < file_list.size(); i++)
        {
            files[i] = file_list.get(i);
        }
        return files;
    }
    
    public static boolean moveFile(String sourcefile, String dest)
    {
        File file = new File(sourcefile);
        File dir = new File(dest);
        return file.renameTo(new File(dir, file.getName()));
    }
    
    public static File[] selectDirsOnly(File[] unsorted_files)
    {
        ArrayList<File> dir_list = new ArrayList<File>();
        for(int i = 0; i < unsorted_files.length; i++)
        {
            if(unsorted_files[i].isDirectory())
            {
                dir_list.add(unsorted_files[i]);
            }
        }
        return (File[]) dir_list.toArray();
    }
    
    public static File[] sortFileListDirsFiles(File[] unsorted_files)
    {
        ArrayList<File> file_list = new ArrayList<File>();
        ArrayList<File> dir_list = new ArrayList<File>();
        for(int i = 0; i < unsorted_files.length; i++)
        {
            if(unsorted_files[i].isDirectory())
            {
                dir_list.add(unsorted_files[i]);
            }
            else
            {
                file_list.add(unsorted_files[i]);
            }
        }
        
        File[] files = new File[dir_list.size() + file_list.size()];
        int count = 0;
        for(; count < dir_list.size(); count++)
        {
            files[count] = dir_list.get(count); 
        }
        for(int j = 0; j < file_list.size(); j++)
        {
            files[j + count] = file_list.get(j);
        }
        return files;  
    }    
}