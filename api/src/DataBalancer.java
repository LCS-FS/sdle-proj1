import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DataBalancer {
    private static final int NUM_VIRTUAL_NODES = 10;
    private static final int N = 3;  // Number of nodes on the preference list (in this case 3 because we have created only 3 nodes in main (version 1  ))
    private final SortedMap<Integer, DynamoNode> circle = new TreeMap<>();
    private final Map<String, List<DynamoNode>> preferenceList = new HashMap<>();


    public void addNode(DynamoNode node) {
        for (int i = 0; i < NUM_VIRTUAL_NODES; i++) {
            String virtualNodeName = node.getName() + "#" + i;
            int hash = getHash(virtualNodeName);
            circle.put(hash, node);
        }
    }

    public void removeNode(DynamoNode node) {
        for (int i = 0; i < NUM_VIRTUAL_NODES; i++) {
            String virtualNodeName = node.getName() + "#" + i;
            int hash = getHash(virtualNodeName);
            circle.remove(hash);
        }
    }

    public DynamoNode getNode(String key) {
        int hash = getHash(key);
        if (!circle.isEmpty()) {
            if (!circle.containsKey(hash)) {
                // Find the next node in the circle
                SortedMap<Integer, DynamoNode> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }
            return circle.get(hash);
        }
        return null;
    }

    public List<DynamoNode> getPreferenceList(String key) {
        List<DynamoNode> replicas = new ArrayList<>();
        DynamoNode coordinator = getNode(key);

        // Include the coordinator in the preference list
        replicas.add(coordinator);

        // Get N-1 clockwise successor nodes
        int count = 0;
        for (Map.Entry<Integer, DynamoNode> entry : circle.tailMap(coordinator.hashCode()).entrySet()) {
            if (count >= N - 1) {
                break;
            }
            if (!replicas.contains(entry.getValue())) {
                replicas.add(entry.getValue());
                count++;
            }
        }

        return replicas;
    }

    private int getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            int hash = 0;
            for (int i = 0; i < 4; i++) {
                hash += ((bytes[i] & 0xFF) << (8 * i));
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found");
        }
    }

}
