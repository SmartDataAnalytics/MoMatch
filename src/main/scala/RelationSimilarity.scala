import org.apache.spark.rdd.RDD

/*
* Created by Shimaa Ibrahim 4 November 2019
* */ class RelationSimilarity {

  /**
    * Get the similarity between two relations from two ontologies in different natural languages.*/
  def GetMultilingualRelationSimilarity(listOfRelationsInTargetOntology: RDD[(String)], listOfRelationsInSourceOntology: RDD[(String, String)], threshold: Double): RDD[(String, String, String, Double)] = {
    var crossRelations: RDD[(String, (String, String))] = listOfRelationsInTargetOntology.cartesian(listOfRelationsInSourceOntology)
//    println("crossRelations"+crossRelations.count())
//    crossRelations.foreach(println(_))
    val gS = new GetSimilarity()
    val p = new PreProcessing()
    var sim: RDD[(String, String, String, Double)] = crossRelations.map(x => (x._2._1, x._2._2, x._1, gS.getSimilarity(p.removeStopWordsFromEnglish(p.splitCamelCase(x._1).toLowerCase), p.removeStopWordsFromEnglish(p.splitCamelCase(x._2._2).toLowerCase)))).filter(y => y._4 >= threshold)
//    println("sim"+sim.count())
//    sim.foreach(println(_))
    sim.distinct(2)
  }

  /**
    * Get the similarity between two relations from two ontologies in the same natural languages.*/
  def GetMonolingualRelationSimilarity(listOfRelationsInTargetOntology: RDD[(String)], listOfRelationsInSourceOntology: RDD[String], threshold: Double): RDD[(String, String, Double)] = {
    var crossRelations: RDD[(String, String)] = listOfRelationsInTargetOntology.cartesian(listOfRelationsInSourceOntology)
    //    println("crossRelations"+crossRelations.count())
    //    crossRelations.foreach(println(_))
    val gS = new GetSimilarity()
    val p = new PreProcessing()
    var sim: RDD[(String, String, Double)] = crossRelations.map(x => (x._1, x._2, gS.getSimilarity(p.removeStopWordsFromEnglish(p.splitCamelCase(x._1).toLowerCase), p.removeStopWordsFromEnglish(p.splitCamelCase(x._2).toLowerCase)))).filter(y => y._3 > threshold)
    //    println("sim"+sim.count())
    //    sim.foreach(println(_))
    sim.distinct(2)
  }
}
