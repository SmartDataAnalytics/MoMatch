import net.sansa_stack.rdf.spark.model._
import org.apache.jena.graph
import org.apache.jena.graph.{Node, NodeFactory}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class OntologyStatistics(sparkSession: SparkSession) {
  /**
    * Get some statistics for the ontology such as number of triples, number of object properties, number of data properties, and number of classes
    */
  def getStatistics(ontologyTriples: RDD[graph.Triple]) = {
//    println("======================================")
//    println("|       Ontology Statistics      |")
//    println("======================================")
    //    val ontoName = ontologyTriples.filter(x => x.getPredicate.getLocalName == "type" && x.getObject.getLocalName == "Ontology").map(x => x.getSubject.getLocalName).first()
    //    println("Ontology name is: " + ontoName)
    println("Number of all resources = " + this.getAllResources(ontologyTriples).count())
    println("Number of triples in the ontology = " + ontologyTriples.count()) //    ontologyTriples.foreach(println(_))
    val sObjectProperty = ontologyTriples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "ObjectProperty").distinct(2)
    println("Number of object properties is " + sObjectProperty.count()) //    sObjectProperty.foreach(println(_))
    val sAnnotationProperty = ontologyTriples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "AnnotationProperty").distinct(2)
    println("Number of annotation properties is " + sAnnotationProperty.count()) //    sAnnotationProperty.foreach(println(_))
    val sDatatypeProperty: RDD[graph.Triple] = ontologyTriples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "DatatypeProperty").distinct(2)
    println("Number of Datatype properties is " + sDatatypeProperty.count()) //    sDatatypeProperty.foreach(println(_))

//    val sClass = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).map(x => x.getSubject.getLocalName).filter(x => x != "Class").distinct()
    val sClass = this.getNumberOfClasses(ontologyTriples)
    println("Number of classes is " + sClass)
    val listOfPredicates = ontologyTriples.map(x => x.getPredicate.getLocalName).distinct(2) //    println("List of predicates in the ontology: ")
    //    listOfPredicates.foreach(println(_))
  }

  /**
    *  Get number of classes in the ontology.
    */
  def getNumberOfClasses(ontologyTriples: RDD[graph.Triple]): Double = {
//    val numOfClasses = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).map(x => x.getSubject.getLocalName).filter(x => x != "Class").distinct().count()
val numOfClasses = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).map(x => x.getSubject.getLocalName).distinct().count()

//    val numOfClasses = this.getAllClasses(ontologyTriples).distinct(2).count()
    numOfClasses
  }

  /**
    *  Get number of object properties in the ontology.
    */
  def getNumberOfRelations(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfRelations = ontologyTriples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "ObjectProperty").distinct(2).count()

    numOfRelations
  }

  /**
    *  Get number of data type properties in the ontology.
    */
  def getNumberOfAttributes(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfAttributes = ontologyTriples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "DatatypeProperty").distinct(2).count()

    numOfAttributes
  }

  /**
    *  Get number of subClassOf relations in the ontology.
    */
  def getNumberOfSubClasses(ontologyTriples: RDD[graph.Triple]): Double = {
//    val numOfSubClasses = ontologyTriples.filter(q => q.getSubject.isURI && q.getObject.isURI && q.getPredicate.getLocalName == "subClassOf").distinct(2).count()
    val numOfSubClasses = ontologyTriples.filter(q => q.getSubject.isURI && q.getPredicate.getLocalName == "subClassOf").distinct(2).count()
    //    println("Number of SubClasses "+numOfSubClasses)
    numOfSubClasses

  }

  /**
    *  Get number of all properties in the ontology.
    */
  def getAllProperties(ontologyTriples: RDD[graph.Triple]): RDD[String] = {
    val allProperties: RDD[String] = ontologyTriples.filter(q => q.getObject.isURI && (q.getObject.getLocalName == "ObjectProperty" || q.getObject.getLocalName == "DatatypeProperty" || q.getObject.getLocalName == "AnnotationProperty")).map(x => x.getSubject.getLocalName).distinct(2)
    allProperties
  }

  /**
    *  Get all classes and all properties in the ontology.
    */
  def getAllResources(ontologyTriples: RDD[graph.Triple]): RDD[String] = {
    val allClasses = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).map(x => x.getSubject.getLocalName).filter(x => x != "Class").distinct()
