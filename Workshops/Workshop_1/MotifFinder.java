import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MotifFinder {

    // Define the possible nucleotide bases
    private static final char[] BASES = {'A', 'C', 'G', 'T'};

    public static void main(String[] args) {
        String fileName = "nucleotide_database.txt";  // File containing nucleotide sequences
        int s = 6;  // Motif size
        try {
            // Count the occurrences of all motifs of size 's' in the file
            Map<String, Integer> motifCounts = countMotifs(fileName, s);
            // Find the most frequent motif
            String bestMotif = findBestMotif(motifCounts);
            System.out.println("The best motif is: " + bestMotif);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates all possible nucleotide combinations of size 's'.
     * 
     * @param s            The size of the motif
     * @param current      The current string being built
     * @param index        The current index in the string
     * @param motifCounts  A map storing the motifs and their respective counts
     */
    private static void generateMotifs(int s, StringBuilder current, int index, Map<String, Integer> motifCounts) {
        if (index == s) {
            // Once a full motif is built, add it to the map with an initial count of 0
            motifCounts.put(current.toString(), 0);
            return;
        }
        // Loop through each possible base and recursively build all motif combinations
        for (char base : BASES) {
            current.setCharAt(index, base);
            generateMotifs(s, current, index + 1, motifCounts);
        }
    }

    /**
     * Reads a file containing nucleotide sequences and counts the frequency of each motif.
     * 
     * @param fileName The name of the file containing nucleotide sequences
     * @param s        The size of the motif to search for
     * @return         A map of motifs and their respective counts
     * @throws IOException If an I/O error occurs while reading the file
     */
    private static Map<String, Integer> countMotifs(String fileName, int s) throws IOException {
        Map<String, Integer> motifCounts = new HashMap<>();
        StringBuilder motifBuilder = new StringBuilder(s);
        // Initialize the StringBuilder with a valid sequence of size 's'
        for (int i = 0; i < s; i++) {
            motifBuilder.append('A');
        }

        // Generate all possible motifs of size 's'
        generateMotifs(s, motifBuilder, 0, motifCounts);

        // Read the file and count occurrences of each motif in the sequences
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // For each motif, count how many times it appears in the current sequence line
                for (String motif : motifCounts.keySet()) {
                    int count = countOccurrences(line, motif);
                    motifCounts.put(motif, motifCounts.get(motif) + count);
                }
            }
        }
        return motifCounts;
    }

    /**
     * Counts the occurrences of a specific motif in a given sequence.
     * 
     * @param sequence The sequence in which to search for the motif
     * @param motif    The motif to search for
     * @return         The number of times the motif appears in the sequence
     */
    private static int countOccurrences(String sequence, String motif) {
        int count = 0;
        // Slide through the sequence and check if the motif matches at each position
        for (int i = 0; i <= sequence.length() - motif.length(); i++) {
            if (sequence.substring(i, i + motif.length()).equals(motif)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds the best motif, based on frequency and consecutive repeats.
     * 
     * @param motifCounts A map of motifs and their respective counts
     * @return            The motif with the highest frequency and most consecutive repeats
     */
    private static String findBestMotif(Map<String, Integer> motifCounts) {
        String bestMotif = null;
        int maxCount = 0;
        int maxRepeats = 0;

        // Loop through all motifs and determine the best one based on frequency and repeats
        for (Map.Entry<String, Integer> entry : motifCounts.entrySet()) {
            String motif = entry.getKey();
            int count = entry.getValue();
            int repeats = countConsecutiveRepeats(motif);

            // Prioritize motifs with higher counts, and among those with equal counts, prioritize more repeats
            if (count > maxCount || (count == maxCount && repeats > maxRepeats)) {
                bestMotif = motif;
                maxCount = count;
                maxRepeats = repeats;
            }
        }
        return bestMotif;
    }

    /**
     * Counts the maximum number of consecutive repeated characters in a motif.
     * 
     * @param motif The motif to analyze
     * @return      The maximum number of consecutive repeated characters
     */
    private static int countConsecutiveRepeats(String motif) {
        int maxRepeats = 0;
        int currentRepeats = 1;

        // Traverse the motif and track the length of consecutive identical characters
        for (int i = 1; i < motif.length(); i++) {
            if (motif.charAt(i) == motif.charAt(i - 1)) {
                currentRepeats++;
            } else {
                maxRepeats = Math.max(maxRepeats, currentRepeats);
                currentRepeats = 1;
            }
        }
        // Ensure the maximum is updated for the final character sequence
        maxRepeats = Math.max(maxRepeats, currentRepeats);

        return maxRepeats;
    }
}
