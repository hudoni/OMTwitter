
/* First created by JCasGen Fri Aug 10 12:06:06 CEST 2012 */
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

/** 
 * Updated by JCasGen Fri Aug 10 12:23:35 CEST 2012
 * @generated */
public class TokenAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TokenAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TokenAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TokenAnnotation(addr, TokenAnnotation_Type.this);
  			   TokenAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TokenAnnotation(addr, TokenAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = TokenAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.maalaang.omtwitter.uima.type.TokenAnnotation");
 
  /** @generated */
  final Feature casFeat_posTag;
  /** @generated */
  final int     casFeatCode_posTag;
  /** @generated */ 
  public String getPosTag(int addr) {
        if (featOkTst && casFeat_posTag == null)
      jcas.throwFeatMissing("posTag", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_posTag);
  }
  /** @generated */    
  public void setPosTag(int addr, String v) {
        if (featOkTst && casFeat_posTag == null)
      jcas.throwFeatMissing("posTag", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_posTag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_stem;
  /** @generated */
  final int     casFeatCode_stem;
  /** @generated */ 
  public String getStem(int addr) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_stem);
  }
  /** @generated */    
  public void setStem(int addr, String v) {
        if (featOkTst && casFeat_stem == null)
      jcas.throwFeatMissing("stem", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_stem, v);}
    
  
 
  /** @generated */
  final Feature casFeat_entityLabel;
  /** @generated */
  final int     casFeatCode_entityLabel;
  /** @generated */ 
  public String getEntityLabel(int addr) {
        if (featOkTst && casFeat_entityLabel == null)
      jcas.throwFeatMissing("entityLabel", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_entityLabel);
  }
  /** @generated */    
  public void setEntityLabel(int addr, String v) {
        if (featOkTst && casFeat_entityLabel == null)
      jcas.throwFeatMissing("entityLabel", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_entityLabel, v);}
    
  
 
  /** @generated */
  final Feature casFeat_stopword;
  /** @generated */
  final int     casFeatCode_stopword;
  /** @generated */ 
  public boolean getStopword(int addr) {
        if (featOkTst && casFeat_stopword == null)
      jcas.throwFeatMissing("stopword", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_stopword);
  }
  /** @generated */    
  public void setStopword(int addr, boolean v) {
        if (featOkTst && casFeat_stopword == null)
      jcas.throwFeatMissing("stopword", "com.maalaang.omtwitter.uima.type.TokenAnnotation");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_stopword, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public TokenAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_posTag = jcas.getRequiredFeatureDE(casType, "posTag", "uima.cas.String", featOkTst);
    casFeatCode_posTag  = (null == casFeat_posTag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_posTag).getCode();

 
    casFeat_stem = jcas.getRequiredFeatureDE(casType, "stem", "uima.cas.String", featOkTst);
    casFeatCode_stem  = (null == casFeat_stem) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stem).getCode();

 
    casFeat_entityLabel = jcas.getRequiredFeatureDE(casType, "entityLabel", "uima.cas.String", featOkTst);
    casFeatCode_entityLabel  = (null == casFeat_entityLabel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_entityLabel).getCode();

 
    casFeat_stopword = jcas.getRequiredFeatureDE(casType, "stopword", "uima.cas.Boolean", featOkTst);
    casFeatCode_stopword  = (null == casFeat_stopword) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stopword).getCode();

  }
}



    