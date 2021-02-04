import net.sansa_stack.rdf.spark.io._
import java.io.File

import org.apache.jena.graph
import org.apache.jena.riot.Lang
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.swing._
import scala.swing.event._


object MoMatchGUITry {
  def main(args: Array[String]) {
    val ui = new UI
    ui.visible = true
  }
}
//class UI extends MainFrame {
//  title = "MoMatch-Multilingual Ontology Matching"
//  preferredSize = new Dimension(320, 240)
//  contents = new BoxPanel(Orientation.Vertical) {
//    contents += new Label("Look at me!")
//    contents += Swing.VStrut(10)
//    contents += Swing.Glue
//    contents += Button("Press me, please") { println("Thank you") }
//    contents += Swing.VStrut(5)
//    contents += Button("Close") { sys.exit(0) }
//    border = Swing.EmptyBorder(10, 10, 10, 10)
//  }
//}

//class UI extends MainFrame {
//  val la = new Label("Look at me!")
//
//  la.foreground = Color.BLUE
//  title = "GUI Program #4"
//
//  contents = new BoxPanel(Orientation.Vertical) {
//    contents += la
//    contents += Swing.VStrut(10)
//    contents += Swing.Glue
//    contents += Button("Press me, please") { pressMe() }
//    contents += Swing.VStrut(5)
//    contents += Button("Change text") { changeText() }
//    contents += Swing.VStrut(5)
//    contents += Button("Close") { closeMe() }
//    border = Swing.EmptyBorder(10, 10, 10, 10)
//  }
//
//  def pressMe() {
//    Dialog.showMessage(contents.head, "Thank you!", title="You pressed me")
//  }
//
//  def changeText() {
//    val r = Dialog.showInput(contents.head, "New label text", initial=la.text)
//    r match {
//      case Some(s) => la.text = s
//      case None =>
//    }
//  }
//
//  def closeMe() {
//    val res = Dialog.showConfirmation(contents.head,
//      "Do you really want to quit?",
//      optionType=Dialog.Options.YesNo,
//      title=title)
//    if (res == Dialog.Result.Ok)
//      sys.exit(0)
//  }
//
//}

//class UI extends MainFrame {
//  def restrictHeight(s: Component) {
//    s.maximumSize = new Dimension(Short.MaxValue, s.preferredSize.height)
//  }
//
//  title = "MoMatch-Multilingual Ontology Matching"
//
//  val firstOntology = new TextField { columns = 32 }
//  val likeScala = new CheckBox("I like Scala")
//  likeScala.selected = true
//  val status1 = new RadioButton("학부생")
//  val status2 = new RadioButton("대학원생")
//  val status3 = new RadioButton("교수")
//  status3.selected = true
//  val statusGroup = new ButtonGroup(status1, status2, status3)
//  val gender = new ComboBox(List("don't know", "female", "male"))
//  val commentField = new TextArea { rows = 8; lineWrap = true; wordWrap = true }
//  val pressMe = new ToggleButton("Press me!")
//  pressMe.selected = true
//
//  restrictHeight(firstOntology)
//  restrictHeight(gender)
//
//  contents = new BoxPanel(Orientation.Vertical) {
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents += new Label("My name")
//      contents += Swing.HStrut(5)
//      contents += firstOntology
//    }
//    contents += Swing.VStrut(5)
//    contents += likeScala
//    contents += Swing.VStrut(5)
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents += status1
//      contents += Swing.HStrut(10)
//      contents += status2
//      contents += Swing.HStrut(10)
//      contents += status3
//    }
//    contents += Swing.VStrut(5)
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents += new Label("Gender")
//      contents += Swing.HStrut(20)
//      contents += gender
//    }
//    contents += Swing.VStrut(5)
//    contents += new Label("Comments")
//    contents += Swing.VStrut(3)
//    contents += new ScrollPane(commentField)
//    contents += Swing.VStrut(5)
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents += pressMe
//      contents += Swing.HGlue
//      contents += Button("Close") { reportAndClose() }
//    }
//    for (e <- contents)
//      e.xLayoutAlignment = 0.0
//    border = Swing.EmptyBorder(10, 10, 10, 10)
//  }
//
//  def reportAndClose() {
//    println("Your name: " + firstOntology.text)
//    println("You like Scala: " + likeScala.selected)
//    println("Undergraduate: " + status1.selected)
//    println("Graduate: " + status2.selected)
//    println("Professor: " + status3.selected)
//    println("Gender: " + gender.selection.item +
//      " (Index: " + gender.selection.index + ")")
//    println("Comments: " + commentField.text)
//    println("'Press me' is pressed: " + pressMe.selected)
//    sys.exit(0)
//  }
//}

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
//  val O2triples: RDD[graph.Triple] = sparkSession1.rdf(lang1)(O2).distinct(2)
  var O2triples: RDD[graph.Triple] = sparkSession1.sparkContext.emptyRDD[graph.Triple]


  def restrictHeight(s: Component) {
    s.maximumSize = new Dimension(Short.MaxValue, s.preferredSize.height)
  }

  title = "MoMatch - Multilingual Ontology Matching"

  val firstOntology = new TextField { columns = 32 }
  firstOntology.editable = false
  val secondOntology = new TextField { columns = 32 }
  secondOntology.editable = false
