import org.apache.jena.graph
import org.apache.jena.graph.Node
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

/*
* Created by Shimaa 14.Jan.2021
* */ class Match(sparkSession1: SparkSession) {
  var numberOfMatchedClasses = 0
  var numberOfMatchedRelations = 0
  var matchedClasses = sparkSession1.sparkContext.emptyRDD[(String, String, String, Double)]
  var matchedRelations = sparkSession1.sparkContext.emptyRDD[(String, String, String, Double)]
  var matchedNonEnglishClasses = sparkSession1.sparkContext.emptyRDD[(String, String, String, String, Double)]
  var matchedNonEnglishRelations = sparkSession1.sparkContext.emptyRDD[(String, String, String, String, Double)]
  val ontStat = new OntologyStatistics(sparkSession1)

  /**
    * Match two ontologies in two different natural languages.
    */
  def MatchOntologies(O1triples: RDD[graph.Triple], O2triples: RDD[graph.Triple], O1Name: String, O2Name: String, naturalLanguage1: String, naturalLanguage2: String, IsCrosslingual: Boolean, threshold: Double) = {
    val ontStat = new OntologyStatistics(sparkSession1)
    //    ontStat.getStatistics(O1triples)
    //    ontStat.getStatistics(O2triples)
    val ontoRebuild = new OntologyRebuilding(sparkSession1)
    val p = new PreProcessing()


    val O1Ontology: RDD[(String, String, String)] = ontoRebuild.RebuildOntology(O1triples)
    //    val tOntology = ontoRebuild.RebuildOntologyWithoutCodes(O2triples)
    val O2Ontology = ontoRebuild.RebuildOntology(O2triples)

    println("======================================")
    println("|     Resources Extraction     |")
    println("======================================")
    // Retrieve class and relation labels for input ontologies
    //    val O1Classes: RDD[(String, String)] = ontStat.retrieveClassesWithCodesAndLabels(O1triples) //applied for ontologies with codes like Multifarm ontologies
    val O1Classes: RDD[String] = ontStat.getAllClasses(O1triples).distinct(2) //applied for ontologies with codes like Multifarm ontologies
    println("====================================== All classes in O1 ======================================")
    O1Classes.foreach(println(_))
    val O1Labels: Map[Node, graph.Triple] = O1triples.filter(x => x.getPredicate.getLocalName == "label").keyBy(_.getSubject).collect().toMap
    val O1LabelsBroadcasting: Broadcast[Map[Node, graph.Triple]] = sparkSession1.sparkContext.broadcast(O1Labels)
    val O1Relations: RDD[String] = ontStat.getAllRelations(O1LabelsBroadcasting, O1triples).map(x => x._2) //    val O1Relations: RDD[String] = ontStat.getAllRelationsOld(O1triples).map(x => x._1)
    println("====================================== All relations in O1 ======================================")
    O1Relations.take(10).foreach(println(_))

    val O2Classes: RDD[String] = ontStat.getAllClasses(O2triples).map(x => p.stringPreProcessing(x)).persist(StorageLevel.MEMORY_AND_DISK).distinct(2) //For SEO
    //    val O2Classes: RDD[(String)] = ontStat.retrieveClassesWithCodesAndLabels(O2triples).map(x=>x._2).persist(StorageLevel.MEMORY_AND_DISK) //For Cmt and Multifarm dataset
    println("====================================== All classes in O2Classes ======================================")
    O2Classes.foreach(println(_))
              val O2Relations: RDD[(String)] = ontStat.getAllRelationsOld(O2triples).map(x => p.stringPreProcessing(x._1))
    val O2Labels: Map[Node, graph.Triple] = O2triples.filter(x => x.getPredicate.getLocalName == "label").keyBy(_.getSubject).collect().toMap
    val O2LabelsBroadcasting: Broadcast[Map[Node, graph.Triple]] = sparkSession1.sparkContext.broadcast(O2Labels)
//    val O2Relations: RDD[(String)] = ontStat.getAllRelations(O2LabelsBroadcasting, O2triples).map(x => p.stringPreProcessing(x._2))
    println("====================================== All relations in O2Classes ======================================")
    O2Relations.take(10).foreach(println(_))

    if (IsCrosslingual == true) {
//            if (naturalLanguage1 != "English")
//              TranslateOntologyResources(O1triples, O1Classes,O1Relations)
//            else if (naturalLanguage2 != "English")
//              TranslateOntologyResources(O2triples, O2Classes,O2Relations)
      println("================================ Cross-lingual Matching ======================================")
      this.CrossLingualMatching(O1Name, O1Classes, O1Relations, naturalLanguage1, O2Name, O2Classes, O2Relations, naturalLanguage2, threshold) //      val e = new Evaluation(sparkSession1)
      //      e.Evaluate()
    } else {
      println("================================ Monolingual Matching ======================================")
      this.MonolingualMatching(O1Classes, O1Relations, O2Classes, O2Relations, threshold)
    }
  }

  /**
    * Automatic translation for input ontology using Yandex API.
    */
  def TranslateOntologyResources(Otriples: RDD[graph.Triple], OClasses: RDD[String], ORelations: RDD[(String)]) = {
    val languageTag = Translation.languageDetection(ontStat.getAllClasses(Otriples).first())
    Translation.translateToEnglish(OClasses, ORelations, languageTag)
  }

  /**
    * Get the cross-lingual matching between two ontologies in twi different natural languages.
    */
  def CrossLingualMatching(O1Name: String, O1Classes: RDD[String], O1Relations: RDD[String], naturalLanguage1: String, O2Name: String, O2Classes: RDD[String], O2Relations: RDD[(String)], naturalLanguage2: String, threshold: Double) = {
    println("Threshold = "+ threshold)
    var O1ClassesWithTranslations: RDD[(String, String)] = sparkSession1.sparkContext.emptyRDD[(String, String)]
    var O1RelationsWithTranslations: RDD[(String, String)] = sparkSession1.sparkContext.emptyRDD[(String, String)]
    var O2ClassesWithTranslations: RDD[(String, String)] = sparkSession1.sparkContext.emptyRDD[(String, String)]
    var O2RelationsWithTranslations: RDD[(String, String)] = sparkSession1.sparkContext.emptyRDD[(String, String)]
    val sim = new ClassSimilarity()
    val relSim = new RelationSimilarity()

    if (naturalLanguage1 != "English") {
      println("Translate first ontology:")
      O1ClassesWithTranslations = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/" + O1Name + "/classesWithTranslation.txt")
        .map(x => (x.split(",").apply(0), x.split(",").apply(1)))
      O1RelationsWithTranslations = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/" + O1Name + "/RelationsWithTranslation.txt")
        .map(x => (x.split(",").apply(0), x.split(",").apply(1)))
    }
    if (naturalLanguage2 != "English") {
      println("Translate second ontology:")
      O2ClassesWithTranslations = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/" + O2Name + "/classesWithTranslation.txt")
        .map(x => (x.split(",").apply(0), x.split(",").apply(1)))
      O2RelationsWithTranslations = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/" + O2Name + "/RelationsWithTranslation.txt")
        .map(x => (x.split(",").apply(0), x.split(",").apply(1)))
    }

    if (naturalLanguage1 != "English" && naturalLanguage2 == "English") {
      println("====================================== Classes Similarity Non-English X English ======================================") //    val matchedClasses: RDD[(String, String, String, Double)] = sim.GetClassSimilarityNonEnglishWithEnglish(O1ClassesWithTranslations, O2Classes)
      matchedClasses = sim.GetClassSimilarityNonEnglishWithEnglish(O1ClassesWithTranslations, O2Classes, threshold)
      matchedClasses.foreach(println(_))
      numberOfMatchedClasses = matchedClasses.count().toInt
      println("Number of matched classes = "+ numberOfMatchedClasses)

      println("====================================== Relations Similarity Non-English X English======================================") //    val matchedRelations: RDD[(String, String, String, Double)] = relSim.GetRelationSimilarityNonEnglishWithEnglish(O2Relations, O1RelationsWithTranslations)
      matchedRelations = relSim.GetRelationSimilarityNonEnglishWithEnglish(O1RelationsWithTranslations, O2Relations, threshold)
      matchedRelations.foreach(println(_))
      numberOfMatchedRelations = matchedRelations.count().toInt
      println("Number of matched relations = "+ numberOfMatchedRelations)
    } else if (naturalLanguage1 == "English" && naturalLanguage2 != "English") {
      println("====================================== Classes Similarity English X non-English======================================") //    val matchedClasses: RDD[(String, String, String, Double)] = sim.GetClassSimilarityNonEnglishWithEnglish(O1ClassesWithTranslations, O2Classes)
      matchedClasses = sim.GetClassSimilarityEnglishWithNonEnglish(O1Classes, O2ClassesWithTranslations, threshold)
      matchedClasses.foreach(println(_))
      numberOfMatchedClasses = matchedClasses.count().toInt
      println("Number of matched classes = "+ numberOfMatchedClasses)

      println("====================================== Relations Similarity English X non-English======================================") //    val matchedRelations: RDD[(String, String, String, Double)] = relSim.GetRelationSimilarityNonEnglishWithEnglish(O2Relations, O1RelationsWithTranslations)
      matchedRelations = relSim.GetRelationSimilarityEnglishWithNonEnglish(O2RelationsWithTranslations, O1Relations, threshold)
      matchedRelations.foreach(println(_))
      numberOfMatchedRelations = matchedRelations.count().toInt
      println("Number of matched relations = "+ numberOfMatchedRelations)
    } else if (naturalLanguage1 != "English" && naturalLanguage2 != "English") {
      println("======================================multi Classes Similarity ======================================") //    val matchedClasses: RDD[(String, String, String, Double)] = sim.GetClassSimilarityNonEnglishWithEnglish(O1ClassesWithTranslations, O2Classes)
      matchedNonEnglishClasses = sim.GetMultilingualClassSimilarity(O1ClassesWithTranslations, O2ClassesWithTranslations, threshold)
      matchedNonEnglishClasses.foreach(println(_))
      numberOfMatchedClasses = matchedNonEnglishClasses.count().toInt
      println("Number of matched classes = "+ numberOfMatchedClasses)

      println("======================================multi Relations Similarity ======================================") //    val matchedRelations: RDD[(String, String, String, Double)] = relSim.GetRelationSimilarityNonEnglishWithEnglish(O2Relations, O1RelationsWithTranslations)
      matchedNonEnglishRelations = relSim.GetMultilingualRelationSimilarity(O1RelationsWithTranslations, O2RelationsWithTranslations, threshold)
      matchedNonEnglishRelations.foreach(println(_))
      numberOfMatchedRelations = matchedNonEnglishRelations.count().toInt
      println("Number of matched relations = "+ numberOfMatchedRelations)
    }
    println("Number of all matched resources = "+ (numberOfMatchedClasses+numberOfMatchedRelations))

  }

  /**
    * Get the matching between two ontologies in the same natural language.
    */
  def MonolingualMatching(O1Classes: RDD[String], O1Relations: RDD[(String)], O2Classes: RDD[String], O2Relations: RDD[(String)], threshold: Double) = {
    //    val O1ClassesWithTranslation: RDD[(String, String)] = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/"+O1Name+"/classesWithTranslation.txt").map(x => (x.split(",").apply(0), x.split(",").apply(1))) //    println("O1 classes with translation")
    //    //    O1ClassesWithTranslation.foreach(println(_))
    println("Threshold = "+ threshold)
    println("====================================== Classes Similarity ======================================")
    val sim = new ClassSimilarity()
    val matchedMonolingualClasses: RDD[(String, String, Double)] = sim.GetMonolingualClassSimilarity(O1Classes, O2Classes, threshold)
    matchedMonolingualClasses.foreach(println(_))
    numberOfMatchedClasses = matchedMonolingualClasses.count().toInt
    println("Number of matched classes = ", numberOfMatchedClasses)


    //    val O1RelationsWithTranslation: RDD[(String, String)] = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/"+O1Name+"/RelationsWithTranslation.txt").map(x => (x.split(",").apply(0), x.split(",").apply(1))) //        println("O1 relations with translation")
    //    //        O1RelationsWithTranslation.foreach(println(_))
    println("====================================== Relations Similarity ======================================")
    val relSim = new RelationSimilarity()
    val matchedMonolingualRelations: RDD[(String, String, Double)] = relSim.GetMonolingualRelationSimilarity(O2Relations, O2Relations, threshold)
    matchedMonolingualRelations.foreach(println(_))
    numberOfMatchedRelations = matchedMonolingualRelations.count().toInt
    println("Number of matched relations = ", numberOfMatchedRelations)
    println("Number of all matched resources = ",numberOfMatchedClasses+numberOfMatchedRelations)
  }
}
