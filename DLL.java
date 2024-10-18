import java.util.HashMap;
import java.util.Scanner;

// Node representing a user (Unode)
class Unode {
    int userID;
    int privateKey;
    Unode next;
    
    public Unode(int userID, int privateKey) {
        this.userID = userID;
        this.privateKey = privateKey;
        this.next = null;
    }
}

// Node representing a resource (Rnode)
class Rnode {
    int resourceID;
    int count;  // Number of users
    int key;    // Combined private key
    Unode head; // Head of Unode list
    Rnode next;
    
    public Rnode(int resourceID) {
        this.resourceID = resourceID;
        this.count = 0;
        this.key = 0;
        this.head = null;
        this.next = null;
    }
}

// Main class for managing resources and users
public class DLL {
    Rnode head; // Head of the Rnode list
    HashMap<Integer, Integer> userKeyMap; // Map to store userID -> privateKey
    
    public DLL() {
        this.head = null;
        this.userKeyMap = new HashMap<>();
    }

    // Function to insert Rnode at the beginning
    public void insertBegin(int resourceID) {
        Rnode newNode = new Rnode(resourceID);
        newNode.next = head;
        head = newNode;
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

    // Function to delete Rnode from the beginning
    public void deleteBegin() {
        if (head != null) {
            head = head.next;
        }
    }

    // Function to delete Rnode from the end
    public void deleteEnd() {
        if (head == null || head.next == null) {
            head = null;
        } else {
            Rnode temp = head;
            while (temp.next.next != null) {
                temp = temp.next;
            }
            temp.next = null;
        }
    }

    // Function to add a user (Unode) to a resource (Rnode) and update the key
    public void addUserAndUpdateKey(int resourceID, int numUsers) {
        Rnode rnode = findResource(resourceID);
        if (rnode == null) {
            System.out.println("Resource not found.");
            return;
        }

        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < numUsers; i++) {
            System.out.print("Enter userID: ");
            int userID = sc.nextInt();
            int privateKey;

            // Check if userID exists in the map
            if (userKeyMap.containsKey(userID)) {
                privateKey = userKeyMap.get(userID);
                System.out.println("Reusing stored private key for user " + userID + ": " + privateKey);
            } else {
                System.out.print("Enter private key for user " + userID + ": ");
                privateKey = sc.nextInt();
                userKeyMap.put(userID, privateKey);  // Store the userID -> privateKey mapping
            }

            // Add the user to the resource's user list and update the key
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

            rnode.count++;
            rnode.key = combineKeys(rnode.key, privateKey); // Update key
            System.out.println("Key for resource " + resourceID + " updated to: " + rnode.key);
        }
    }

    // Function to delete a user (Unode) from a resource (Rnode) and update the key
    public void deleteUnode(int resourceID, int userID) {
        Rnode rnode = findResource(resourceID);
        if (rnode == null || rnode.head == null) {
            System.out.println("Resource or user not found.");
            return;
        }

        Unode prev = null;
        Unode current = rnode.head;

        while (current != null && current.userID != userID) {
            prev = current;
            current = current.next;
        }

        if (current == null) {
            System.out.println("User not found in the resource.");
            return;
        }

        // Remove the Unode from the list
        if (prev == null) {
            rnode.head = current.next;
        } else {
            prev.next = current.next;
        }

        rnode.count--;
        rnode.key = combineKeys(rnode.key, current.privateKey); // Update key by XOR-ing again
        System.out.println("User " + userID + " removed. Key for resource " + resourceID + " updated to: " + rnode.key);
    }

    // Function to combine keys using XOR (you can modify this based on your key combination logic)
    public int combineKeys(int existingKey, int newKey) {
        return existingKey ^ newKey;
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
        for (HashMap.Entry<Integer, Integer> entry : userKeyMap.entrySet()) {
            System.out.println("UserID: " + entry.getKey() + ", PrivateKey: " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DLL rm = new DLL();
        int choice;

        do {
            System.out.println("1.Insert begin \n2.Insert end \n3.Print \n4.Delete begin \n5.Delete end \n6.Insert unode & update key \n7.Delete unode & update key \n8.Print userID -> PrivateKey map \n9.Exit");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter resource ID: ");
                    rm.insertBegin(sc.nextInt());
                    break;

                case 2:
                    System.out.print("Enter resource ID: ");
                    rm.insertEnd(sc.nextInt());
                    break;

                case 3:
                    rm.printResources();
                    break;

                case 4:
                    rm.deleteBegin();
                    break;

                case 5:
                    rm.deleteEnd();
                    break;

                case 6:
                    System.out.print("Enter resource ID: ");
                    int resourceID = sc.nextInt();
                    System.out.print("Enter number of users to add: ");
                    int numUsers = sc.nextInt();
                    rm.addUserAndUpdateKey(resourceID, numUsers);
                    break;

                case 7:
                    System.out.print("Enter resource ID: ");
                    int resID = sc.nextInt();
                    System.out.print("Enter userID to delete: ");
                    int userID = sc.nextInt();
                    rm.deleteUnode(resID, userID);
                    break;

                case 8:
                    rm.printUserKeyMap();
                    break;

                case 9:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (choice != 9);
    }
}