//  val likeScala = new CheckBox("I like Scala")
//  likeScala.selected = true
  val status1 = new RadioButton("Cross-lingual matching")
  val status2 = new RadioButton("Monolingual matching")
  status1.selected = true // do Cross-lingual matching by default
  val statusGroup: ButtonGroup = new ButtonGroup(status1, status2)
  val gender = new ComboBox(List("don't know", "female", "male"))
  val commentField = new TextArea { rows = 8; lineWrap = true; wordWrap = true }
  val pressMe = new ToggleButton("Press me!")
  pressMe.selected = false

  restrictHeight(firstOntology)
  restrictHeight(gender)

  contents = new BoxPanel(Orientation.Vertical) {
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("First ontology")
      contents += Swing.HStrut(5)
      contents += firstOntology
      contents += Swing.HStrut(5)
      contents += Button("Browse") {browseForFirstOntology()}
    }
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Second ontology")
      contents += Swing.HStrut(5)
      contents += secondOntology
      contents += Swing.HStrut(5)
      contents += Button("Browse") {browseForSecondOntology()}
    }
    contents += Swing.VStrut(10)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Statistics for the first ontology:")
      contents += Swing.HStrut(5)
      contents += Button("Get statistics"){ getStatsForO1()}
    }
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Statistics for the second ontology:")
      contents += Swing.HStrut(5)
      contents += Button("Get statistics"){getStatsForO2()}
    }

//    contents += Swing.VStrut(5)
//    contents += likeScala
//    contents += Swing.VStrut(5)

    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Matching type:")
      contents += Swing.HStrut(5)
      contents += status1 //Cross-lingual matching
      contents += Swing.HStrut(10)
      contents += status2 //Monolingual matching
//      contents += Swing.HStrut(10)
//      contents += status3
      contents += Swing.HStrut(5)
      contents += Button("Match"){doMatch()}
    }
//    contents += Swing.VStrut(5)
//    contents += new BoxPanel(Orientation.Horizontal) {
//      contents += Button("Match"){}
//    }
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Gender")
      contents += Swing.HStrut(20)
      contents += gender
    }
    contents += Swing.VStrut(5)
    contents += new Label("Comments")
    contents += Swing.VStrut(3)
    contents += new ScrollPane(commentField)
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += pressMe
      contents += Swing.HGlue
      contents += Button("Close") { closeAndExit() }
    }
    for (e <- contents)
      e.xLayoutAlignment = 0.0
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  listenTo(firstOntology)
  listenTo(commentField)
  listenTo(gender.selection)
