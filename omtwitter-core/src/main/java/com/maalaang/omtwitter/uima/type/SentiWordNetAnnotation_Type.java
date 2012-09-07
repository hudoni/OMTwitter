
/* First created by JCasGen Tue Aug 14 20:01:44 CEST 2012 */
package com.maalaang.omtwitter.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** A sentiment score annotation based on SentiWordNet
 * Updated by JCasGen Tue Aug 14 23:42:15 CEST 2012
 * @generated */
public class SentiWordNetAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SentiWordNetAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SentiWordNetAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SentiWordNetAnnotation(addr, SentiWordNetAnnotation_Type.this);
  			   SentiWordNetAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SentiWordNetAnnotation(addr, SentiWordNetAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = SentiWordNetAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
 
  /** @generated */
  final Feature casFeat_positiveScore;
  /** @generated */
  final int     casFeatCode_positiveScore;
  /** @generated */ 
  public double getPositiveScore(int addr) {
        if (featOkTst && casFeat_positiveScore == null)
      jcas.throwFeatMissing("positiveScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_positiveScore);
  }
  /** @generated */    
  public void setPositiveScore(int addr, double v) {
        if (featOkTst && casFeat_positiveScore == null)
      jcas.throwFeatMissing("positiveScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_positiveScore, v);}
    
  
 
  /** @generated */
  final Feature casFeat_negativeScore;
  /** @generated */
  final int     casFeatCode_negativeScore;
  /** @generated */ 
  public double getNegativeScore(int addr) {
        if (featOkTst && casFeat_negativeScore == null)
      jcas.throwFeatMissing("negativeScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_negativeScore);
  }
  /** @generated */    
  public void setNegativeScore(int addr, double v) {
        if (featOkTst && casFeat_negativeScore == null)
      jcas.throwFeatMissing("negativeScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_negativeScore, v);}
    
  
 
  /** @generated */
  final Feature casFeat_subjectiveScore;
  /** @generated */
  final int     casFeatCode_subjectiveScore;
  /** @generated */ 
  public double getSubjectiveScore(int addr) {
        if (featOkTst && casFeat_subjectiveScore == null)
      jcas.throwFeatMissing("subjectiveScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_subjectiveScore);
  }
  /** @generated */    
  public void setSubjectiveScore(int addr, double v) {
        if (featOkTst && casFeat_subjectiveScore == null)
      jcas.throwFeatMissing("subjectiveScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_subjectiveScore, v);}
    
  
 
  /** @generated */
  final Feature casFeat_objectiveScore;
  /** @generated */
  final int     casFeatCode_objectiveScore;
  /** @generated */ 
  public double getObjectiveScore(int addr) {
        if (featOkTst && casFeat_objectiveScore == null)
      jcas.throwFeatMissing("objectiveScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_objectiveScore);
  }
  /** @generated */    
  public void setObjectiveScore(int addr, double v) {
        if (featOkTst && casFeat_objectiveScore == null)
      jcas.throwFeatMissing("objectiveScore", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_objectiveScore, v);}
    
  
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated */ 
  public int getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_id);
  }
  /** @generated */    
  public void setId(int addr, int v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "com.maalaang.omtwitter.uima.type.SentiWordNetAnnotation");
    ll_cas.ll_setIntValue(addr, casFeatCode_id, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public SentiWordNetAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_positiveScore = jcas.getRequiredFeatureDE(casType, "positiveScore", "uima.cas.Double", featOkTst);
    casFeatCode_positiveScore  = (null == casFeat_positiveScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_positiveScore).getCode();

 
    casFeat_negativeScore = jcas.getRequiredFeatureDE(casType, "negativeScore", "uima.cas.Double", featOkTst);
    casFeatCode_negativeScore  = (null == casFeat_negativeScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_negativeScore).getCode();

 
    casFeat_subjectiveScore = jcas.getRequiredFeatureDE(casType, "subjectiveScore", "uima.cas.Double", featOkTst);
    casFeatCode_subjectiveScore  = (null == casFeat_subjectiveScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_subjectiveScore).getCode();

 
    casFeat_objectiveScore = jcas.getRequiredFeatureDE(casType, "objectiveScore", "uima.cas.Double", featOkTst);
    casFeatCode_objectiveScore  = (null == casFeat_objectiveScore) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_objectiveScore).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Integer", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

  }
}



    