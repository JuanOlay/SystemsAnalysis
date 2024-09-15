import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Class to generate a nucleotide database with sequences and apply entropy filtering.
 */
public class NucleotideDatabase {

    private static final char[] BASES = {'A', 'C', 'G', 'T'};

    /**
     * Main method to generate sequences and write them to a file.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        int n = 1000000;  // Number of sequences to generate
        int m = 50;       // Length of each sequence
        double[] probabilities = {0.25, 0.25, 0.25, 0.25};  // Probabilities for A, C, G, T

        try {
            System.out.println("Generating and writing sequences...");
            generateAndWriteSequences(n, m, probabilities, "nucleotide_database.txt");
            System.out.println("Sequences generated and written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates sequences of nucleotides and writes them to a file.
     *
     * @param n the number of sequences to generate
     * @param m the length of each sequence
     * @param probabilities an array of probabilities for each nucleotide base
     * @param fileName the name of the file where sequences will be written
     * @throws IOException if an error occurs while writing to the file
     */
    public static void generateAndWriteSequences(int n, int m, double[] probabilities, String fileName) throws IOException {
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < n; i++) {
                String sequence = generateSequence(m, probabilities, random);
                // Entropy filtering
                if (calculateEntropy(sequence) > thresholdEntropy()) {
                    writer.write(sequence);
                    writer.newLine();
                }
            }
        }
    }
    
    /**
     * Generates a random sequence of nucleotides based on the given length and probabilities.
     *
     * @param m the length of the sequence
     * @param probabilities an array of probabilities for each nucleotide base
     * @param random a Random object for generating random numbers
     * @return the generated sequence as a String
     */
    private static String generateSequence(int m, double[] probabilities, Random random) {
        StringBuilder sequence = new StringBuilder(m);
        for (int i = 0; i < m; i++) {
            sequence.append(weightedRandomBase(probabilities, random));
        }
        return sequence.toString();
    }
    
    /**
     * Selects a nucleotide base randomly based on the given probabilities.
     *
     * @param probabilities an array of probabilities for each nucleotide base
     * @param random a Random object for generating random numbers
     * @return the selected nucleotide base
     */
    private static char weightedRandomBase(double[] probabilities, Random random) {
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < BASES.length; i++) {
            cumulativeProbability += probabilities[i];
            if (rand <= cumulativeProbability) {
                return BASES[i];
            }
        }
        return 'A';  // Default value, should not be reached
    }
    
    /**
     * Calculates the Shannon entropy of a nucleotide sequence.
     *
     * @param sequence the nucleotide sequence
     * @return the entropy value of the sequence
     */
    private static double calculateEntropy(String sequence) {
        int[] baseCounts = new int[4];  // For A, C, G, T
        for (char c : sequence.toCharArray()) {
            if (c == 'A') baseCounts[0]++;
            else if (c == 'C') baseCounts[1]++;
            else if (c == 'G') baseCounts[2]++;
            else if (c == 'T') baseCounts[3]++;
        }

        double entropy = 0.0;
        for (int count : baseCounts) {
            if (count > 0) {
                double p = (double) count / sequence.length();
                entropy -= p * Math.log(p) / Math.log(2);
            }
        }
        return entropy;
    }
    
    /**
     * Returns the threshold value for entropy filtering.
     *
     * @return the entropy threshold value
     */
    private static double thresholdEntropy() {
        return 1.5;  // Threshold for filtering repetitive sequences
    }
}
