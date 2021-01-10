import org.apache.jena.graph
import org.apache.jena.graph.Node
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

class Merge(sparkSession1: SparkSession) {
  var numberOfMatchedClasses = 0
  var numberOfMatchedRelations = 0
  /**
    * Merge two ontologies in two different natural languages.
    */
  def MergeOntologies(O1triples: RDD[graph.Triple], O2triples: RDD[graph.Triple], O2translated: String, O1Name: String): RDD[graph.Triple] = {
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
    val O1Relations: RDD[String] = ontStat.getAllRelations(O1LabelsBroadcasting, O1triples).map(x => x._2)
    println("====================================== All relations in O1 ======================================")
    O1Relations.foreach(println(_))

    val O2Classes: RDD[String] = ontStat.getAllClasses(O2triples).map(x => p.stringPreProcessing(x)).persist(StorageLevel.MEMORY_AND_DISK).distinct(2) //For SEO
    //    val O2Classes: RDD[(String)] = ontStat.retrieveClassesWithCodesAndLabels(O2triples).map(x=>x._2).persist(StorageLevel.MEMORY_AND_DISK) //For Cmt and Multifarm dataset
    println("====================================== All classes in O2 ======================================")
    O2Classes.foreach(println(_))
    //      val O2Relations: RDD[(String)] = ontStat.getAllRelationsOld(O2triples).map(x => p.stringPreProcessing(x._1))
    val O2Labels: Map[Node, graph.Triple] = O2triples.filter(x => x.getPredicate.getLocalName == "label").keyBy(_.getSubject).collect().toMap
    val O2LabelsBroadcasting: Broadcast[Map[Node, graph.Triple]] = sparkSession1.sparkContext.broadcast(O2Labels)
    val O2Relations: RDD[(String)] = ontStat.getAllRelations(O2LabelsBroadcasting, O2triples).map(x => p.stringPreProcessing(x._1))
    println("====================================== All relations in O2 ======================================")
    O2Relations.foreach(println(_))

    println("======================================")
    println("|    Cross-lingual Matching    |")
    println("======================================")
    // ####################### Automatic Translation using Yandex API #######################
    //    val languageTag1: String = O1triples.filter(x=> x.getPredicate.getLocalName == "label").first().getObject.getLiteralLanguage
    //    println("language tag for O1 is "+languageTag1)
    //        Translation.translateToEnglish(O1Classes,O1Relations, languageTag1)
//    Translation.translateToGerman(O2Classes,O2Relations)

    val O1ClassesWithTranslation: RDD[(String, String)] = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/"+O1Name+"/classesWithTranslation.txt").map(x => (x.split(",").apply(0), x.split(",").apply(1))) //    println("O1 classes with translation")
    //    O1ClassesWithTranslation.foreach(println(_))
    println("====================================== Classes Similarity ======================================")
    val sim = new ClassSimilarity()
    val matchedClasses: RDD[(String, String, String, Double)] = sim.GetClassSimilarity(O1ClassesWithTranslation, O2Classes)
    matchedClasses.foreach(println(_))
    numberOfMatchedClasses = matchedClasses.count().toInt


    val O1RelationsWithTranslation: RDD[(String, String)] = sparkSession1.sparkContext.textFile("src/main/resources/OfflineDictionaries/"+O1Name+"/RelationsWithTranslation.txt").map(x => (x.split(",").apply(0), x.split(",").apply(1))) //        println("O1 relations with translation")
    //        O1RelationsWithTranslation.foreach(println(_))
    println("====================================== Relations Similarity ======================================")
    val relSim = new RelationSimilarity()
    val matchedRelations: RDD[(String, String, String, Double)] = relSim.GetRelationSimilarity(O2Relations, O1RelationsWithTranslation)
    matchedRelations.foreach(println(_))
    numberOfMatchedRelations = matchedRelations.count().toInt


    val om = new MultilingualOntology(sparkSession1)
    val multilingualMergedOntology: RDD[graph.Triple] = om.GenerateMultilingualOntology(O1ClassesWithTranslation, matchedClasses, matchedRelations, O1RelationsWithTranslation, O1Ontology, O2Ontology, O2translated)

//    multilingualMergedOntology.coalesce(1, shuffle = true).saveAsNTriplesFile("src/main/resources/MultilingualMergedOntology")


    multilingualMergedOntology
  }
}
