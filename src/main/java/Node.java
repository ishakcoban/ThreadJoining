import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Node extends Thread {
    @Option(name = "-i")
    @Argument
    private String fileName;

    public static Map<ArrayList<String>, String> map = new HashMap<>();
    public static Map<String, Boolean> processes = new HashMap<>();
    public static Map<String, Boolean> waitingMessages = new HashMap<>();
    public static Map<String, Boolean> startingMessages = new HashMap<>();

    public void run() {

        // Find the current thread and make processing
        for (Map.Entry<String, Boolean> processes1 : processes.entrySet()) {
            if (Objects.equals(processes1.getKey(), Thread.currentThread().getName())) {
                try {
                    int randomTime = 1 + (int) (Math.random() * 2000);
                    Thread.sleep(randomTime);
                    System.out.println("Node" + Thread.currentThread().getName() + " is completed");
                    processes.put(Thread.currentThread().getName(), true);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void perform(Map<ArrayList<String>, String> map) {

        fillMaps();// Fill created all maps

        //Create as many threads as the number of process
        ArrayList<Thread> threads = new ArrayList<>();
        for (Map.Entry<String, Boolean> processes1 : processes.entrySet()) {
            Node thread = new Node();
            thread.setName(processes1.getKey());
            threads.add(thread);
        }
        //Shuffle processes
        ArrayList<String> arr = new ArrayList<>();
        for (Map.Entry<String, Boolean> processes1 : processes.entrySet()) {
            arr.add(processes1.getKey());
        }
        Collections.shuffle(arr);

        while (true) {

            boolean processStatus = true;
            for (Map.Entry<String, Boolean> processes : processes.entrySet()) {
                if (!processes.getValue()) {
                    processStatus = false;

                }
            }
            if (processStatus) {
                break;

            }

            for (String p : arr) {
                for (Map.Entry<String, Boolean> processes1 : processes.entrySet()) {
                    if (Objects.equals(p, processes1.getKey())) {
                        int count1 = 0;
                        for (Map.Entry<ArrayList<String>, String> map1 : map.entrySet()) {
                            if (!Objects.equals(processes1.getKey(), map1.getValue()) && !processes1.getValue()) {
                                count1++;

                            }
                        }
                        if (count1 == 0) {
                            for (Thread thread : threads) {
                                if (Objects.equals(thread.getName(), processes1.getKey())) {
                                    for (Map.Entry<String, Boolean> startingMessages1 : startingMessages.entrySet()) {
                                        if (Objects.equals(thread.getName(), Character.toString(startingMessages1.getKey().charAt(0))) && !startingMessages1.getValue()) {
                                            startingMessages.put(startingMessages1.getKey(), true);
                                            System.out.println("Node" + startingMessages1.getKey());
                                            thread.start();
                                        }
                                    }

                                }

                            }
                        } else {
                            int counter = 0;
                            for (Map.Entry<ArrayList<String>, String> map1 : map.entrySet()) {
                                if (Objects.equals(map1.getValue(), processes1.getKey())) {
                                    for (int i = 0; i < map1.getKey().size(); i++) {
                                        for (Thread thread : threads) {
                                            if (Objects.equals(thread.getName(), map1.getKey().get(i)) && !Objects.equals(thread.getState().toString(), "TERMINATED")) {
                                                counter++;

                                            }

                                        }
                                    }

                                }

                            }

                            if (counter != 0 && !processes1.getValue()) {
                                for (Thread thread : threads) {
                                    if (Objects.equals(thread.getName(), processes1.getKey())) {
                                        for (Map.Entry<String, Boolean> waitingMessages1 : waitingMessages.entrySet()) {
                                            if (Objects.equals(thread.getName(), Character.toString(waitingMessages1.getKey().charAt(0))) && !waitingMessages1.getValue()) {
                                                waitingMessages.put(waitingMessages1.getKey(), true);
                                                System.out.println("Node" + waitingMessages1.getKey());
                                            }
                                        }

                                    }
                                }
                            } else {
                                for (Thread thread : threads) {
                                    if (Objects.equals(thread.getName(), processes1.getKey())) {
                                        for (Map.Entry<String, Boolean> startingMessages1 : startingMessages.entrySet()) {
                                            if (Objects.equals(thread.getName(), Character.toString(startingMessages1.getKey().charAt(0))) && !startingMessages1.getValue()) {
                                                startingMessages.put(startingMessages1.getKey(), true);
                                                System.out.println("Node" + startingMessages1.getKey());
                                                thread.start();
                                            }
                                        }

                                    }


                                }
                            }

                        }
                    }
                }
            }

        }

    }

    public static void fillMaps() {

        // Prevent duplicate processes coming from original map
        Set<String> list = new HashSet<>();

        for (Map.Entry<ArrayList<String>, String> entry : map.entrySet()) {
            list.addAll(entry.getKey());
            if (!Objects.equals(entry.getValue(), "-")) {
                list.add(entry.getValue());
            }
        }
        for (String process : list) {
            processes.put(process, false);
        }

        // Fill the startingMessages map by messages
        for (Map.Entry<String, Boolean> processes1 : processes.entrySet()) {
            startingMessages.put(processes1.getKey() + " is being started", false);
        }

        // Fill the waitingMessages map by messages
        for (Map.Entry<String, Boolean> processes1 : processes.entrySet()) {
            for (Map.Entry<ArrayList<String>, String> map1 : map.entrySet()) {
                if (Objects.equals(processes1.getKey(), map1.getValue())) {
                    StringBuilder s1 = new StringBuilder();
                    s1.append(processes1.getKey()).append(" is waiting for ");
                    for (int i = 0; i < map1.getKey().size(); i++) {
                        if (i != map1.getKey().size() - 1) {
                            s1.append(map1.getKey().get(i)).append(",");
                        } else {
                            s1.append(map1.getKey().get(i));
                        }

                    }
                    waitingMessages.put(s1.toString(), false);
                }
            }
        }

    }

    public static void readFile(String fileName) {

        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                ArrayList<String> keys = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    if (data.length() == 1) {
                        keys.add(String.valueOf(data.charAt(i)));
                        map.put(keys, "-");
                        break;
                    }
                    if (String.valueOf(data.charAt(i)).equals("-")) {
                        for (int j = i + 2; j < data.length(); j++) {
                            map.put(keys, String.valueOf(data.charAt(j)));
                        }
                        break;
                    }
                    if (data.charAt(i) != ',') {
                        keys.add(String.valueOf(data.charAt(i)));
                    }
                }

            }
            myReader.close();
            perform(map);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void getInput(String[] args) throws IOException, InvalidAException {

        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            return;
        }
        if ((Objects.equals(fileName, "input1.txt")) || (Objects.equals(fileName, "input2.txt"))) {
            readFile(fileName);
        } else {
            throw new InvalidAException("File not found");
        }

    }

}

class InvalidAException extends Exception {
    public InvalidAException(String str) {
        super(str);
    }
}