import org.apache.jena.graph
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/*
* Created by Shimaa Ibrahim 10 February 2022
*/ class QualityAssessmentForMatchingProcess(sparkSession: SparkSession, ontoMatch: Match) {
  val ontoStat = new OntologyStatistics(sparkSession)

  /**
    * Get the quality assessment sheet for the input ontologies.
    */
  def GetQualityAssessmentForMatching(O1: RDD[graph.Triple], O2: RDD[graph.Triple]) = {
    println("Degree of overlapping is " + this.DegreeOfOverlapping(O1, O2))
  }

  /**
    * refers to to how many common resources exist between the input ontologies.
    */
  def DegreeOfOverlapping(O1: RDD[graph.Triple], O2: RDD[graph.Triple]): Double = {
    val Overlapping: Double = ontoMatch.numberOfAllMatchedResources / (ontoStat.getAllResources(O1).count().toDouble + ontoStat.getAllResources(O2).count().toDouble)
    ontoStat.roundNumber(Overlapping)
  }
}
