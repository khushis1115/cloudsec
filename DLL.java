import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

// Node representing a user (Unode)
class Unode {
    int userID;
    String privateKey; // Changed from int to String
    Unode next;

    public Unode(int userID, String privateKey) {
        this.userID = userID;
        this.privateKey = privateKey;
        this.next = null;
    }
}

// Node representing a resource (Rnode)
class Rnode {
    int resourceID;
    int count;  // Number of users
    String key; // Combined private key (XOR results stored as strings for demonstration)
    Unode head; // Head of Unode list
    Rnode next;

    public Rnode(int resourceID) {
        this.resourceID = resourceID;
        this.count = 0;
        this.key = ""; // Initialize key as an empty string
        this.head = null;
        this.next = null;
    }
}

// Main class for managing resources and users
public class DLL {
    Rnode head; // Head of the Rnode list
    HashMap<Integer, String> userKeyMap; // Map to store userID -> privateKey

    public DLL() {
        this.head = null;
        this.userKeyMap = new HashMap<>();
    }

    // Function to insert Rnode at the end
    public void insertEnd(int resourceID) {
        Rnode newNode = new Rnode(resourceID);
        if (head == null) {
            head = newNode;
        } else {
            Rnode temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
    }

    // Function to find a resource node by ID
    public Rnode findResource(int resourceID) {
        Rnode temp = head;
        while (temp != null) {
            if (temp.resourceID == resourceID) {
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }

    // Function to combine keys (for demonstration, concatenate strings with "-")
    public String combineKeys(String existingKey, String newKey) {
        if (existingKey.isEmpty()) {
            return newKey;
        }
        return existingKey + "-" + newKey;
    }

    // Function to populate resources and users from the CSV file
    public void populateFromCSV(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;

        // Read the header to initialize resources
        if ((line = br.readLine()) != null) { // Read the first line (header)
            String[] headers = line.split(",");
            for (int i = 1; i < headers.length; i++) { // Skip the first column (User Private Key)
                int resourceID = i; // Column index as resource ID
                insertEnd(resourceID); // Create Rnodes for each resource
            }
        }

        // Process each user's data (skip the header row)
        int userID = 1; // Start user IDs from 1
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String privateKey = data[0]; // First column is the private key

            // Map the private key for this user
            userKeyMap.put(userID, privateKey);

            // Assign the user to resources
            // Updated part in the populateFromCSV method
            for (int i = 1; i < data.length; i++) { // Iterate over resource connections
                try {
                    int isConnected = Integer.parseInt(data[i]); // Check if connected
                    if (isConnected == 1) {
                        int resourceID = i; // Resource ID corresponds to column index
                        Rnode rnode = findResource(resourceID);

                        // Create and link the Unode
                        Unode newUnode = new Unode(userID, privateKey);
                        if (rnode.head == null) {
                            rnode.head = newUnode;
                        } else {
                            Unode temp = rnode.head;
                            while (temp.next != null) {
                                temp = temp.next;
                            }
                            temp.next = newUnode;
                        }

                        // Update resource metadata
                        rnode.count++;
                        rnode.key = combineKeys(rnode.key, privateKey); // Update key
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid resource connection value for user " + userID + ", column " + i + ". Skipping...");
                }
            }

            userID++;
        }

        br.close();
    }

    // Function to print all resources and their users
    public void printResources() {
        Rnode temp = head;
        while (temp != null) {
            System.out.print("R" + temp.resourceID + "(count=" + temp.count + ", key=" + temp.key + ")->");
            Unode utemp = temp.head;
            while (utemp != null) {
                System.out.print("U" + utemp.userID + "->");
                utemp = utemp.next;
            }
            System.out.println("null");
            temp = temp.next;
        }
    }

    // Function to print the userIDs and their private keys stored in the HashMap
    public void printUserKeyMap() {
        System.out.println("UserID -> PrivateKey Map:");
        for (HashMap.Entry<Integer, String> entry : userKeyMap.entrySet()) {
            System.out.println("UserID: " + entry.getKey() + ", PrivateKey: " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        DLL rm = new DLL();

        try {
            // Populate resources and users from the CSV file
            rm.populateFromCSV("src/adjacencylist/user_resource_access.csv");

            // Print resources and user map
            rm.printResources();
            rm.printUserKeyMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
