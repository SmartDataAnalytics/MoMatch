import org.apache.spark.rdd.RDD

class ClassSimilarity {
//  /**
//    * Get the similarity between two classes from two ontologies in different natural languages.*/
//  def GetClassSimilarityNonEnglishWithEnglish(O1Classes: RDD[String], O1ClassesWithTranslation: RDD[(String, String)], naturalLanguage1:String, O2Classes: RDD[String],O2ClassesWithTranslation: RDD[(String, String)], naturalLanguage2:String, threshold: Double): RDD[(String, String, String, Double)]={
//    val gS = new GetSimilarity()
//    val t = O1ClassesWithTranslation.cartesian(O2Classes)
//    val tt: RDD[(String, String, String, Double)] = t.map(x => (x._1._1, x._1._2, x._2,gS.getSimilarity(x._1._2.toLowerCase,x._2.toLowerCase))).filter(y=>y._4>= threshold)
////    tt.foreach(println(_))
//    tt.distinct(2)
//  }


  /**
    * Get the similarity between two classes from two ontologies in different natural languages (the second ontology in English).*/
  def GetClassSimilarityNonEnglishWithEnglish(O1ClassesWithTranslation: RDD[(String, String)], O2Classes: RDD[String], threshold: Double): RDD[(String, String, String, Double)]={
    val gS = new GetSimilarity()
    val t = O1ClassesWithTranslation.cartesian(O2Classes)
    val tt: RDD[(String, String, String, Double)] = t.map(x => (x._1._1, x._1._2, x._2,gS.getSimilarity(x._1._2.toLowerCase,x._2.toLowerCase))).filter(y=>y._4>= threshold)
    //    tt.foreach(println(_))
    tt.distinct(2)
  }
  /**
    * Get the similarity between two classes from two ontologies in different natural languages (the first ontology in English).*/
  def GetClassSimilarityEnglishWithNonEnglish(O1Classes: RDD[String], O2ClassesWithTranslation: RDD[(String, String)], threshold: Double): RDD[(String, String, String, Double)]={
    val gS = new GetSimilarity()
    val t = O1Classes.cartesian(O2ClassesWithTranslation)
    val tt: RDD[(String,  String, String, Double)] = t.map(x => (x._1, x._2._1, x._2._2,gS.getSimilarity(x._1.toLowerCase,x._2._2.toLowerCase))).filter(y=>y._4>= threshold)
    //    tt.foreach(println(_))
    tt.distinct(2)
  }

  /**
    * Get the similarity between two classes from two ontologies in different natural languages (Non-English ontologies).*/
  def GetMultilingualClassSimilarity(O1ClassesWithTranslation: RDD[(String, String)], O2ClassesWithTranslation: RDD[(String, String)], threshold: Double): RDD[(String,String, String, String, Double)]={
    val gS = new GetSimilarity()
    val t = O1ClassesWithTranslation.cartesian(O2ClassesWithTranslation)
    val tt: RDD[(String, String,  String, String, Double)] = t.map(x => (x._1._1, x._1._2, x._2._1, x._2._2,gS.getSimilarity(x._1._2.toLowerCase,
      x._2._2.toLowerCase))).filter(y=>y._5>= threshold)
    //    tt.foreach(println(_))
    tt.distinct(2)
  }

  /**
    * Get the similarity between two classes from two ontologies in the same natural languages.*/
  def GetMonolingualClassSimilarity(O1: RDD[String], O2: RDD[String], threshold: Double): RDD[(String, String, Double)]={
    val gS = new GetSimilarity()
    val t = O1.cartesian(O2)
    val tt = t.map(x => (x._1, x._2, gS.getSimilarity(x._1.toLowerCase,x._2.toLowerCase))).filter(y=>y._3>= threshold)
      // tt.foreach(println(_))
    tt.distinct(2)
  }

}
