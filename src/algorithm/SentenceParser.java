package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class SentenceParser {

	private static Pattern[] PATTERNS_TO_IGNORE = { Pattern.compile("the"), Pattern.compile("of"),
			Pattern.compile("to"), Pattern.compile("that"), Pattern.compile("a"), Pattern.compile("and"),
			Pattern.compile(" |\n|\t") };

	private static final Map<String, Float> STOPWORD_WEIGHTS = new HashMap<>();

	private static final String STOPWORD_FILE_LOCATION = "stopword_dictionary.txt";

	private static final String PARSER_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	private static final LexicalizedParser lp = LexicalizedParser.loadModel(PARSER_MODEL);

	static {

		try {

			loadStopWords();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

	}

	/**
	 * Does the magic.
	 * 
	 * @param input
	 *            The file to read from
	 * @return The list of words after it has removed unnecessary words
	 */
	public static String[] algorithm(String t, float minimumSimilarity) {

		long startTime = System.currentTimeMillis();

		t = t.toLowerCase();
		Reader reader = new StringReader(t);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {

			StringBuilder sentenceString = new StringBuilder();

			for (HasWord h : sentence) {

				sentenceString.append(h.toString() + " ");

			}

			sentenceList.add(sentenceString.toString());

		}

		StringBuilder output = new StringBuilder();

		for (String sentence : sentenceList) {

			Tree tree = lp.parse(sentence);
			CustomTree customTree = new CustomTree(tree, null);
			CustomTree originalTree = new CustomTree(customTree, null);

			List<String> removed = new ArrayList<>();

			while (treeSimilarity(originalTree, customTree) > minimumSimilarity) {

				List<CustomTree> possibleRemoval = customTree.leaves();
				double mostSimilarity = 0;
				CustomTree mostSimilarRemoval = null;
				CustomTree topCandidate = null;

				for (CustomTree candidate : possibleRemoval) {

					CustomTree t2 = new CustomTree(customTree, null);

					if (candidate.getParent().getChildren().size() == 1) {

						t2.removeLeaf(candidate.getParent());
						t2.removeLeaf(candidate);

					}

					else {

						t2.removeLeaf(candidate);

					}

					double similarity = treeSimilarity(customTree, t2);

					if (similarity > mostSimilarity) {

						mostSimilarity = similarity;
						mostSimilarRemoval = t2;
						topCandidate = candidate;

					}

				}

				removed.add(topCandidate.getData());

				customTree = mostSimilarRemoval;

			}

			removed.removeIf(new Predicate<String>() {

				@Override
				public boolean test(String t) {

					return Character.isUpperCase(t.charAt(0));

				}

			});

			String[] words = sentence.split(" ");

			for (String word : words) {

				if (!removed.contains(word.toLowerCase())) {

					output.append(word + " ");

				}

			}

		}

		ArrayList<String> textList = new ArrayList<>();

		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(output.toString()),
				new CoreLabelTokenFactory(), "");

		while (ptbt.hasNext()) {

			CoreLabel label = ptbt.next();

			boolean isUsefulWord = true;

			for (int j = 0; j < PATTERNS_TO_IGNORE.length; j++) {

				if (Pattern.matches(PATTERNS_TO_IGNORE[j].toString(), label.toString())) {

					isUsefulWord = false;
					break;

				}

			}

			if (isUsefulWord) {

				if (Pattern.matches("\\p{Punct}", label.toString())) {

					String temp = textList.remove(textList.size() - 1);
					temp += label.toString();
					textList.add(temp);

				}

				else {

					textList.add(label.toString());

				}

			}

		}

		StringBuilder text = new StringBuilder();

		textList.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {

				text.append(t + " ");

			}

		});

		System.out.println("Original: " + t.split(" ").length);
		System.out.println("Tree stuff reduced it to: " + output.toString().split(" ").length);
		System.out.println("Tree algo provided a "
				+ (1f - ((float) output.toString().split(" ").length) / ((float) t.split(" ").length)) + "% reduction");
		System.out.println("Final size: " + textList.size());
		System.out.println((1f - ((float) textList.size()) / ((float) output.toString().split(" ").length))
				+ "% reduction provided just by posprocessing");
		System.out.println((1f - ((float) textList.size()) / ((float) t.split(" ").length)) + "% reduction overall");

		System.out.println("After tree: " + output.toString());
		System.out.println("After postprocessing: " + textList);

		System.out.println("Algorithm took: " + (System.currentTimeMillis() - startTime) + " millis");
		System.out.println("Algorithm took: "
				+ (((float) (System.currentTimeMillis() - startTime)) / t.split(" ").length) + " millis / word");

		return textList.toArray(new String[textList.size()]);

		/*
		 * Make it return the right stuff and do the post processing (removing
		 * the regexs) and add polish and params and stuff
		 */

	}

	public static String[] preprocessThenAlgo(String t, float minimumSimilarity) {

		t = t.toLowerCase();

		ArrayList<String> textList = new ArrayList<>();

		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(t), new CoreLabelTokenFactory(), "");

		while (ptbt.hasNext()) {

			CoreLabel label = ptbt.next();

			boolean isUsefulWord = true;

			for (int j = 0; j < PATTERNS_TO_IGNORE.length; j++) {

				if (Pattern.matches(PATTERNS_TO_IGNORE[j].toString(), label.toString())) {

					isUsefulWord = false;
					break;

				}

			}

			if (isUsefulWord) {

				if (Pattern.matches("\\p{Punct}", label.toString())) {

					String temp = textList.remove(textList.size() - 1);
					temp += label.toString();
					textList.add(temp);

				}

				else {

					textList.add(label.toString());

				}

			}

		}

		StringBuilder text = new StringBuilder();

		textList.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {

				text.append(t + " ");

			}

		});

		Reader reader = new StringReader(text.toString());
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {

			StringBuilder sentenceString = new StringBuilder();

			for (HasWord h : sentence) {

				sentenceString.append(h.toString() + " ");

			}

			sentenceList.add(sentenceString.toString());

		}

		StringBuilder output = new StringBuilder();

		for (String sentence : sentenceList) {

			Tree tree = lp.parse(sentence);
			CustomTree customTree = new CustomTree(tree, null);
			CustomTree originalTree = new CustomTree(customTree, null);

			List<String> removed = new ArrayList<>();

			while (treeSimilarity(originalTree, customTree) > minimumSimilarity) {

				List<CustomTree> possibleRemoval = customTree.leaves();
				double mostSimilarity = 0;
				CustomTree mostSimilarRemoval = null;
				CustomTree topCandidate = null;

				for (CustomTree candidate : possibleRemoval) {

					CustomTree t2 = new CustomTree(customTree, null);

					if (candidate.getParent().getChildren().size() == 1) {

						t2.removeLeaf(candidate.getParent());
						t2.removeLeaf(candidate);

					}

					else {

						t2.removeLeaf(candidate);

					}

					double similarity = treeSimilarity(customTree, t2);

					if (similarity > mostSimilarity) {

						mostSimilarity = similarity;
						mostSimilarRemoval = t2;
						topCandidate = candidate;

					}

				}

				removed.add(topCandidate.getData());

				customTree = mostSimilarRemoval;

			}

			removed.removeIf(new Predicate<String>() {

				@Override
				public boolean test(String t) {

					return Character.isUpperCase(t.charAt(0));

				}

			});

			String[] words = sentence.split(" ");

			for (String word : words) {

				if (!removed.contains(word.toLowerCase())) {

					output.append(word + " ");

				}

			}

		}

		System.out.println("Original: " + t.split(" ").length);
		System.out.println("Preproccesing reduced it to: " + text.toString().split(" ").length);
		System.out.println("Preprocessing provided a "
				+ (1f - ((float) text.toString().split(" ").length / (float) t.split(" ").length)) + "% reduction");
		System.out.println("Final size (after tree): " + output.toString().split(" ").length);
		System.out.println("Tree provided: "
				+ (1f - ((float) output.toString().split(" ").length / (float) text.toString().split(" ").length))
				+ "% reduction");
		System.out.println("Overall reduction: "
				+ (1f - ((float) output.toString().split(" ").length / (float) t.split(" ").length)) + "%");

		System.out.println("After prprocessing: " + text.toString());
		System.out.println("After tree: " + output.toString());

		return output.toString().split(" ");

	}

	/*
	 * private static List<CustomTree> leafPairs(CustomTree t) {
	 * 
	 * List<CustomTree> leaves = new ArrayList<>();
	 * 
	 * for (CustomTree leaf : t.leaves()) {
	 * 
	 * if(!leaf.isLeaf()){
	 * 
	 * System.out.println("goteem");
	 * 
	 * }
	 * 
	 * leaves.add(leaf.getParent());
	 * 
	 * }
	 * 
	 * return leaves;
	 * 
	 * }
	 */

	private static File removeStopwords(File input, float calibrationConstant) {

		PTBTokenizer<CoreLabel> ptbt = null;

		StringBuilder output = new StringBuilder();

		try {

			ptbt = new PTBTokenizer<>(new FileReader(input), new CoreLabelTokenFactory(), "");

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

		while (ptbt.hasNext()) {

			String word = ptbt.next().toString();

			if (STOPWORD_WEIGHTS.containsKey(word) && STOPWORD_WEIGHTS.get(word) > calibrationConstant) {

				output.append(word + " ");

			}

		}

		File fileOut = new File("tempwostopwords.txt");

		try {

			FileWriter writer = new FileWriter(fileOut);
			writer.write(output.toString());
			writer.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return fileOut;

	}

	public static void loadStopWords() throws FileNotFoundException {

		// today i also used this python script to create a weighted list of the
		// most common words to be used for calibration and stoplisting:
		/*
		 * I changed the script to this (it now normalizes it) from nltk.corpus
		 * import gutenberg from nltk.corpus import reuters from collections
		 * import Counter import string
		 * 
		 * total_words = []
		 * 
		 * print("Size: " + str(len(gutenberg.fileids()) +
		 * len(reuters.fileids())))
		 * 
		 * i = 0
		 * 
		 * for fileid in gutenberg.fileids(): words = gutenberg.words(fileid)
		 * for w in words: if not (str(w) in string.punctuation or
		 * str(w).isdigit()): total_words.append(w) i += 1
		 * 
		 * for fileid in reuters.fileids(): words = reuters.words(fileid) for w
		 * in words: total_words.append(w)
		 * 
		 * i += 1
		 * 
		 * total_words = [str(w) for w in total_words] print("Total words: " +
		 * str(len(total_words))) counter = Counter(total_words)
		 * 
		 * total = sum(counter.values(), 0.0) for key in counter: counter[key]
		 * /= total
		 * 
		 * file = open("out.txt", "w") file.write(str(counter))
		 */
		// I also made a to do list if but its not stored with the project so
		// you wont be able to see it

		Scanner scan = new Scanner(new File(STOPWORD_FILE_LOCATION));

		String[] split = scan.nextLine().split(", ");

		scan.close();

		for (String s : split) {

			String[] keyValue = s.split(":");

			if (keyValue.length == 2) {

				float freq = Double.valueOf(keyValue[1].substring(1)).floatValue();
				String word = keyValue[0].substring(1, keyValue[0].length() - 1);

				STOPWORD_WEIGHTS.put(word, freq);

			}

		}

	}

	public static void treeWalk(Tree root) {

		if (root != null) {

			System.out.println(root.toString());

			for (Tree t : root.getChildrenAsList()) {

				treeWalk(t);

			}

		}

	}

	public static double treeSimilarity(CustomTree t1, CustomTree t2) {

		double numNodes = (countNodes(t1) + countNodes(t2)) / 2d;

		return recurse(t1, t2) / numNodes;

	}

	public static int recurse(CustomTree t1, CustomTree t2) {

		if (t1 != null && t2 != null) {

			int sum = (t1.getData().equals(t2.getData()) ? 1 : 0);

			for (int i = 0; i < t1.getChildren().size(); i++) {

				sum += recurse(t1.getChildren().get(i), i < t2.getChildren().size() ? t2.getChildren().get(i) : null);

			}

			return sum;

		}

		if (t1 != null && t2 != null) {

			return 0;

		}

		if (t1 == null && t2 == null) {

			return 1;

		}

		return 0;

	}

	public static int countNodes(CustomTree t) {

		if (t.isLeaf()) {

			return 1;

		}

		int sum = 1;

		for (CustomTree c : t.getChildren()) {

			sum += countNodes(c);

		}

		return sum;

	}

	/**
	 * demoDP demonstrates turning a file into tokens and then parse trees. Note
	 * that the trees are printed by calling pennPrint on the Tree object. It is
	 * also possible to pass a PrintWriter to pennPrint if you want to capture
	 * the output. This code will work with any supported language.
	 */
	public static void demoDP(LexicalizedParser lp, String filename) {
		// This option shows loading, sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a
																// PennTreebankLanguagePack
																// for English
		GrammaticalStructureFactory gsf = null;
		if (tlp.supportsGrammaticalStructures()) {
			gsf = tlp.grammaticalStructureFactory();
		}
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);
			parse.pennPrint();
			System.out.println();

			if (gsf != null) {
				GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
				@SuppressWarnings("rawtypes")
				Collection tdl = gs.typedDependenciesCCprocessed();
				System.out.println(tdl);
				System.out.println();
			}
		}
	}

	/*
	 * TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	 * GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	 * GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
	 * Collection<TypedDependency> tdl = gs.allTypedDependencies();
	 * Main.writeImage(tree, tdl, "image.png",3);
	 */

}
