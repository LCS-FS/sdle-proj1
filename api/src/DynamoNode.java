import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class DynamoNode {
    private final String name;

    public DynamoNode(String name) {
        this.name = name;
        createCsvFile();
    }

    private void createCsvFile() {
        try {
            String fileName = "database/" + this.name + ".csv";
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeKey(String key) {
        // this function is always writing at the end of the file.
        // change to write a new if key
        try {
            String fileName = "database/" + this.name + ".csv";
            FileWriter writer = new FileWriter(fileName, true);
            writer.append(key + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
