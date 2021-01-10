import org.apache.spark.rdd.RDD

/*
* Created by Shimaa Ibrahim 4 November 2019
* */ class RelationSimilarity {

  /**
    * Get the similarity between two relations in two different ontologies.*/
  def GetRelationSimilarity(listOfRelationsInTargetOntology: RDD[(String)], listOfRelationsInSourceOntology: RDD[(String, String)]): RDD[(String, String, String, Double)] = {
    var crossRelations: RDD[(String, (String, String))] = listOfRelationsInTargetOntology.cartesian(listOfRelationsInSourceOntology)
//    println("crossRelations"+crossRelations.count())
//    crossRelations.foreach(println(_))
    val gS = new GetSimilarity()
    val p = new PreProcessing()
    var sim: RDD[(String, String, String, Double)] = crossRelations.map(x => (x._2._1, x._2._2, x._1, gS.getSimilarity(p.removeStopWordsFromEnglish(p.splitCamelCase(x._1).toLowerCase), p.removeStopWordsFromEnglish(p.splitCamelCase(x._2._2).toLowerCase)))).filter(y => y._4 > 0.9)
//    println("sim"+sim.count())
//    sim.foreach(println(_))
    sim.distinct(2)
  }

}
