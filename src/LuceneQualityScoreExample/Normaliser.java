package LuceneQualityScoreExample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * I've used the Apache Lucene library for calculating the best match against a
 * list of normalised strings from un-normalised input string.
 * 
 * This solution could also have been implemented with pure fuzzy string
 * searching algorithms like Jaro-Winkler, Levenshtein or Monge-Elkan.
 * 
 * The method normilise considers a quality score between 0.0 and 1.0. This
 * approach is not advisable by Lucene developers and was removed since
 * version 3: http://wiki.apache.org/lucene-java/ScoresAsPercentages
 * http://www.gossamer-threads.com/lists/lucene/java-user/75002
 *
 */
class Normaliser {

    private static final List<String> N = Arrays.asList(
            "Architect",
            "Software engineer",
            "Quantity surveyor",
            "Accountant",
            "Medical engineer",
            "Software engineer asks another engineer");
    private Boolean _debug = false;
    private IndexReader _idxReader;
    private IndexSearcher _searcher;
    private QueryParser _queryParser;
    private StandardAnalyzer _analyzer = new StandardAnalyzer(Version.LUCENE_46);
    private List<String> _normalisedList = new ArrayList<String>();

    public Normaliser() throws IOException {
        _idxReader = DirectoryReader.open(generateIndex(N));
        _queryParser = new QueryParser(Version.LUCENE_46, "title", _analyzer);
        _searcher = new IndexSearcher(_idxReader);
    }

    public Normaliser(Boolean debug) throws IOException {
        this();
        _debug = debug;
    }

    private Directory generateIndex(List<String> titles) throws IOException {
        Directory index = new RAMDirectory();
        IndexWriter idxWriter = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_46, _analyzer));
        for (String title : titles) {
            Document doc = new Document();
            doc.add(new TextField("title", title, Field.Store.YES));
            idxWriter.addDocument(doc);
        }
        idxWriter.close();

        return index;
    }

    /**
     * Prepares a list of lMax top matched entries from the unnormalised_input
     * query string.
     * <p>
     * The score of every list entry can be between 0.0 and 1.0. If the score is
     * higher then 1, then it's normalised against the match with the maximum
     * score of the returned list.
     *
     * @param unnormalised_input is the search query string.
     * @param lMax the maximum number of elements that can be returned.
     */
    public void normaliseList(String unnormalised_input, int lMax) throws ParseException, IOException {
        _normalisedList.clear();

        if (unnormalised_input.trim().isEmpty()) {
            return;
        }

        TopDocs topDocs = _searcher.search(_queryParser.parse(unnormalised_input), lMax);
        ScoreDoc[] hits = topDocs.scoreDocs;

        if (hits.length == 0) {
            return;
        }

        float scoreNorm, maxScore = topDocs.getMaxScore();
        String debugInfo = _debug ? ", (Score: %f; TotalHits: %d)" : "";

        for (ScoreDoc hit : hits) {
            // normalization of only one element doesn't make sense:
            // score = score / score * scoreNorm alwasy returns 1.0
            // score normalization can be tested in  the presence of more
            // than one matched result
            scoreNorm = 1.0f;
            if (hit.score > 1) {
                scoreNorm = 1.0f / maxScore;
            }
            hit.score *= scoreNorm;

            _normalisedList.add(String.format("%s" + debugInfo,
                    _searcher.doc(hit.doc).get("title"), hit.score, hits.length));
        }
    }

    /**
     * Returns only the best matched entry from the unnormalised_input query
     * string.
     */
    public String normalise(String unnormalised_input) throws ParseException, IOException {
        normaliseList(unnormalised_input, 20);
        if (_normalisedList.isEmpty()) {
            return "";
        }

        return _normalisedList.get(0);
    }

    public void output(String unnorm, String norm) {
        System.out.format("\n%s -> %s", unnorm, norm);
        if (_debug && _normalisedList.size() > 1) {
            for (String s : _normalisedList) {
                System.out.format("\n\t-> %s", s);
            }
        }
    }

    public void close() throws IOException {
        _idxReader.close();
    }
}
