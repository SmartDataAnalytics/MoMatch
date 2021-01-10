import org.apache.spark.rdd.RDD

class ClassSimilarity {
  /**
    * Get the similarity between two classes in two different ontologies.*/
  def GetClassSimilarity(O1Translated: RDD[(String, String)], O2: RDD[String]): RDD[(String, String, String, Double)]={
    val gS = new GetSimilarity()
    val t = O1Translated.cartesian(O2)
    val tt: RDD[(String, String, String, Double)] = t.map(x => (x._1._1, x._1._2, x._2,gS.getSimilarity(x._1._2.toLowerCase,x._2.toLowerCase))).filter(y=>y._4>0.9)
//    tt.foreach(println(_))
    tt.distinct(2)
  }

}
