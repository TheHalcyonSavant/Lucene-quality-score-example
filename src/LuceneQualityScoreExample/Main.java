package LuceneQualityScoreExample;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Normaliser n = new Normaliser();
        //n = new Normaliser(true);      // uncomment this line for more comprehensive test

        String jt = "Java engineer";
        String normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        jt = "C# engineer";
        normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        jt = "Accountant";
        normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        jt = "Chief Accountant";
        normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        jt = "engineer";
        normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        jt = "Accountan";
        normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        jt = "";
        normalisedTitle = n.normalise(jt);
        n.output(jt, normalisedTitle);

        n.close();

        System.out.println("");
    }
}
