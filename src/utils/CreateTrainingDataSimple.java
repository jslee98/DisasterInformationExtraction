/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jeff Lee
 * Reads precategorized text files and formats into a single .arff file
 * The .arff file is used to train our WEKA text classifier
 */

public class CreateTrainingDataSimple {

    public static void main(String[] args) {

        File inDir;
        File[] flist;
        BufferedReader reader;
        String floc;
        String line;
        String text;

        String path = "./resources/Concisus-v3/text_files/";
        String[] languages = {"english", "spanish"};
        String[] domains = {"attack", "aviation", "quake", "train"};

        Path file = Paths.get("./resources/training/trainingdata.arff");
        List<String> lines = new ArrayList();
        lines.add("@relation 'news domain'");
        lines.add("@attribute Text string");
        lines.add("@attribute class {attack, aviation, quake, train}");
        lines.add("@data");

        for (String domain : domains) {
            for(String lang : languages) {
                System.out.println("\nCompiling " + domain + " in " + lang + "\n");
                inDir=new File(path + domain + '/' + lang);
                flist=inDir.listFiles();
                for(int f=0;f<flist.length;f++) {
                    floc=flist[f].getAbsolutePath();
                    try {
                        reader=new BufferedReader(new FileReader(floc));
                        text="";
                        while((line=reader.readLine())!=null) {
                            line=line.replaceAll("'", " ");
                            text=text+line+" ";
                        }
                        lines.add("'" + text + "'," + domain);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
