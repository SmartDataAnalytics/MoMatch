import org.apache.spark.rdd.RDD

class ClassSimilarity {
  /**
    * Get the similarity between two classes from two ontologies in different natural languages.*/
  def GetMultilingualClassSimilarity(O1Translated: RDD[(String, String)], O2: RDD[String], threshold: Double): RDD[(String, String, String, Double)]={
    val gS = new GetSimilarity()
    val t = O1Translated.cartesian(O2)
    val tt: RDD[(String, String, String, Double)] = t.map(x => (x._1._1, x._1._2, x._2,gS.getSimilarity(x._1._2.toLowerCase,x._2.toLowerCase))).filter(y=>y._4>= threshold)
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
