import java.io.FileInputStream;
import java.io.FileOutputStream;

public class HuffTest {
    public static void main(String[] args) {
        try {
            // File names
            String originalFile = "data/ecoli.txt";
            String compressedFile = "data/ecoli_compressed.hf";
            String decompressedFile = "data/ecoli_decompressed.txt";

            // Compress the original file
            BitInputStream in = new BitInputStream(new FileInputStream(originalFile));
            BitOutputStream out = new BitOutputStream(new FileOutputStream(compressedFile));
            HuffProcessor processor = new HuffProcessor();
            processor.compress(in, out);
            System.out.println("Compression complete. Compressed file: " + compressedFile);

            // Decompress the compressed file
            in = new BitInputStream(new FileInputStream(compressedFile));
            out = new BitOutputStream(new FileOutputStream(decompressedFile));
            processor.decompress(in, out);
            System.out.println("Decompression complete. Decompressed file: " + decompressedFile);

            // Compare original and decompressed files
            boolean filesMatch = compareFiles(originalFile, decompressedFile);
            if (filesMatch) {
                System.out.println("Success: The original and decompressed files match.");
            } else {
                System.out.println("Error: The original and decompressed files do not match.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to compare two files
    private static boolean compareFiles(String file1, String file2) throws Exception {
        FileInputStream fis1 = new FileInputStream(file1);
        FileInputStream fis2 = new FileInputStream(file2);

        int b1, b2;
        do {
            b1 = fis1.read();
            b2 = fis2.read();
            if (b1 != b2) {
                fis1.close();
                fis2.close();
                return false;
            }
        } while (b1 != -1 && b2 != -1);

        fis1.close();
        fis2.close();
        return true;
    }
}