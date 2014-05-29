package edu.cmu.lti.f13.hw4.hw4_106692.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f13.hw4.hw4_106692.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_106692.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_106692.utils.*;



public class RetrievalEvaluator extends CasConsumer_ImplBase {

	/** query id number **/
	public ArrayList<Integer> qIdList;

	/** query and text relevant values **/
	public ArrayList<Integer> relList;
	
	public ArrayList<Integer> rankList;
	
	public ArrayList<Document> docList;
	
	public ArrayList< Map<String, Integer> > mapList;
	
	public ArrayList< Entry<Double, Integer> > currentDocuments;

		
	public void initialize() throws ResourceInitializationException {

		qIdList = new ArrayList<Integer>();

		relList = new ArrayList<Integer>();

		rankList = new ArrayList<Integer>();
		
		docList = new ArrayList<Document>();
		
		mapList = new ArrayList< Map<String, Integer> >();
		
		currentDocuments = new ArrayList< Entry<Double, Integer> > ();
	}

	/**
	 * TODO :: 1. construct the global word dictionary 2. keep the word
	 * frequency for each sentence
	 */
	@Override
	public void processCas(CAS aCas) throws ResourceProcessException {
		JCas jcas;
		try {
			jcas =aCas.getJCas();
		} catch (CASException e) {
			throw new ResourceProcessException(e);
		}

		FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();
	
		if (it.hasNext()) {
			Document doc = (Document) it.next();

			//Make sure that your previous annotators have populated this in CAS
			FSList fsTokenList = doc.getTokenList();
			//ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);

			qIdList.add(doc.getQueryID());
			relList.add(doc.getRelevanceValue());
			docList.add(doc);
			mapList.add(MapSimilarity(doc.getTokenList()));
			//Do something useful here
		}
	}

	/**
	 * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2.
	 * Compute the MRR metric
	 */
	@Override
	public void collectionProcessComplete(ProcessTrace arg0)
			throws ResourceProcessException, IOException {

		super.collectionProcessComplete(arg0);
		Map<String, Integer> queryMap= null, docMap =null;
		
		// TODO :: compute the cosine similarity measure
		// TODO :: compute the rank of retrieved sentences
		for(int i=0; i<relList.size(); i++){
			if(relList.get(i)==99 ) {
				if(currentDocuments.size()> 0) {
					sort(currentDocuments);
					int rank = getRank(currentDocuments);
					rankList.add(rank);
				}
				currentDocuments.clear();
				queryMap = mapList.get(i);
			}
			else {
				docMap=mapList.get(i);
				double doble = computeCosineSimilarity(queryMap,docMap);
				int relevancevalue = relList.get(i);
				Entry <Double, Integer> conjunto = new MyEntry<Double,Integer>(doble,relevancevalue);
				currentDocuments.add(conjunto);
			}
		}
		if(currentDocuments.size()> 0) {
			sort(currentDocuments);
			int rank = getRank(currentDocuments);
			rankList.add(rank);
		}
		currentDocuments.clear();
		
		// TODO :: compute the metric:: mean reciprocal rank
		double metric_mrr = compute_mrr();
		System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
	}
	
	public Map<String,Integer> MapSimilarity (FSList list){
		ArrayList<Token> token = Utils.fromFSListToCollection(list,Token.class);
	    HashMap<String,Integer> mapaTokens = new HashMap<String,Integer>();
		
		for(Token t: token) {
			mapaTokens.put(t.getText(), t.getFrequency());
		}
		
		return mapaTokens;
	}

	/**
	 * Compute cosine similarity between two sentences
	 * @return cosine_similarity
	 */
	private double computeCosineSimilarity(Map<String, Integer> queryVector,
			Map<String, Integer> docVector) {
		double cosine_similarity=0.0;
		double normA = computeNorm(queryVector);
		double normB = computeNorm(docVector);
		
		Set<String> set = new HashSet<String>(queryVector.keySet()); 
		set.retainAll(docVector.keySet());
		double dob= 0;
		
		for(String s:set){
			int i= queryVector.get(s);
			int i2 = docVector.get(s);
			dob += i * i2;
		}
		
		cosine_similarity = dob/(normA*normB);

		return cosine_similarity;
	}
	
	private double computeNorm(Map<String, Integer> mapa) {
		double dob2 =0;
		for(Integer i:mapa.values()){
			dob2 +=i*i;
		}
		dob2=Math.sqrt(dob2);
		
		return dob2;
	}

	/**
	 * 
	 * @return mrr
	 */
	private double compute_mrr() {
		double metric_mrr=0.0;
      
		for(int i=0;i< rankList.size();i++)
		   metric_mrr += 1.0/(double)rankList.get(i);
		   
		metric_mrr = metric_mrr/(double)rankList.size();	
		
		return metric_mrr;
	}

	public void sort(ArrayList< Entry<Double, Integer>> arr) {
		Collections.sort(arr, new Comparator<Entry<Double, Integer>> () {
			@Override
			public int compare(Entry<Double, Integer> arg0,
					Entry<Double, Integer> arg1) {
				return -1 * Double.compare(arg0.getKey(), arg1.getKey());
			}
			
		});
	}
	
	public int getRank(ArrayList< Entry<Double, Integer>> arr) {
		
		for(int i=0;i<arr.size();i++){
		    if (arr.get(i).getValue()==1){
		 
		    	return i+1;
		   
		    }
		    	
		}
		
		return 0;
	}
}
