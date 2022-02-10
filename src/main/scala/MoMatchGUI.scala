import java.io.File

import net.sansa_stack.rdf.spark.io._
import net.sansa_stack.rdf.spark.model._
import org.apache.jena.graph
import org.apache.jena.graph.NodeFactory
import org.apache.jena.riot.Lang
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.swing.Swing._
import scala.swing._
import scala.swing.event._


object MoMatchGUI {
  def main(args: Array[String]) {
    val ui = new UI
    ui.visible = true
    ui.resizable = false
  }
}

class UI extends MainFrame {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  val sparkSession1 = SparkSession.builder //      .master("spark://172.18.160.16:3090")
    .master("local[*]").config("spark.serializer", "org.apache.spark.serializer.KryoSerializer").getOrCreate()
  var O1 = ""
  var O2 = ""
  val lang1: Lang = Lang.TURTLE
  //  val O1triples: RDD[graph.Triple] = sparkSession1.rdf(lang1)(O1).distinct(2)
  var O1triples: RDD[graph.Triple] = sparkSession1.sparkContext.emptyRDD[graph.Triple]
  //  val O2triples: RDD[graph.Triple] = sparkSession1.rdf(lang1)(O2Classes).distinct(2)
  var O2triples: RDD[graph.Triple] = sparkSession1.sparkContext.emptyRDD[graph.Triple]
  var languageTagForO1 = ""
  var languageTagForO2 = ""
  val p = new PreProcessing()

  def restrictHeight(s: Component) {
    s.maximumSize = new Dimension(Short.MaxValue, s.preferredSize.height)
  }

  title = "MoMatch - Multilingual Ontology Matching"

  val firstOntology = new TextField {
    columns = 32
  }
  firstOntology.editable = false
  val secondOntology = new TextField {
    columns = 32
  }
  secondOntology.editable = false
  val crosslingualStatus = new RadioButton("Cross-lingual matching")
  val monolingualStatus = new RadioButton("Monolingual matching")
  crosslingualStatus.selected = true // do Cross-lingual matching by default
  val statusGroup1: ButtonGroup = new ButtonGroup(crosslingualStatus, monolingualStatus)
  val statsForO1 = new RadioButton("Statistics for the first ontology")
  val statsForO2 = new RadioButton("Statistics for the second ontology")
  statsForO1.selected = true // by default get statistics for O1
  val statsGroup2: ButtonGroup = new ButtonGroup(statsForO1, statsForO2)
  val naturalLanguageForO1 = new ComboBox(List("Detect language", "English", "German", "French", "Arabic"))
  val naturalLanguageForO2 = new ComboBox(List("Detect language", "English", "German", "French", "Arabic"))
  val qualityAssessmentForO1 = new RadioButton("Quality assessment for the first ontology")
  val qualityAssessmentForO2 = new RadioButton("Quality assessment for the second ontology")
  qualityAssessmentForO1.selected = true // do assessment for O1 by default
  val statusGroup2: ButtonGroup = new ButtonGroup(qualityAssessmentForO1, qualityAssessmentForO2)
  //  val gender = new ComboBox(List("don't know", "female", "male"))
  //  val commentField = new TextArea { rows = 8; lineWrap = true; wordWrap = true }
  //  val pressMe = new ToggleButton("Press me!")
  //  pressMe.selected = false
  //  restrictHeight(firstOntology)
  //  restrictHeight(secondOntology)
  //  restrictHeight(gender)
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new BoxPanel(Orientation.Vertical) {
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered),"Input ontologies")
      //      border = LineBorder(Color.gray)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new Label("First ontology")
        contents += Swing.HStrut(5)
        contents += firstOntology
        contents += Swing.HStrut(5)
        contents += Button("Browse") {
          browseForFirstOntology()
        }
        contents += Swing.HStrut(5)
        contents += new Label("Language")
        contents += Swing.HStrut(5)
        contents += naturalLanguageForO1
      }
