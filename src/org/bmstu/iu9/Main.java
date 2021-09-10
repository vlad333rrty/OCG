package org.bmstu.iu9;

import org.bmstu.iu9.cfg.ControlFlowGraph;
import org.bmstu.iu9.dot.DotConverter;
import org.bmstu.iu9.parser.ParseException;
import org.bmstu.iu9.parser.ParseResult;
import org.bmstu.iu9.parser.Parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
    private static final String DATA_PATH = "/home/vlad333rrty/IdeaProjects/OCG4/data/";
    private static final String INPUT_FILE_PATH = "/home/vlad333rrty/IdeaProjects/OCG4/data/output.txt";

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        Parser parser = new Parser(INPUT_FILE_PATH);
        ParseResult result = parser.parse();
        ControlFlowGraph cfg = new ControlFlowGraph(result.getRoot(), result.getVertices());

        cfg.buildSSA();

        toPNG(cfg);
    }

    private static void toPNG(ControlFlowGraph cfg) throws IOException, InterruptedException {
        DotConverter dotConverter = new DotConverter();
        FileOutputStream file = new FileOutputStream(DATA_PATH + "result.txt");
        PrintStream Output = new PrintStream(file);
        Output.print(dotConverter.convertToDot(cfg));
        Output.close();

        File f = new File(DATA_PATH + "result.txt");
        String arg1 = f.getAbsolutePath();
        String arg2 = DATA_PATH + "result.png";
        String[] c = {"dot", "-Tpng", arg1, "-o", arg2};
        Runtime.getRuntime().exec(c).waitFor();
    }
}
