import java.util.List;

public class Main {
    public static void main(String[] args) {
        DynamoNode node1 = new DynamoNode("Node1");
        DynamoNode node2 = new DynamoNode("Node2");
        DynamoNode node3 = new DynamoNode("Node3");
        DynamoNode node4 = new DynamoNode("Node4");


        DataBalancer dataBalancer = new DataBalancer();
        dataBalancer.addNode(node1);
        dataBalancer.addNode(node2);
        dataBalancer.addNode(node3);
        dataBalancer.addNode(node4);

        // TODO those keys are keys of objects and should be hashes
        // please replace
        String key1 = "Key1";
        String key2 = "Key2";
        String key3 = "Key3";

        // testing getNode()
        DynamoNode responsibleNode1 = dataBalancer.getNode(key1);
        DynamoNode responsibleNode2 = dataBalancer.getNode(key2);
        DynamoNode responsibleNode3 = dataBalancer.getNode(key3);

        System.out.println("Key1 is mapped to: " + responsibleNode1.getName());
        System.out.println("Key2 is mapped to: " + responsibleNode2.getName());
        System.out.println("Key3 is mapped to: " + responsibleNode3.getName());

        // testing storeKey()
        responsibleNode1.storeKey(key1);
        responsibleNode2.storeKey(key2);
        responsibleNode3.storeKey(key3);

        // test getPreferenceList()
        List<DynamoNode> preferenceList1 = dataBalancer.getPreferenceList(key1);
        List<DynamoNode> preferenceList2 = dataBalancer.getPreferenceList(key2);
        List<DynamoNode> preferenceList3 = dataBalancer.getPreferenceList(key3);

        System.out.println("Preference list for Key1: ");
        for (DynamoNode node : preferenceList1) {
            System.out.println(node.getName());
        }
        System.out.println("Preference list for Key2: ");
        for (DynamoNode node : preferenceList2) {
            System.out.println(node.getName());
        }
        System.out.println("Preference list for Key3: ");
        for (DynamoNode node : preferenceList3) {
            System.out.println(node.getName());
        }


        /*
        flow for the actual main
        -> wait for a put / get request from frontend
        -> use dataBalancer class to do:
            -> define coordinator
            -> get preference list of nodes
            -> if get:
                -> get all the replicas based on the preference list
                -> conflict resolution
                -> return free conflict version
            -> if put:
                -> write on each node the new version based on the preference list

         */
    }
}