//      contents += Swing.VStrut(5)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new Label("Second ontology")
        contents += Swing.HStrut(5)
        contents += secondOntology
        contents += Swing.HStrut(5)
        contents += Button("Browse") {
          browseForSecondOntology()
        }
        contents += Swing.HStrut(5)
        contents += new Label("Language")
        contents += Swing.HStrut(5)
        contents += naturalLanguageForO2
      }
    }
    contents += Swing.VStrut(10)

    import java.awt.Font

    val myFont = new Font("SansSerif", Font.PLAIN, 10)
    contents += new BoxPanel(Orientation.Vertical) {
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered), "Ontology Statistics")
      //      border = LineBorder(Color.gray)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new BorderPanel {
          add(statsForO1, BorderPanel.Position.West)
          add(statsForO2, BorderPanel.Position.Center)
          add(Button("Get statistics") {
            getStats()
          }, BorderPanel.Position.East)
      }
      }

    }

    //    contents += Swing.VStrut(5)
    //    contents += likeScala
    //    contents += Swing.VStrut(5)
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Vertical) {
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered),"Ontology Matching")
      //      border = LineBorder(Color.gray)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new BorderPanel {
          add(crosslingualStatus, BorderPanel.Position.West)
          add(monolingualStatus, BorderPanel.Position.Center)
          add(Button("Match") {
            doMatch()
          }, BorderPanel.Position.East)
        }
      }
    }
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Vertical) {
      border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Lowered),"Quality Assessment")
      //      border = LineBorder(Color.gray)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new BorderPanel {
          add(qualityAssessmentForO1, BorderPanel.Position.West)
          add(qualityAssessmentForO2, BorderPanel.Position.Center)
          add(Button("Assess") {assess()}, BorderPanel.Position.East)
        }
      }
    }
