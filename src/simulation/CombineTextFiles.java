package simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


public class CombineTextFiles {
    public static void main(String[] args) throws IOException {
        ArrayList<String> final_list= new ArrayList<String>();
        File file_dir = new File("files");
        File[] files = file_dir.listFiles();
        Arrays.sort(files, Comparator.comparing(File::getName));
        File temp = null;
        for (int i=0; i<=files.length; i++){
            if(i%10==1){
                temp = files[i];
            } else if (i%10>1){
                files[i-1] = files[i];
            } else if (i%10==0 && i!=0){
                files[i-1] = temp;
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("results.txt"));
            for (int i=0; i<=files.length-1; i++){
                //System.out.println(Files.readString(Path.of(files[i].getAbsolutePath())));
                writer.write(Files.readString(Path.of(files[i].getAbsolutePath())));
                writer.newLine();
                writer.flush();
            }
            //System.out.println(files.length);
            writer.close();
        } catch (Exception e) {
            System.out.println("something went wrong");
        }


        //String(Files.readAllBytes(Paths.get(fileName)));
    }
}
