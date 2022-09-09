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
    println("Degree of overlapping is " + this.DegreeOfOverlapping(O1, O2)*100+"%")
    println("Match coverage is " + this.MatchCoverage(O1, O2))
    println("Match ratio is " + this.MatchRatio(O1, O2))
  }

  /**
    * refers to to how many common resources exist between the input ontologies.
    */
  def DegreeOfOverlapping(O1: RDD[graph.Triple], O2: RDD[graph.Triple]): Double = {
    val Overlapping: Double = ontoMatch.numberOfAllMatchedResources / (ontoStat.getAllSchemaResources(O1).count().toDouble + ontoStat.getAllSchemaResources(O2).count().toDouble)
    ontoStat.roundNumber(Overlapping)
  }
  /**
    * refers to the fraction of resources which exist in at least one correspondence in the matching results in comparison to the total number of resources in the input ontologies.
    */
  def MatchCoverage(O1: RDD[graph.Triple], O2: RDD[graph.Triple]): Double = {
    val matchCoverage = (ontoMatch.R_O1_match + ontoMatch.R_O2_match) / (ontoStat.getAllSchemaResources(O1).count().toDouble + ontoStat.getAllSchemaResources(O2).count().toDouble)
    println("R1-match = "+ ontoMatch.R_O1_match + "R2-match = "+ontoMatch.R_O2_match + " all resources in O1 = "+ ontoStat.getAllSchemaResources(O1).count() + " all resources in O2 = " + ontoStat.getAllSchemaResources(O2).count())
    ontoStat.roundNumber(matchCoverage)
//    matchCoverage
  }
  /**
    * refers to the ratio between the number of found correspondences and the number of matched resources in the input ontologies..
    */
  def MatchRatio(O1: RDD[graph.Triple], O2: RDD[graph.Triple]): Double = {
    val matchRatio: Double = (2 * ontoMatch.numberOfAllMatchedResources) / (ontoMatch.R_O1_match + ontoMatch.R_O2_match)
    ontoStat.roundNumber(matchRatio)
  }
}