//    val allClasses = this.getAllClasses(ontologyTriples).filter(x => x.toLowerCase != "class").distinct()
    val allProperties = this.getAllProperties(ontologyTriples)
    allClasses.union(allProperties)
  }

  /**
    *  Get number of HRD such as comment, label, and description.
    */
  def getNumberOfHRD(ontologyTriples: RDD[graph.Triple]): Double = {
    val numOfHRD = ontologyTriples.filter(q => q.getPredicate.getLocalName == "comment" || q.getPredicate.getLocalName == "label" || q.getPredicate.getLocalName == "description").distinct(2).count() //    println("Number of SubClasses "+numOfSubClasses)
    numOfHRD

  }

  /**
    *  Get the distribution of each resource (class/property) in the ontology.
    */
  def resourceDistribution(ontologyTriples: RDD[graph.Triple]): RDD[Int] = {
    val rd: RDD[Int] = ontologyTriples.filter(x => x.getPredicate.getLocalName != "label" && x.getSubject.isURI).groupBy(_.getSubject).map(f => (f._2.filter(p => p.getSubject.getLocalName.contains(p.getSubject.getLocalName))).size)
    rd
  }

  /**
    *  Get number of properties which do not have a domain or a range.
    */
  def missingDomainOrRange(ontologyTriples: RDD[graph.Triple]): Long = {
    val allProperties: RDD[String] = this.getAllProperties(ontologyTriples)
    val t = ontologyTriples.filter(x => x.getPredicate.getLocalName == "domain" || x.getPredicate.getLocalName == "range").map(y => (y.getSubject, y.getPredicate.getLocalName))
    val tt = allProperties.zipWithIndex().keyBy(_._1).leftOuterJoin(t.keyBy(_._1.getLocalName)).filter(x => x._2._2.isEmpty)
    tt.count()
  }

  //  def getNumberOfRelations(triples: RDD[graph.Triple]): Double={
  //    val numOfObjectProperty = triples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "ObjectProperty").distinct(2).count()
  //
  //    val numOfAnnotationProperty = triples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "AnnotationProperty").distinct(2).count()
  //    //    sAnnotationProperty.foreach(println(_))
  //
  //    val numOfDatatypeProperty = triples.filter(q => q.getObject.isURI && q.getObject.getLocalName == "DatatypeProperty").distinct(2).count()
  //
  //    val numOfRelations = numOfObjectProperty + numOfAnnotationProperty + numOfDatatypeProperty
  //
  //    numOfRelations
  //  }
//  def OntologyWithCodeOrText(ontologyTriples: RDD[graph.Triple]): Boolean = {
//    val classes = ontologyTriples.filter(q => q.getSubject.isURI && q.getObject.isURI && q.getObject.getLocalName == "Class").distinct(2)
//    var hasCode = false
//    if (classes.first().getSubject.getLocalName.exists(_.isDigit)) hasCode = true else hasCode = false
//    hasCode
//  }

  //  def getAllClasses(ontologyTriples: RDD[graph.Triple]): RDD[String] = { //will be applied for ontologies without codes like SEO
  //    val classesWithoutURIs: RDD[String] = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class")))
  //      .filter(x => x.getSubject.isURI)
  //      .map(x => x.getSubject.getLocalName).distinct()
  //    classesWithoutURIs
  //  }
  /**
    *  Get all classes in the ontology.
    */
  def getAllClasses(ontologyTriples: RDD[graph.Triple]): RDD[String] = { //will be applied for ontologies with or without codes
    var classesWithoutURIs = sparkSession.sparkContext.emptyRDD[String]
    if (ontologyTriples.find(None, Some(NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#label")), None).count() > 0) {
//      println("ontology has labels")
      classesWithoutURIs = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).keyBy(_.getSubject.getLocalName).join(ontologyTriples.filter(x => x.getSubject.isURI).keyBy(_.getSubject.getLocalName)).filter(x => x._2._2.getPredicate.getLocalName == "label").map(y => (y._2._2.getObject.getLiteral.getLexicalForm.split("@").head))//.distinct(2)
    } else {
//      println("ontology do not have labels")
      classesWithoutURIs = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).map(x => x.getSubject.getLocalName)//.distinct()
    }
    classesWithoutURIs
  }

