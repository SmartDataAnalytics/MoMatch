import org.apache.spark.rdd.RDD

/*
* Created by Shimaa Ibrahim 4 November 2019
* */ class RelationSimilarity {

//
  /**
    * Get the similarity between two relations from two ontologies in different natural languages (the second ontology in English).*/
  def GetRelationSimilarityNonEnglishWithEnglish(O1RelationsWithTranslations: RDD[(String, String)], O2Relations: RDD[(String)], threshold: Double): RDD[(String, String, String, Double)] = {
    var crossRelations = O1RelationsWithTranslations.cartesian(O2Relations)
//    println("crossRelations"+crossRelations.count())
//    crossRelations.foreach(println(_))
    val gS = new GetSimilarity()
    val p = new PreProcessing()
    var sim: RDD[(String, String, String, Double)] = crossRelations.map(x => (x._1._1, x._1._2, x._2,
      gS.getSimilarity(p.removeStopWordsFromEnglish(p.splitCamelCase(x._1._2).toLowerCase),
        p.removeStopWordsFromEnglish(p.splitCamelCase(x._2).toLowerCase)))).filter(y => y._4 >= threshold)
//    println("sim"+sim.count())
//    sim.foreach(println(_))
    sim.distinct(2)
  }
  /**
    * Get the similarity between two relations from two ontologies in different natural languages (the first ontology in English).*/
  def GetRelationSimilarityEnglishWithNonEnglish(O2RelationsWithTranslations: RDD[(String, String)], O1Relations: RDD[(String)], threshold: Double): RDD[(String, String, String, Double)] = {
    var crossRelations: RDD[(String, (String, String))] = O1Relations.cartesian(O2RelationsWithTranslations)
    //    println("crossRelations"+crossRelations.count())
    //    crossRelations.foreach(println(_))
    val gS = new GetSimilarity()
    val p = new PreProcessing()
    var sim: RDD[(String, String, String, Double)] = crossRelations.map(x => (x._1, x._2._1, x._2._2, gS.getSimilarity(p.removeStopWordsFromEnglish(p.splitCamelCase(x._1).toLowerCase), p.removeStopWordsFromEnglish(p.splitCamelCase(x._2._2).toLowerCase)))).filter(y => y._4 >= threshold)
    //    println("sim"+sim.count())
    //    sim.foreach(println(_))
    sim.distinct(2)
  }
  /**
    * Get the similarity between two relations from two ontologies in different natural languages (Non-English ontologies).*/
  def GetMultilingualRelationSimilarity(O1RelationsWithTranslations: RDD[(String, String)], O2RelationsWithTranslations: RDD[(String, String)], threshold: Double): RDD[(String,String, String, String, Double)] = {
    var crossRelations = O1RelationsWithTranslations.cartesian(O2RelationsWithTranslations)
    //    println("crossRelations"+crossRelations.count())
    //    crossRelations.foreach(println(_))
    val gS = new GetSimilarity()
    val p = new PreProcessing()
    var sim: RDD[(String, String, String, String, Double)] = crossRelations.map(x => (x._1._1, x._1._2, x._2._1, x._2._2,
      gS.getSimilarity(p.removeStopWordsFromEnglish(p.splitCamelCase(x._1._2).toLowerCase),
        p.removeStopWordsFromEnglish(p.splitCamelCase(x._2._2).toLowerCase)))).filter(y => y._5 >= threshold)
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
