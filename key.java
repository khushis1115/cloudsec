import java.util.*;

// Class representing a User Node
class UserNode {
    int userData; // Data associated with the user
    String key; // Unique key associated with the user node

    public UserNode(int userData) {
        this.userData = userData;
        this.key = generateKey(userData);
    }

    // Method to generate a unique key based on the user's data
    private String generateKey(int data) {
        return "K" + Integer.toHexString(Objects.hash(data));
    }

    // Method to combine this UserNode with another UserNode to generate a new key
    public String combineKeys(UserNode other) {
        String combinedData = this.key + other.key;
        return generateKeyFromCombinedData(combinedData);
    }

    // Method to generate a new key from combined keys
    private String generateKeyFromCombinedData(String combinedData) {
        return "K" + Integer.toHexString(Objects.hash(combinedData));
    }

    @Override
    public String toString() {
        return "UserNode(" + userData + ", Key: " + key + ")";
    }

    public static void main(String[] args) {
        // Create two user nodes
        UserNode user1 = new UserNode(100);
        UserNode user2 = new UserNode(200);

        // Print their individual keys
        System.out.println("User 1: " + user1);
        System.out.println("User 2: " + user2);

        // Combine their keys and print the new combined key
        String combinedKey = user1.combineKeys(user2);
        System.out.println("Combined Key of User 1 and User 2: " + combinedKey);

        // Combine in reverse to show the result is still unique
        String reverseCombinedKey = user2.combineKeys(user1);
        System.out.println("Combined Key of User 2 and User 1: " + reverseCombinedKey);
    }
}
