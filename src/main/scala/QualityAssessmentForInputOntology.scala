import org.apache.jena.graph
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/*
* Created by Shimaa Ibrahim 28 October 2019
*/
class QualityAssessmentForInputOntology(sparkSession: SparkSession) {
  val ontoStat = new OntologyStatistics(sparkSession)
  val ontoMatch = new Match(sparkSession)

  /**
    * Get the quality assessment sheet for the input ontologies.
    */
  def GetQualityAssessmentForOntology(O: RDD[graph.Triple])={
    println("Relationship richness for O is " + this.RelationshipRichness(O))
    println("Attribute richness for O is " + this.AttributeRichness(O))
    println("Inheritance richness for O is " + this.InheritanceRichness(O))
    println("Readability for O is " + this.Readability(O))
    println("Isolated Elements for O is " + this.IsolatedElements(O))
    println("Missing Domain Or Range for O is " + this.MissingDomainOrRange(O))
    println("Redundancy for O is " + this.Redundancy(O))
  }

  /**
    * refers to how much knowledge about classes is inthe schema.
    * The more attributes are defined, the more knowledge the ontol-ogy provides.
    * the number of attributes for all classes divided by the number of classes (C).
    */
  def AttributeRichness(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfRelations = ontoStat.getNumberOfRelations(ontologyTriples)
    //    println("Number of Relations = "+numOfRelations)
    val numOfClasses = ontoStat.getNumberOfClasses(ontologyTriples)
    //    println("Number of Classes = "+numOfClasses)
    val attributeRichness: Double = numOfRelations / numOfClasses
    ontoStat.roundNumber(attributeRichness)
  }

  /**
    * refers to the diversity of relations and their position in the ontology.
    * The more relations the ontology has (except \texttt{rdfs:subClassOf} relation), the richer it is. number of object property / (subClassOf + object property)
    */
  def RelationshipRichness(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfRelations = ontoStat.getNumberOfRelations(ontologyTriples)
    val numOfSubClassOf = ontoStat.getNumberOfSubClasses(ontologyTriples)
    val relationshipRichness = numOfRelations / (numOfSubClassOf + numOfRelations)
    ontoStat.roundNumber(relationshipRichness)
  }

  /**
    * refers to how well knowledge is distributed across different  levels  in  the  ontology.
    * the number of sub-classes divided by the sum of the number of classes.
    */
  def InheritanceRichness(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfSubClassOf = ontoStat.getNumberOfSubClasses(ontologyTriples)
    val numOfClasses = ontoStat.getNumberOfClasses(ontologyTriples)
//    println("Number of subClassOf "+numOfSubClassOf+" Number of classes "+numOfClasses)
    ontoStat.roundNumber(numOfSubClassOf / numOfClasses)
  }

  /**
    * refers to the the existence of human readable descriptions(HRD) in the ontology, such as comments, labels, or description.
    * The more human readable descriptions exist, the more readable the ontology is.
    * HRD / number of resources
    */
  def Readability(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfHRD = ontoStat.getNumberOfHRD(ontologyTriples)
//    val numOfTriples = ontologyTriples.distinct().count()
    val numOfResources = ontoStat.getAllSchemaResources(ontologyTriples).count().toDouble
    ontoStat.roundNumber(numOfHRD / numOfResources)
  }

  /**
    * refers to classes and properties which are defined but not connected to the rest of the ontology, i.e. not used.
    * (isolated classes + isolated properties)/(classes + properties)
    */
  def IsolatedElements(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfIsolatedElements = ontoStat.resourceDistribution(ontologyTriples).filter(x => x == 1).count()
//    println("numOfIsolatedElements "+numOfIsolatedElements)
    val numOfClasses = ontoStat.getNumberOfClasses(ontologyTriples)
    val numOfProperties = ontoStat.getNumberOfRelations(ontologyTriples) + ontoStat.getNumberOfAttributes(ontologyTriples)
    ontoStat.roundNumber(numOfIsolatedElements / (numOfClasses + numOfProperties))
  }

  /**
    * refers to missing information about properties.
    * The less of missing information about properties, the more the ontology is complete.
    */
  def MissingDomainOrRange(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfPropertiesMissingInfo = ontoStat.missingDomainOrRange(ontologyTriples).toDouble
    val numOfAllProperties = ontoStat.getAllProperties(ontologyTriples).count().toDouble
    ontoStat.roundNumber(numOfPropertiesMissingInfo/numOfAllProperties)
  }

  /**
    * refers to how many redundant resources exist.
    */
  def Redundancy(O: RDD[graph.Triple]): Double = {
    val numOfAllClasses = ontoStat.getAllClasses(O).count().toDouble
    val numOfAllClassesWithoutRedundancy = ontoStat.getAllClasses(O).distinct().count().toDouble

//    ontoStat.roundNumber(1 - (numOfAllClassesWithoutRedundancy / numOfAllClassesWithRedundancy))
    val redundantClasses: Double = numOfAllClasses - numOfAllClassesWithoutRedundancy
    println("Redundant classes = " + redundantClasses + " number of all classes with redundancy = "+numOfAllClasses)
//    ontoStat.roundNumber(redundantClasses / numOfAllClasses)
    val redundancyRatio = ((redundantClasses / numOfAllClasses)* 1000).round / 1000.toDouble
//    println("round value = "+redundancyRatio)
    redundancyRatio

  }


}
