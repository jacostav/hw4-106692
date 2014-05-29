package edu.cmu.lti.f13.hw4.hw4_106692.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;




import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.cas.FSList;

import edu.cmu.lti.f13.hw4.hw4_106692.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_106692.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_106692.utils.Utils;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
		if (iter.isValid()) {
			iter.moveToNext();
			Document doc = (Document) iter.get();
			createTermFreqVector(jcas, doc);
		}

	}
	/**
	 * 
	 * @param jcas
	 * @param doc
	 */

	private void createTermFreqVector(JCas jcas, Document doc) {

		String docText = doc.getText();
		
		String[] strToken = docText.split(" ");
		HashMap<String,Integer> mapa =new HashMap <String,Integer>();
		
		for(int i=0;i<strToken.length;i++){
			
			if(mapa.containsKey(strToken[i])){
				Integer obj= mapa.get(strToken[i]);
				mapa.put(strToken[i], obj + 1);
				
				}
			else{
				
				mapa.put(strToken[i], 1);
			   }
			
		}
		ArrayList<Token> lista = new ArrayList<Token>();
		
		
		
		for(Map.Entry<String, Integer> s: mapa.entrySet()) {
			Token tok= new Token(jcas);
			tok.setText(s.getKey());
			tok.setFrequency(s.getValue());
			lista.add(tok);
		}
		
		Utils.fromCollectionToFSList(jcas, lista);
		FSList flista = Utils.fromCollectionToFSList(jcas, lista);
		
		doc.setTokenList(flista);
	}
}
