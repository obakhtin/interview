package local.interview;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    static private File file;

    static public void setFile(File file) {
        Parser.file = file;
    }

    public static String parse() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String parsedText = "";
        String line;
        while ((line = reader.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                int charCode = line.codePointAt(i);
                // charCode >= A && charCode <= z
                if (charCode > 0x41 && charCode < 0x7A) {
                    parsedText = parsedText + line.charAt(i);
                }
            }
        }
        return parsedText;
    }
}