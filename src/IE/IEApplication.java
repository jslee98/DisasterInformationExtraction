package IE;

import classification.TextClassifier;
import gate.*;
import gate.util.GateException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeff Lee
 * Extract information from a text input
 * Works with English earthquake reports and Spanish aviation reports
 *
 */

public class IEApplication {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        boolean runLoop = true;

        String pathToNLP="./gapps/MyNLPApp.gapp";
        String pathToSpanishIE="./gapps/IE_AIRPLANE_ES.gapp";
        String pathToEnglishIE="./gapps/IE_QUAKE_EN.gapp";

        TextClassifier classifier = new TextClassifier();
        String classificationFile = "./trainingdata.arff";
        classifier.initClassifier();
        classifier.loadTrainingInstances(classificationFile);
        classifier.createTestInstances();

        while (runLoop) {
            try {
                Gate.init();
                CallMyGATEApp languageProcessor = new CallMyGATEApp();
                CallMyGATEApp spanishIE = new CallMyGATEApp();
                CallMyGATEApp englishIE = new CallMyGATEApp();

                // Load the Gate applications
                languageProcessor.loadMyGapp(pathToNLP);
                spanishIE.loadMyGapp(pathToSpanishIE);
                englishIE.loadMyGapp(pathToEnglishIE);

                System.out.print("Enter the text you would like to extract information from: ");
                String userInput = scan.nextLine();
                Document sampleDoc = Factory.newDocument(userInput);

                // Static test input
                // Document sampleDoc = Factory.newDocument("2005 October 08 03:50:40 UTC At least 86,000 people killed, more than 69,000 injured and extensive damage in northern Pakistan.The heaviest damage occurred in the Muzaffarabad area, Kashmir where entire villages were destroyedand at Uri where 80 percent of the town was destroyed. At least 32,335 buildings collapsed in Anantnag, Baramula, Jammu and Srinagar, Kashmir.");
                // Document sampleDoc = Factory.newDocument("2009  30 de junio: el vuelo 626 de Yemenia chocó en cercanías a Comoras, en el Océano Índico.");

                Corpus sampleCorpus= Factory.newCorpus("");
                sampleCorpus.add(sampleDoc);
                languageProcessor.setCorpus(sampleCorpus);
                languageProcessor.executeMyGapp();

                String sampleDomain = classifier.classify(userInput);
                String sampleLanguage = sampleDoc.getFeatures().get("lang").toString();

                if (sampleLanguage.equals("spanish") && sampleDomain.equals("aviation")) {
                    spanishIE.setCorpus(sampleCorpus);
                    spanishIE.executeMyGapp();
                    System.out.println("\n> Information found in the document:\n");
                    System.out.println("The sample document is about " + sampleDomain + " written in " + sampleLanguage + ".");
                    displayFoundInformation(sampleDoc);

                } else if (sampleLanguage.equals("english") && sampleDomain.equals("quake")) {
                    englishIE.setCorpus(sampleCorpus);
                    englishIE.executeMyGapp();
                    System.out.println("\n> Information found in the document:\n");
                    System.out.println("The sample document is about " + sampleDomain + " written in " + sampleLanguage + ".");
                    displayFoundInformation(sampleDoc);
                } else {
                    System.out.println("\nThe sample document is about " + sampleDomain + " written in " + sampleLanguage + ".");
                    System.out.println("Error: No information on your topic was able to be extracted in this language!\n");
                }

                Factory.deleteResource(sampleDoc);

                System.out.print("Type 'quit' to exit the application, or 'continue' to do another extraction: ");
                String exitString = scan.nextLine();
                if (exitString.equalsIgnoreCase("quit")) {
                    runLoop = false;
                    System.out.println("Thank you for using our information extraction application.");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        scan.close();
    }

    public static void displayFoundInformation(Document doc) {
        Annotation mention;
        String mentionType, mentionText;
        Long mentionStart, mentionEnd;

        AnnotationSet mentions = doc.getAnnotations().get("Mention");
        String documentContent = doc.getContent().toString();
        Iterator<Annotation> mentionIterator = mentions.iterator();

        while(mentionIterator.hasNext()) {
            mention = mentionIterator.next();
            mentionType = mention.getFeatures().get("type").toString();
            mentionStart = mention.getStartNode().getOffset();
            mentionEnd = mention.getEndNode().getOffset();
            mentionText = documentContent.substring(mentionStart.intValue(), mentionEnd.intValue());

            // display mention
            System.out.println(mentionType + " = " + mentionText);
        }
        System.out.println();
    }
}
