import org.apache.jena.graph
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/*
* Created by Shimaa Ibrahim 28 October 2019
*/
class QualityAssessment(sparkSession: SparkSession) {
  val ontoStat = new OntologyStatistics(sparkSession)

  /**
    * Get the quality assessment sheet for the input and merged ontologies.
    */
  def GetQualityAssessmentSheet(O1: RDD[graph.Triple], O2: RDD[graph.Triple])={
    println("Relationship richness for O1 is " + this.RelationshipRichness(O1))
    println("Relationship richness for O2 is " + this.RelationshipRichness(O2))
    println("==============================================")
    println("Attribute richness for O1 is " + this.AttributeRichness(O1))
    println("Attribute richness for O2 is " + this.AttributeRichness(O2))
    println("==============================================")
    println("Inheritance richness for O1 is " + this.InheritanceRichness(O1))
    println("Inheritance richness for O2 is " + this.InheritanceRichness(O2))
    println("==============================================")
    println("Readability for O1 is " + this.Readability(O1))
    println("Readability for O2 is " + this.Readability(O2))
    println("==============================================")
    println("Isolated Elements for O1 is " + this.IsolatedElements(O1))
    println("Isolated Elements for O2 is " + this.IsolatedElements(O2))
    println("==============================================")
    println("Missing Domain Or Range for O1 is " + this.MissingDomainOrRange(O1))
    println("Missing Domain Or Range for O2 is " + this.MissingDomainOrRange(O2))
    println("==============================================")
    println("Redundancy for O1 is " + this.Redundancy(O1))
    println("Redundancy for O2 is " + this.Redundancy(O2))
    println("==============================================")
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
    println("Number of subClassOf "+numOfSubClassOf+" Number of classes "+numOfClasses)
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
    val numOfResources = ontoStat.getAllResources(ontologyTriples).count().toDouble
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
    val numOfAllClassesWithRedundancy = ontoStat.getAllClasses(O).count().toDouble
    val numOfAllClassesWithoutRedundancy = ontoStat.getNumberOfClasses(O)

    ontoStat.roundNumber(1 - (numOfAllClassesWithoutRedundancy / numOfAllClassesWithRedundancy))
  }

  /**
    * refers  to  how  many  classes  in  the  input  ontologies C1+C2 are preserved in the merged ontology Cm excluding matched classes Cmatch.
    */
  def ClassCoverage(O1: RDD[graph.Triple], O2: RDD[graph.Triple], Om: RDD[graph.Triple], numberOfMatchedClasses: Int): Double = {
    val numOfMergedClasses = ontoStat.getNumberOfClasses(Om)
    val numOfClassesO1 = ontoStat.getNumberOfClasses(O1)
    val numOfClassesO2 = ontoStat.getNumberOfClasses(O2)
    ontoStat.roundNumber(numOfMergedClasses / (numOfClassesO1 + numOfClassesO2 - numberOfMatchedClasses))
  }

  /**
    * refers  to  how  many  relations  in  the  input  ontologies P1+P2 are preserved in the merged ontology Pm excluding matched properties Pmatch.
    */
  def PropertyCoverage(O1: RDD[graph.Triple], O2: RDD[graph.Triple], Om: RDD[graph.Triple], numberOfMatchedProperties: Int): Double = {
    val numOfMergedProperties = ontoStat.getAllProperties(Om).count().toDouble
    val numOfPropertiesO1 = ontoStat.getAllProperties(O1).count().toDouble
    val numOfPropertiesO2 = ontoStat.getAllProperties(O2).count().toDouble
    ontoStat.roundNumber(numOfMergedProperties / (numOfPropertiesO1 + numOfPropertiesO2 - numberOfMatchedProperties))
  }

  /** refers  to  how  much  the  size  of  the  merged  ontology compared to the input ontologies.
    * The smaller size of merged ontology, themore the ontology is compacted,
    * e.g. if some resources are removed in order to avoid redundant resources in the merged ontology.
    */
  def Compactness(O1: RDD[graph.Triple], O2: RDD[graph.Triple], Om: RDD[graph.Triple]): Double = {
    ontoStat.roundNumber(ontoStat.getAllResources(Om).count().toDouble / (ontoStat.getAllResources(O1).count().toDouble + ontoStat.getAllResources(O2).count().toDouble))

  }
}
