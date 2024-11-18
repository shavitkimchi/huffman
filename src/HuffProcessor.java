import java.util.PriorityQueue;

/**
 * Although this class has a history of several years,
 * it is starting from a blank-slate, new and clean implementation
 * as of Fall 2018.
 * <P>
 * Changes include relying solely on a tree for header information
 * and including debug and bits read/written information
 * 
 * @author Owen Astrachan
 *
 * Revise
 */

public class HuffProcessor {

	private class HuffNode implements Comparable<HuffNode> {
		HuffNode left;
		HuffNode right;
		int value;
		int weight;

		public HuffNode(int val, int count) {
			value = val;
			weight = count;
		}
		public HuffNode(int val, int count, HuffNode ltree, HuffNode rtree) {
			value = val;
			weight = count;
			left = ltree;
			right = rtree;
		}

		public int compareTo(HuffNode o) {
			return weight - o.weight;
		}
	}

	public static final int BITS_PER_WORD = 8;
	public static final int BITS_PER_INT = 32;
	public static final int ALPH_SIZE = (1 << BITS_PER_WORD); 
	public static final int PSEUDO_EOF = ALPH_SIZE;
	public static final int HUFF_NUMBER = 0xface8200;
	public static final int HUFF_TREE  = HUFF_NUMBER | 1;

	private boolean myDebugging = false;
	
	public HuffProcessor() {
		this(false);
	}
	
	public HuffProcessor(boolean debug) {
		myDebugging = debug;
	}

	/**
	 * Compresses a file. Process must be reversible and loss-less.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be compressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void compress(BitInputStream in, BitOutputStream out){
		int[] counts = readForCounts(in);
		HuffNode root = buildTree(counts);
		String[] codings = new String[ALPH_SIZE +1];
		makeCodingsFromTree(root, codings, "");
		writeHeader(root, out);
		writeCompressedBits(in, codings, out);
		out.close();
	}
	private int[] readForCounts(BitInputStream in) {
		int[] counts = new int[ALPH_SIZE +1];
		int val;
		while ((val = in.readBits(BITS_PER_WORD)) != -1) {
			counts[val]++;
		}
		counts[PSEUDO_EOF] =1;
		return counts;
	}
	private HuffNode buildTree(int[] counts) {
		PriorityQueue<HuffNode> pq = new PriorityQueue<>();
		for (int i =0; i < counts.length; i++) {
			if (counts[i] > 0) {
				pq.add(new HuffNode(i, counts[i], null, null));
			}
		}
		while (pq.size() > 1) {
			HuffNode left = pq.remove();
			HuffNode right = pq.remove();
			HuffNode parent = new HuffNode(-1, left.weight + right.weight, left, right);
			pq.add(parent);
		}
		return pq.remove();
	}
	private void makeCodingsFromTree(HuffNode root, String[] codings, String path) {
		if (root.left == null && root.right == null) {
			codings[root.value] = path;
			return;
		}
		makeCodingsFromTree(root.left, codings, path + "0");
		makeCodingsFromTree(root.right, codings, path + "1");
	}
	private void writeHeader(HuffNode root, BitOutputStream out) {
		out.writeBits(BITS_PER_INT, HUFF_TREE);
		writeTree(root, out);
	}
	private void writeTree(HuffNode node, BitOutputStream out) {
		if (node.left == null && node.right == null) {
			out.writeBits(1, 1);
			out.writeBits(BITS_PER_WORD +1, node.value);
		} else {
			out.writeBits(1, 0);
			writeTree(node.left, out);
			writeTree(node.right, out);
		}
	}
	private void writeCompressedBits(BitInputStream in, String[] codings, BitOutputStream out) {
		in.reset();
		int val;
		while ((val = in.readBits(BITS_PER_WORD)) != -1) {
			String code = codings[val];
			out.writeBits(code.length(), Integer.parseInt(code, 2));
		}
		String code = codings[PSEUDO_EOF];
		out.writeBits(code.length(), Integer.parseInt(code, 2));
	}

	/**
	 * Decompresses a file. Output file must be identical bit-by-bit to the
	 * original.
	 *
	 * @param in
	 *            Buffered bit stream of the file to be decompressed.
	 * @param out
	 *            Buffered bit stream writing to the output file.
	 */
	public void decompress(BitInputStream in, BitOutputStream out){
		int magic = in.readBits(BITS_PER_INT);
		if (magic != HUFF_TREE) {
			throw new HuffException("Not a Huffman file");
		}
		HuffNode root = readTree(in);
		HuffNode current = root;

		while (true){
			int bit = in.readBits(1);
			if (bit == -1) {
				throw new HuffException("Bad input, no PSEUDO_EOF");
			}
			if (bit == 0) {
				current = current.left;
			} else {
				current = current.right;
			}
			if (current.left == null && current.right == null) {
				if (current.value == PSEUDO_EOF) {
					break;
				} else {
					out.writeBits(BITS_PER_WORD, current.value);
					current = root;
				}
			}
		}
		out.close();
	}

	private HuffNode readTree(BitInputStream in) {
		int bit = in.readBits(1);
		if (bit == -1) {
			throw new HuffException("Bad input, no more bits to read");
		}
		if (bit == 1) {
			int value = in.readBits(BITS_PER_WORD +1);
			return new HuffNode(value, 0, null, null);
		}
		HuffNode left = readTree(in);
		HuffNode right = readTree(in);
		return new HuffNode(0, 0, left, right);
	}
}