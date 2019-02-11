package classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author Jeff Lee & UPF
 * Classifies text into 4 different domains, each in English and
 * The domains are Attacks, Aviation, Earthquakes, and Trains
 *
 */
public class TextClassifier {

   StringToWordVector filter;
   Classifier naiveClassifier; 
   Instances trainInstances;
   Instances testInstances;

   public void initClassifier() {
        filter = new StringToWordVector();
        naiveClassifier = new NaiveBayes();        
   }
   
   public String classify(String txt) throws Exception {
        Instance instance;
        instance = new DenseInstance(2);
        instance.setValue(testInstances.attribute(0),txt);
        instance.setValue(testInstances.attribute(1),trainInstances.attribute(trainInstances.classIndex()).value(0));
        testInstances.add(instance);

        Instances filteredInstances = Filter.useFilter(testInstances, filter);
        System.out.println(testInstances.instance(0).stringValue(0));
        double index = naiveClassifier.classifyInstance(filteredInstances.instance(0));
        String className = filteredInstances.classAttribute().value((int)index);

        return className;
   }
   
   public void removeInstance() {
       testInstances.delete(0);
   }
   
   public void createTestInstances() {
        ArrayList fvClassVal = new ArrayList();
        String value;
        Enumeration enu = trainInstances.attribute(trainInstances.classIndex()).enumerateValues();
        while(enu.hasMoreElements()) {
            value = (String)enu.nextElement();
            fvClassVal.add(value);
        }        
        Attribute classAttribute = new Attribute("topic", fvClassVal);
        ArrayList fvWekaAttributes=new ArrayList();
        Attribute textAttribute = new Attribute("text",(Vector) null);
        fvWekaAttributes.add(textAttribute);       
        fvWekaAttributes.add(classAttribute);
        testInstances = new Instances("Rel", fvWekaAttributes, 1); 
   }
   
   public void loadTrainingInstances(String training_file) {
       
       try {
           trainInstances = new Instances(new BufferedReader(new FileReader(training_file)));
           int lastIndex = trainInstances.numAttributes() - 1;
           trainInstances.setClassIndex(lastIndex); 
           filter.setInputFormat(trainInstances);
           trainInstances = Filter.useFilter(trainInstances, filter);
           naiveClassifier.buildClassifier(trainInstances);
           
       } catch (FileNotFoundException ex) {
           Logger.getLogger(TextClassifier.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(TextClassifier.class.getName()).log(Level.SEVERE, null, ex);
       } catch (Exception ex) {
           Logger.getLogger(TextClassifier.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
   
   public static void main(String[] args) {
       try {

           String trainFile="./trainingdata.arff";

           TextClassifier classifier=new TextClassifier();
           classifier.initClassifier();
           classifier.loadTrainingInstances(trainFile);
           classifier.createTestInstances();
          
           String text;
           String topic;

           Scanner scanner = new Scanner(System.in);
           System.out.print("Enter text to classify, or 'quit' to end >>> ");
           text = scanner.nextLine();
           while(!text.equalsIgnoreCase("quit")) {
               topic = classifier.classify(text);
               System.out.println("Your text is about " + topic);
               classifier.removeInstance();
               System.out.print("Enter text to classify, or 'quit' to end >>> ");
               text = scanner.nextLine();
            }

           
       } catch (Exception ex) {
           Logger.getLogger(TextClassifier.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
    
}