//  listenTo(likeScala)
  listenTo(status1)
  listenTo(status2)
  listenTo(pressMe)

  reactions += {
    case EditDone(`firstOntology`) =>
      println("Your name is now: " + firstOntology.text)
    case EditDone(`commentField`) =>
      println("You changed the comments")
    case SelectionChanged(`gender`) =>
      println("Your gender is now: " + gender.selection.item)
//    case ButtonClicked(`likeScala`) =>
//      if (!likeScala.selected) {
//        if (Dialog.showConfirmation(contents.head,
//          "Are you sure you don't like Scala?")
//          != Dialog.Result.Yes)
//          likeScala.selected = true
//      }
    case ButtonClicked(s) =>
      println("Button click on button: '" + s.text + "'")
  }

  def browseForFirstOntology(){
    val chooser = new FileChooser(new File("."))
    chooser.title = "Select a file"
    val result = chooser.showOpenDialog(null)
    if (result == FileChooser.Result.Approve) {
//      println("Approve -- " + chooser.selectedFile)
      firstOntology.text = chooser.selectedFile.toString
      if (!firstOntology.text.contains(".nt")){
        val res = Dialog.showMessage(contents.head,"Please choose an ontology in NTriple format!",title)
      }
      else {
        O1 = firstOntology.text
        O1triples = sparkSession1.rdf(lang1)(O1).distinct(2)
        Some(chooser.selectedFile)
      }
    } else None
  }
  def browseForSecondOntology(){
    val chooser = new FileChooser(new File("."))
    chooser.title = "Select a file"
    val result = chooser.showOpenDialog(null)
    if (result == FileChooser.Result.Approve) {
//      println("Approve -- " + chooser.selectedFile)
      secondOntology.text = chooser.selectedFile.toString
      if (!secondOntology.text.contains(".nt")){
        val res = Dialog.showMessage(contents.head,"Please choose an ontology in NTriple format!",title)
      }
      else {
        O2 = secondOntology.text
        O2triples = sparkSession1.rdf(lang1)(O2).distinct(2)
        Some(chooser.selectedFile)
      }
    } else None
  }
  def getStatsForO1(){
    val ontStat = new OntologyStatistics(sparkSession1)
    if (O1triples.isEmpty()){
      val res = Dialog.showMessage(contents.head,"Please select the first ontology first!",title)
    }
    else
      ontStat.getStatistics(O1triples)
  }
  def getStatsForO2(){
    val ontStat = new OntologyStatistics(sparkSession1)
    if (O2triples.isEmpty()){
      val res = Dialog.showMessage(contents.head,"Please select the second ontology first!",title)
    }
    else
      ontStat.getStatistics(O2triples)
  }
  def doMatch(){
    val ontoMatch = new Match(sparkSession1)
    if (O1triples.isEmpty() || O2triples.isEmpty()){
      val res = Dialog.showMessage(contents.head,"Please select the two ontologies first to be matched!",title)
    }
    else{
      if (status1.selected == true){
        val res = Dialog.showMessage(contents.head,"Matching type is "+status1.text,title)
        println("Matching type is: "+status1.text)
      }
      else{
        val res = Dialog.showMessage(contents.head,"Matching type is "+status2.text,title)
        println("Matching type is: "+status2.text)
      }
      ontoMatch.MatchOntologies(O1triples, O2triples, "Conference-de", status1.selected)
    }
  }
//  def reportAndClose() {
//    println("Your name: " + firstOntology.text)
//    println("You like Scala: " + likeScala.selected)
//    println("Undergraduate: " + status1.selected)
//    println("Graduate: " + status2.selected)
//    println("Professor: " + status3.selected)
//    println("Gender: " + gender.selection.item +
//      " (Index: " + gender.selection.index + ")")
//    println("Comments: " + commentField.text)
//    println("'Press me' is pressed: " + pressMe.selected)
//    sys.exit(0)
//  }
  def closeAndExit() {
    val res = Dialog.showConfirmation(contents.head,
      "Do you really want to quit?",
      optionType=Dialog.Options.YesNo,
      title=title)
    if (res == Dialog.Result.Ok)
      sys.exit(0)
  }
}