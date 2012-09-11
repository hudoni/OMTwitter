

/* First created by JCasGen Fri Aug 10 12:48:42 CEST 2012 */
package com.maalaang.omtwitter.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** Single tweet annotation
 * Updated by JCasGen Mon Sep 10 22:43:33 CEST 2012
 * XML source: E:/Workspaces/GitHub/OMTwitter/omtwitter-core/src/main/resources/com/maalaang/omtwitter/uima/type/TweetAnnotation.xml
 * @generated */
public class TweetAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(TweetAnnotation.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TweetAnnotation() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TweetAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TweetAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TweetAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: author

  /** getter for author - gets user screen name of a tweet
   * @generated */
  public String getAuthor() {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_author == null)
      jcasType.jcas.throwFeatMissing("author", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_author);}
    
  /** setter for author - sets user screen name of a tweet 
   * @generated */
  public void setAuthor(String v) {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_author == null)
      jcasType.jcas.throwFeatMissing("author", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_author, v);}    
   
    
  //*--------------*
  //* Feature: polarity

  /** getter for polarity - gets overall sentiment polarity of a tweet [POS|NEG|NEU]
   * @generated */
  public String getPolarity() {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_polarity);}
    
  /** setter for polarity - sets overall sentiment polarity of a tweet [POS|NEG|NEU] 
   * @generated */
  public void setPolarity(String v) {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_polarity, v);}    
   
    
  //*--------------*
  //* Feature: query

  /** getter for query - gets query words that were used for collecting a tweet
   * @generated */
  public String getQuery() {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_query);}
    
  /** setter for query - sets query words that were used for collecting a tweet 
   * @generated */
  public void setQuery(String v) {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_query == null)
      jcasType.jcas.throwFeatMissing("query", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_query, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets ID of a tweet
   * @generated */
  public String getId() {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets ID of a tweet 
   * @generated */
  public void setId(String v) {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: date

  /** getter for date - gets String representation of the date when a tweet was posted
   * @generated */
  public String getDate() {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_date == null)
      jcasType.jcas.throwFeatMissing("date", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_date);}
    
  /** setter for date - sets String representation of the date when a tweet was posted 
   * @generated */
  public void setDate(String v) {
    if (TweetAnnotation_Type.featOkTst && ((TweetAnnotation_Type)jcasType).casFeat_date == null)
      jcasType.jcas.throwFeatMissing("date", "com.maalaang.omtwitter.uima.type.TweetAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TweetAnnotation_Type)jcasType).casFeatCode_date, v);}    
  }

    