//    contents += Swing.VStrut(10)
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents += Swing.HGlue
//      contents += Button("Close") {
//        closeAndExit()
//      }
//    }

    for (e <- contents) {
      e.xLayoutAlignment = 0.0
    }
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  //  val button1 = new Button("Button 1")
  //  val button2 = new Button("Button 2")
  //  val table = new Table(4,3) {
  //    border = LineBorder(Color.BLACK)
  //  }
  //  contents = new BoxPanel(Orientation.Vertical) {
  //    border = EmptyBorder(10)
  //    contents += new BorderPanel {
  //      add(button1, BorderPanel.Position.West)
  //    }
  //    contents += VStrut(10)
  //    contents += table
  //    contents += VStrut(10)
  //    contents += new BorderPanel {
  //      add(button2, BorderPanel.Position.East)
  //    }
  //  }
  listenTo(firstOntology)
  //  listenTo(commentField)
  //  listenTo(gender.selection)
  //  listenTo(likeScala)
  listenTo(crosslingualStatus)
  listenTo(monolingualStatus)
  //  listenTo(pressMe)
  reactions += { case EditDone(`firstOntology`) => println("Your name is now: " + firstOntology.text) //    case EditDone(`commentField`) =>
  //      println("You changed the comments")
  //    case SelectionChanged(`gender`) =>
  //      println("Your gender is now: " + gender.selection.item)
  //    case ButtonClicked(`likeScala`) =>
  //      if (!likeScala.selected) {
  //        if (Dialog.showConfirmation(contents.head,
  //          "Are you sure you don't like Scala?")
  //          != Dialog.Result.Yes)
  //          likeScala.selected = true
  //      }
  case ButtonClicked(s) => println("Button click on button: '" + s.text + "'")
  }

  def browseForFirstOntology() {
    val chooser = new FileChooser(new File("."))
    chooser.title = "Select a file"
    val result = chooser.showOpenDialog(null)
    if (result == FileChooser.Result.Approve) {
      //      println("Approve -- " + chooser.selectedFile)
      firstOntology.text = chooser.selectedFile.toString
      if (!firstOntology.text.contains(".nt")) {
        val res = Dialog.showMessage(contents.head, "Please choose an ontology in NTriple format!", title)
      } else {
        O1 = firstOntology.text
//        O1triples = sparkSession1.rdf(lang1)(O1).distinct(2)
        O1triples = p.graphPreprocessing(sparkSession1.rdf(lang1)(O1).distinct(2))
        Some(chooser.selectedFile)
      }
    } else None
  }

  def browseForSecondOntology() {
    val chooser = new FileChooser(new File("."))
    chooser.title = "Select a file"
    val result = chooser.showOpenDialog(null)
    if (result == FileChooser.Result.Approve) {
      //      println("Approve -- " + chooser.selectedFile)
      secondOntology.text = chooser.selectedFile.toString
      if (!secondOntology.text.contains(".nt")) {
        val res = Dialog.showMessage(contents.head, "Please choose an ontology in NTriple format!", title)
      } else {
        O2 = secondOntology.text
//        O2triples = sparkSession1.rdf(lang1)(O2Classes).distinct(2)
        O2triples = p.graphPreprocessing(sparkSession1.rdf(lang1)(O2).distinct(2))
//        O2triples.coalesce(1, shuffle = true).saveAsTextFile("Pre-processedSecondOntology")
        Some(chooser.selectedFile)
      }
    } else None
  }

  def getLanguageForO1(): String ={
    if (naturalLanguageForO1.item == "English"){
      languageTagForO1 = "en"
    }
    else if (naturalLanguageForO1.item == "German"){
      languageTagForO1 = "de"
    }
    else if (naturalLanguageForO1.item == "Arabic"){
      languageTagForO1 = "ar"
    }
    else if (naturalLanguageForO1.item == "French"){
      languageTagForO1 = "fr"
    }
    languageTagForO1
  }
  def getLanguageForO2(): String ={
    if (naturalLanguageForO2.item == "English"){
      languageTagForO2 = "en"
    }
    else if (naturalLanguageForO2.item == "German"){
      languageTagForO2 = "de"
    }
    else if (naturalLanguageForO2.item == "Arabic"){
      languageTagForO2 = "ar"
    }
    else if (naturalLanguageForO2.item == "French"){
      languageTagForO2 = "fr"
    }
    languageTagForO2
  }

  def getStats() {
    val ontStat = new OntologyStatistics(sparkSession1)
    if (O1triples.isEmpty()||O2triples.isEmpty()) {
      val res = Dialog.showMessage(contents.head, "Please select the input ontologies first!", title)
    }
    else if (statsForO1.selected && !statsForO2.selected){
      println("===============================================")
      println("|      Statistics for the first ontology      |")
      println("===============================================")
      ontStat.getStatistics(O1triples)
    }
    else if (!statsForO1.selected && statsForO2.selected){
      println("===============================================")
      println("|      Statistics for the second ontology      |")
      println("===============================================")
      ontStat.getStatistics(O2triples)
    }
    else Dialog.showMessage(contents.head, "Please select statistics for the first or the second ontology!!", title)
  }

  def doMatch() {
    val ontoMatch = new Match(sparkSession1)
    if (O1triples.isEmpty() || O2triples.isEmpty()) {
      val res = Dialog.showMessage(contents.head, "Please select the two ontologies first to be matched!", title)
    }
    else if (naturalLanguageForO1.selection.index == 0 || naturalLanguageForO2.selection.index == 0){
      val res = Dialog.showMessage(contents.head, "Please choose the language of the two ontologies first.", title)
    }
    else {
      if (crosslingualStatus.selected == true) {
        val res = Dialog.showMessage(contents.head, "Matching type is " + crosslingualStatus.text, title)
        println("Matching type is: " + crosslingualStatus.text)
      } else {
        val res = Dialog.showMessage(contents.head, "Matching type is " + monolingualStatus.text, title)
        println("Matching type is: " + monolingualStatus.text)
      }
      val O1Name = O1triples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Ontology")))
        .map(x => x.getSubject.getLocalName).first()
      println("First ontology name is: "+O1Name.toString())
      val O2Name = O2triples.find(None, None, Some(NodeFactory.createURI("http://www.w3.org/2002/07/owl#Ontology")))
        .map(x => x.getSubject.getLocalName).first()
      println("Second ontology name is: "+O2Name.toString())
      ontoMatch.MatchOntologies(O1triples, O2triples, O1Name, O2Name, languageTagForO1, languageTagForO2, crosslingualStatus.selected, threshold = 1.0)
    }
  }

  def assess() {
    val quality = new QualityAssessmentForInputOntology(sparkSession1)
    if (O1triples.isEmpty() || O2triples.isEmpty()) {
      val res = Dialog.showMessage(contents.head, "Please select the two ontologies first.", title)
    } else {
      if (qualityAssessmentForO1.selected == true){
        println("===============================================")
        println("|  Quality assessment for the first ontology  |")
        println("===============================================")
        quality.GetQualityAssessmentForOntology(O1triples)
      }
      else if (qualityAssessmentForO2.selected == true){
        println("===============================================")
        println("| Quality assessment for the second ontology  |")
        println("===============================================")
        quality.GetQualityAssessmentForOntology(O2triples)
      }
    }
  }
  //  def reportAndClose() {
  //    println("Your name: " + firstOntology.text)
  //    println("You like Scala: " + likeScala.selected)
  //    println("Undergraduate: " + crosslingualStatus.selected)
  //    println("Graduate: " + monolingualStatus.selected)
  //    println("Professor: " + status3.selected)
  //    println("Gender: " + gender.selection.item +
  //      " (Index: " + gender.selection.index + ")")
  //    println("Comments: " + commentField.text)
  //    println("'Press me' is pressed: " + pressMe.selected)
  //    sys.exit(0)
  //  }
  def closeAndExit() {
    val res = Dialog.showConfirmation(contents.head, "Do you really want to quit?", optionType = Dialog.Options.YesNo, title = title)
    if (res == Dialog.Result.Ok) sys.exit(0)
  }
}