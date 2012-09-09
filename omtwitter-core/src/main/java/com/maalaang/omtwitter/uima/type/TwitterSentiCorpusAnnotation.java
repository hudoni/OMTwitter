

/* First created by JCasGen Tue Aug 14 20:04:02 CEST 2012 */
package com.maalaang.omtwitter.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** A sentiment score annotation based on Twitter Sentiment Corpus
 * Updated by JCasGen Tue Aug 14 23:42:20 CEST 2012
 * XML source: E:/Workspaces/GitHub/omtwitter/omtwitter-core/src/main/resources/com/maalaang/omtwitter/uima/type/TwitterSentiCorpusAnnotation.xml
 * @generated */
public class TwitterSentiCorpusAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(TwitterSentiCorpusAnnotation.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TwitterSentiCorpusAnnotation() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TwitterSentiCorpusAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TwitterSentiCorpusAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TwitterSentiCorpusAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: positiveScore

  /** getter for positiveScore - gets positive score of this expression
   * @generated */
  public double getPositiveScore() {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_positiveScore == null)
      jcasType.jcas.throwFeatMissing("positiveScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_positiveScore);}
    
  /** setter for positiveScore - sets positive score of this expression 
   * @generated */
  public void setPositiveScore(double v) {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_positiveScore == null)
      jcasType.jcas.throwFeatMissing("positiveScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_positiveScore, v);}    
   
    
  //*--------------*
  //* Feature: negativeScore

  /** getter for negativeScore - gets negative score of this expression
   * @generated */
  public double getNegativeScore() {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_negativeScore == null)
      jcasType.jcas.throwFeatMissing("negativeScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_negativeScore);}
    
  /** setter for negativeScore - sets negative score of this expression 
   * @generated */
  public void setNegativeScore(double v) {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_negativeScore == null)
      jcasType.jcas.throwFeatMissing("negativeScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_negativeScore, v);}    
   
    
  //*--------------*
  //* Feature: subjectiveScore

  /** getter for subjectiveScore - gets subjective score of this expression
   * @generated */
  public double getSubjectiveScore() {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_subjectiveScore == null)
      jcasType.jcas.throwFeatMissing("subjectiveScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_subjectiveScore);}
    
  /** setter for subjectiveScore - sets subjective score of this expression 
   * @generated */
  public void setSubjectiveScore(double v) {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_subjectiveScore == null)
      jcasType.jcas.throwFeatMissing("subjectiveScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_subjectiveScore, v);}    
   
    
  //*--------------*
  //* Feature: objectiveScore

  /** getter for objectiveScore - gets objective score of this expression
   * @generated */
  public double getObjectiveScore() {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_objectiveScore == null)
      jcasType.jcas.throwFeatMissing("objectiveScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_objectiveScore);}
    
  /** setter for objectiveScore - sets objective score of this expression 
   * @generated */
  public void setObjectiveScore(double v) {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_objectiveScore == null)
      jcasType.jcas.throwFeatMissing("objectiveScore", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_objectiveScore, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets id of this expression
   * @generated */
  public int getId() {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets id of this expression 
   * @generated */
  public void setId(int v) {
    if (TwitterSentiCorpusAnnotation_Type.featOkTst && ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.TwitterSentiCorpusAnnotation");
    jcasType.ll_cas.ll_setIntValue(addr, ((TwitterSentiCorpusAnnotation_Type)jcasType).casFeatCode_id, v);}    
  }

    