
/* First created by JCasGen Fri Aug 10 12:48:42 CEST 2012 */
package com.maalaang.omtwitter.uima.type;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Single tweet annotation
 * Updated by JCasGen Mon Sep 10 22:43:33 CEST 2012
 * @generated */
public class TweetAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TweetAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TweetAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TweetAnnotation(addr, TweetAnnotation_Type.this);
  			   TweetAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TweetAnnotation(addr, TweetAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = TweetAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.maalaang.omtwitter.uima.type.TweetAnnotation");
 
  /** @generated */
  final Feature casFeat_author;
  /** @generated */
  final int     casFeatCode_author;
  /** @generated */ 
  public String getAuthor(int addr) {
        if (featOkTst && casFeat_author == null)
      jcas.throwFeatMissing("author", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_author);
  }
  /** @generated */    
  public void setAuthor(int addr, String v) {
        if (featOkTst && casFeat_author == null)
      jcas.throwFeatMissing("author", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_author, v);}
    
  
 
  /** @generated */
  final Feature casFeat_polarity;
  /** @generated */
  final int     casFeatCode_polarity;
  /** @generated */ 
  public String getPolarity(int addr) {
        if (featOkTst && casFeat_polarity == null)
      jcas.throwFeatMissing("polarity", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_polarity);
  }
  /** @generated */    
  public void setPolarity(int addr, String v) {
        if (featOkTst && casFeat_polarity == null)
      jcas.throwFeatMissing("polarity", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_polarity, v);}
    
  
 
  /** @generated */
  final Feature casFeat_query;
  /** @generated */
  final int     casFeatCode_query;
  /** @generated */ 
  public String getQuery(int addr) {
        if (featOkTst && casFeat_query == null)
      jcas.throwFeatMissing("query", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_query);
  }
  /** @generated */    
  public void setQuery(int addr, String v) {
        if (featOkTst && casFeat_query == null)
      jcas.throwFeatMissing("query", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_query, v);}
    
  
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_date;
  /** @generated */
  final int     casFeatCode_date;
  /** @generated */ 
  public String getDate(int addr) {
        if (featOkTst && casFeat_date == null)
      jcas.throwFeatMissing("date", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_date);
  }
  /** @generated */    
  public void setDate(int addr, String v) {
        if (featOkTst && casFeat_date == null)
      jcas.throwFeatMissing("date", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_date, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TweetAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_author = jcas.getRequiredFeatureDE(casType, "author", "uima.cas.String", featOkTst);
    casFeatCode_author  = (null == casFeat_author) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_author).getCode();

 
    casFeat_polarity = jcas.getRequiredFeatureDE(casType, "polarity", "uima.cas.String", featOkTst);
    casFeatCode_polarity  = (null == casFeat_polarity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_polarity).getCode();

 
    casFeat_query = jcas.getRequiredFeatureDE(casType, "query", "uima.cas.String", featOkTst);
    casFeatCode_query  = (null == casFeat_query) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_query).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_date = jcas.getRequiredFeatureDE(casType, "date", "uima.cas.String", featOkTst);
    casFeatCode_date  = (null == casFeat_date) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_date).getCode();

  }
}



    