//  def retrieveClassesWithCodesAndLabels(ontologyTriples: RDD[graph.Triple]): RDD[(String, String)] = {
//    //will be applied for ontologies with codes like Multifarm ontologies
//    var classes = ontologyTriples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Class"))).filter(x => x.getSubject.isURI).keyBy(_.getSubject.getLocalName).join(ontologyTriples.filter(x => x.getSubject.isURI).keyBy(_.getSubject.getLocalName)).filter(x => x._2._2.getPredicate.getLocalName == "label").map(y => (y._1, y._2._2.getObject.getLiteral.getLexicalForm.split("@").head)).distinct(2)
//    classes
//  }

//  def retrieveClassesWithoutLabels(o: RDD[graph.Triple]): RDD[String] = { //for classes with local names ex:ekaw-en, edas and SEO ontologies
//    val p = new PreProcessing()
//    val classesWithoutURIs: RDD[String] = o.filter(x => x.getSubject.isURI).map(y => p.stringPreProcessing(y.getSubject.getLocalName)).distinct().union(o.map { case (x) => if (x.getObject.isURI) (p.stringPreProcessing(x.getObject.getLocalName)) else null }.filter(y => y != null && y != "class")).distinct()
//    classesWithoutURIs
//  }

  /**
    *  Get all properties in the ontology.
    */
  def getAllRelationsOld(ontologyTriples: RDD[graph.Triple]): RDD[(String, String)] = {
    val prop: RDD[(String, String)] = ontologyTriples.filter(q => (q.getObject.isURI && q.getObject.getLocalName == "ObjectProperty") || (q.getObject.isURI && q.getObject.getLocalName == "AnnotationProperty") || (q.getObject.isURI && q.getObject.getLocalName == "DatatypeProperty") || (q.getObject.isURI && q.getObject.getLocalName == "FunctionalProperty") || (q.getObject.isURI && q.getObject.getLocalName == "InverseFunctionalProperty")).distinct(2).map(x => (x.getSubject.getLocalName, x.getObject.getLocalName)).distinct(2)
    prop
  }

  /**
    *  Get all properties in the ontology.
    */
  def getAllRelations(ontoLabelBroadcasting: Broadcast[Map[Node, graph.Triple]], ontologyTriples: RDD[graph.Triple]): RDD[(String, String)] = {
    val prop: RDD[graph.Triple] = ontologyTriples.filter(q => (q.getObject.isURI && q.getObject.getLocalName == "ObjectProperty") || (q.getObject.isURI && q.getObject.getLocalName == "AnnotationProperty") || (q.getObject.isURI && q.getObject.getLocalName == "DatatypeProperty") || (q.getObject.isURI && q.getObject.getLocalName == "FunctionalProperty") || (q.getObject.isURI && q.getObject.getLocalName == "InverseFunctionalProperty")).distinct(2)//.map(x => (x.getSubject.getLocalName, x.getObject.getLocalName)).distinct(2)
  val relations: RDD[(String, String)] = prop.map(x => if (ontoLabelBroadcasting.value.contains(x.getSubject)) (x.getSubject.getLocalName, ontoLabelBroadcasting.value(x.getSubject).getObject.getLiteral.toString().split("@").head) else (x.getSubject.getLocalName, x.getObject.getLocalName)).distinct(2)
  relations
  }

//  def retrieveRelationsWithCodes(sourceLabelBroadcasting: Broadcast[Map[Node, graph.Triple]], ontologyTriples: RDD[graph.Triple]): RDD[(String, String)] = {
//    val prop: RDD[graph.Triple] = ontologyTriples.filter(q => (q.getObject.isURI && q.getObject.getLocalName == "ObjectProperty") || (q.getObject.isURI && q.getObject.getLocalName == "AnnotationProperty") || (q.getObject.isURI && q.getObject.getLocalName == "DatatypeProperty") || (q.getObject.isURI && q.getObject.getLocalName == "FunctionalProperty") || (q.getObject.isURI && q.getObject.getLocalName == "InverseFunctionalProperty")).distinct(2)
//    //    println("prop =============>")
//    //    prop.foreach(println(_))
//    val relations: RDD[(String, String)] = prop.map(x => if (sourceLabelBroadcasting.value.contains(x.getSubject)) (x.getSubject.getLocalName, sourceLabelBroadcasting.value(x.getSubject).getObject.getLiteral.toString().split("@").head) else (x.getSubject.getLocalName, x.getObject.getLocalName)).distinct(2)
//    relations
//  }

  def roundNumber(num: Double): Double = {
    (num * 100).round / 100.toDouble
  }
}
