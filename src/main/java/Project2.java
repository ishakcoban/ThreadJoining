import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class Project2 {

    public static void main(String[] args) throws IOException, InvalidAException {
        new Node().getInput(args);
    }
}
