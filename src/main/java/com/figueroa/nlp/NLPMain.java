package com.figueroa.nlp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 *
 * @author gfigueroa
 */
public class NLPMain {

	// Resources
	private final static String RESOURCES_PATH = 
			"resources" + File.separator;
	
    // POSTagger and Lemmatizer
    private final static String POS_TAGGER_MODEL_PATH =
    		RESOURCES_PATH +
            "pos_models" + File.separator + "english-left3words-distsim.tagger";
    
    private final static String POS_TAGGER_CONFIG_PATH =
    		RESOURCES_PATH +
            "pos_models" +  File.separator + "english-left3words-distsim.tagger.props";
    
    private final static String TAG_SEPARATOR = "_";
    private final static String WN_HOME = RESOURCES_PATH + "WordNet-3.0";
    
    private POSTagger posTagger;
    private Lemmatizer lemmatizer;

    /**
     * Initialize the NLPMain class with the context path of the application.
     * Initializes all required classes for the NLP tools.
     * TODO: Make this initialization more efficient (once for entire app)
     */
    public NLPMain() {
    	
    	// Load POSTagger
        posTagger = new POSTagger(POS_TAGGER_MODEL_PATH,
                POS_TAGGER_CONFIG_PATH, TAG_SEPARATOR);
        // Load Lemmatizer
        lemmatizer = new Lemmatizer(WN_HOME, posTagger);
    }
    
    private void stemCSVFiles(String dataDirectory, String tokenSeparator) {
        File directory = new File(dataDirectory);
        File fileList[] = directory.listFiles();

        System.out.println("***************************************************");
        System.out.println("Lemmatizing text files for directory: " + dataDirectory +
                "...");
        System.out.println("***************************************************");
        System.out.println();

        for (int i = 0; i < fileList.length; i++) {

            File currFile = fileList[i];
            System.out.println("Processing file: " +
                    currFile.getName() +
                    " (" + (i + 1) + "/" + fileList.length + ")...");

            File lemmatizedFile = new File(currFile.getPath() + ".lemmatized");
            try {
                // File to read
                FileInputStream fis = new FileInputStream(currFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(bis));

                // File to write
                if (!lemmatizedFile.exists()) {
                    lemmatizedFile.createNewFile();
                }
                FileWriter fw = new FileWriter(lemmatizedFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                // If abstract is divided into two or more paragraphs, these are combined.
                while (br.ready()) {
                    String line = br.readLine();
                    String[] tokens = line.split(tokenSeparator);
                    String lemmatizedLine = tokens[0] + tokenSeparator +
                            tokens[1] + tokenSeparator;
                    
                    for (int j = 2; j < tokens.length; j++) { // First two tokens are IDs
                        String token = tokens[j];
                        String lemmatizedToken = lemmatizer.stemText(token, false);
                        lemmatizedLine += lemmatizedToken + "\t";
                    }
                    bw.write(lemmatizedLine + "\n");
                }

                fis.close();
                bis.close();
                br.close();
                bw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println();
        }
        System.out.println("***************************************************");
        System.out.println("Text lemmatized successfully for files in directory: " + dataDirectory);
    }

    private void stemFiles(String dataDirectory) {
        File directory = new File(dataDirectory);
        File fileList[] = directory.listFiles();

        System.out.println("***************************************************");
        System.out.println("Lemmatizing text files for directory: " + dataDirectory +
                "...");
        System.out.println("***************************************************");
        System.out.println();

        for (int i = 0; i < fileList.length; i++) {

            File currFile = fileList[i];
            System.out.println("Processing file: " +
                    currFile.getName() +
                    " (" + (i + 1) + "/" + fileList.length + ")...");

            File lemmatizedFile = new File(currFile.getPath() + ".lemmatized");
            try {
                // File to read
                FileInputStream fis = new FileInputStream(currFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(bis));

                // File to write
                if (!lemmatizedFile.exists()) {
                    lemmatizedFile.createNewFile();
                }
                FileWriter fw = new FileWriter(lemmatizedFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                // If abstract is divided into two or more paragraphs, these are combined.
                while (br.ready()) {
                    String line = br.readLine();

                    String lemmatizedLine = lemmatizer.stemText(line, false);
                    bw.write(lemmatizedLine + "\n");
                }

                fis.close();
                bis.close();
                br.close();
                bw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println();
        }
        System.out.println("***************************************************");
        System.out.println("Text lemmatized successfully for files in directory: " + dataDirectory);
    }

    /**
     * Lemmatize each term in a given string.
     * @param text
     * @return the lemmatized text
     * @throws Exception
     */
    public String lemmatizeText(String text) throws Exception {
        String lemmatizedText = lemmatizer.stemText(text, false);
        return lemmatizedText;
    }
    
    /**
     * Assign POS tags to each term in the given string. 
     * Uses the Penn Treebank tagset.
     * @param text
     * @return the POS-tagged text
     * @throws Exception
     */
    public String tagText(String text) throws Exception {
        String taggedText = posTagger.tagText(text);
        return taggedText;
    }
    
    /**
     * Main entry point for application.
     * @param args:
     * arg1: operation - can be "-lemmatize"/"-l" or "-tag"/"-t"
     * arg2: text - the text to perform the operation on
     */
    public static void main(String[] args) {
    	
    	if (args.length >= 2) {

    		try {
	    		String operation = args[0];
	    		String text = args[1];
	    		String processedText = "";
	    		if (operation.equalsIgnoreCase("-lemmatize") || 
	    				operation.equalsIgnoreCase("-l")) {
	    			NLPMain nlpMain = new NLPMain();
	    			processedText = nlpMain.lemmatizeText(text);
	    		}
	    		else if (operation.equalsIgnoreCase("-tag") ||
	    				operation.equalsIgnoreCase("-t")) {
	    			NLPMain nlpMain = new NLPMain();
	    			processedText = nlpMain.tagText(text);
	    		}
	    		else {
	    			System.err.println("Wrong arguments. Usage:\n"
	    					+ "lemmatizer-tagger -l text\n"
	    					+ "lemmatizer-tagger -t text");
	    			System.exit(1);
	    		}
	    		
	    		System.out.println(processedText);
    		}
    		catch (Exception e) {
    			System.err.println("Internal error: " + e.getMessage());
    			e.printStackTrace();
    		}
    	}
    	else {
			System.err.println("Wrong arguments. Usage:\n"
					+ "lemmatizer-tagger -l text\n"
					+ "lemmatizer-tagger -t text");
    	}
    	
    }
}