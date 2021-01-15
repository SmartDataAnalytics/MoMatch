import net.sansa_stack.rdf.spark.io._
import org.apache.jena.graph
import org.apache.jena.riot.Lang
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/*
* Created by Shimaa 13.Jan.2021
* */
object OntologyMatching {
  def main(args: Array[String]): Unit = {
    //    val frame = new MOMergGUI("My first try")
    //    frame.setVisible(true)
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val sparkSession1 = SparkSession.builder //      .master("spark://172.18.160.16:3090")
      .master("local[*]").config("spark.serializer", "org.apache.spark.serializer.KryoSerializer").getOrCreate()

    val startTimeMillis = System.currentTimeMillis()
    //    val m = new MOMergGUI()
    //    println(m.s)
    //================= German ontologies =================
    //        val O1 = "src/main/resources/EvaluationDataset/German/conference-de.ttl"
    //    val O1 = "/home/shimaa/MoMatch/src/main/resources/OntologyMatchingTask/ms.nt"
    //    val O1 = "src/main/resources/EvaluationDataset/German/cmt-de.ttl"
    //    val O1 = "src/main/resources/EvaluationDataset/German/confOf-de.ttl"
    //            val O1 = "src/main/resources/EvaluationDataset/German/iasted-de.ttl"
    //        val O1 = "src/main/resources/EvaluationDataset/German/sigkdd-de.ttl"
    //================= Arabic ontologies =================
    //        val O1 = "src/main/resources/EvaluationDataset/Arabic/conference-ar.ttl"
    //                val O1 = "src/main/resources/EvaluationDataset/Arabic/cmt-ar.ttl"
    //                val O1 = "src/main/resources/EvaluationDataset/Arabic/confOf-ar.ttl"
    //            val O1 = "src/main/resources/EvaluationDataset/Arabic/iasted-ar.ttl"
    //        val O1 = "src/main/resources/EvaluationDataset/Arabic/sigkdd-ar.ttl"
    //================= French ontologies =================
    //            val O1 = "src/main/resources/EvaluationDataset/French/conference-fr.ttl"
    //          val O1 = "src/main/resources/EvaluationDataset/French/cmt-fr.ttl"
    //          val O1 = "src/main/resources/EvaluationDataset/French/confOf-fr.ttl"
    //              val O1 = "src/main/resources/EvaluationDataset/French/iasted-fr.ttl"
    //              val O1 = "src/main/resources/EvaluationDataset/French/sigkdd-fr.ttl"
    //================= English ontologies =================
    val O1 = "/home/shimaa/MoMatch/src/main/resources/OntologyMatchingTask/uo.nt"

//    val O2 = "src/main/resources/CaseStudy/SEO.ttl"
//    val O2 = "src/main/resources/EvaluationDataset/English/edas-en.ttl"
//    val O2 = "src/main/resources/EvaluationDataset/English/ekaw-en.ttl"
    val O2 = "/home/shimaa/MoMatch/src/main/resources/OntologyMatchingTask/owlapi.nt"
    val lang1: Lang = Lang.TURTLE
    val O1triples: RDD[graph.Triple] = sparkSession1.rdf(lang1)(O1).distinct(2)
    val O2triples: RDD[graph.Triple] = sparkSession1.rdf(lang1)(O2).distinct(2)
    val runTime = Runtime.getRuntime

    val ontStat = new OntologyStatistics(sparkSession1) //    ontStat.getStatistics(O1triples)
    val ontoMatch = new Match(sparkSession1)

    println("Statistics for O1 ontology")
    ontStat.getStatistics(O1triples)
    println("All classes in O1:")
    ontStat.getAllClasses((O1triples)).foreach(println(_))

    println("Statistics for O2 ontology")
    ontStat.getStatistics(O2triples)
    println("All classes in O2:")
    ontStat.getAllClasses((O2triples)).foreach(println(_))


    ontoMatch.MatchOntologies(O1triples, O2triples, "Conference-de", IsCrosslingual = 0)


    //    println("==========================================================================")
    //    println("|         Quality Assessment for each input and output ontologies        |")
    //    println("==========================================================================")
    //    val quality = new QualityAssessment(sparkSession1)
    //      quality.GetQualityAssessmentSheet(O1triples, O2triples)

    val endTimeMillis = System.currentTimeMillis()
    val durationMinutes = (endTimeMillis - startTimeMillis) / (1000 * 60)
    println("runtime = " + durationMinutes + " minutes")
    sparkSession1.stop
  }

}
