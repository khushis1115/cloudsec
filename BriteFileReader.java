import java.io.*;

public class BriteFileReader {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\amits\\OneDrive\\Documents\\NetBeansProjects\\CLOUD\\src\\topology1.brite"; // Specify your file path here
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
