

/* First created by JCasGen Fri Aug 10 12:06:06 CEST 2012 */
package com.maalaang.omtwitter.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Aug 10 12:23:35 CEST 2012
 * XML source: E:/Workspaces/GitHub/omtwitter/omtwitter-core/src/main/resources/com/maalaang/omtwitter/uima/type/TokenAnnotation.xml
 * @generated */
public class TokenAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(TokenAnnotation.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TokenAnnotation() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public TokenAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public TokenAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public TokenAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: posTag

  /** getter for posTag - gets the part-of-speech tag of this token
   * @generated */
  public String getPosTag() {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_posTag == null)
      jcasType.jcas.throwFeatMissing("posTag", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_posTag);}
    
  /** setter for posTag - sets the part-of-speech tag of this token 
   * @generated */
  public void setPosTag(String v) {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_posTag == null)
      jcasType.jcas.throwFeatMissing("posTag", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_posTag, v);}    
   
    
  //*--------------*
  //* Feature: stem

  /** getter for stem - gets the stem of the word
   * @generated */
  public String getStem() {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_stem);}
    
  /** setter for stem - sets the stem of the word 
   * @generated */
  public void setStem(String v) {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_stem == null)
      jcasType.jcas.throwFeatMissing("stem", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_stem, v);}    
   
    
  //*--------------*
  //* Feature: entityLabel

  /** getter for entityLabel - gets the label of the entity that contains this token
   * @generated */
  public String getEntityLabel() {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_entityLabel == null)
      jcasType.jcas.throwFeatMissing("entityLabel", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_entityLabel);}
    
  /** setter for entityLabel - sets the label of the entity that contains this token 
   * @generated */
  public void setEntityLabel(String v) {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_entityLabel == null)
      jcasType.jcas.throwFeatMissing("entityLabel", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_entityLabel, v);}    
   
    
  //*--------------*
  //* Feature: stopword

  /** getter for stopword - gets true if this token is a stopword, otherwise false
   * @generated */
  public boolean getStopword() {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_stopword == null)
      jcasType.jcas.throwFeatMissing("stopword", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_stopword);}
    
  /** setter for stopword - sets true if this token is a stopword, otherwise false 
   * @generated */
  public void setStopword(boolean v) {
    if (TokenAnnotation_Type.featOkTst && ((TokenAnnotation_Type)jcasType).casFeat_stopword == null)
      jcasType.jcas.throwFeatMissing("stopword", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((TokenAnnotation_Type)jcasType).casFeatCode_stopword, v);}    
  }

    