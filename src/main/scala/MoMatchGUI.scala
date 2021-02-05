import java.awt.Color
import java.io.File

import net.sansa_stack.rdf.spark.io._
import org.apache.jena.graph
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
//  val crosslingualStatus = new RadioButton("학부생")
//  val monolingualStatus = new RadioButton("대학원생")
//  val status3 = new RadioButton("교수")
//  status3.selected = true
//  val statusGroup1 = new ButtonGroup(crosslingualStatus, monolingualStatus, status3)
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
//      contents += crosslingualStatus
//      contents += Swing.HStrut(10)
//      contents += monolingualStatus
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
//    println("Undergraduate: " + crosslingualStatus.selected)
//    println("Graduate: " + monolingualStatus.selected)
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

  val firstOntology = new TextField {
    columns = 32
  }
  firstOntology.editable = false
  val secondOntology = new TextField {
    columns = 32
  }
  secondOntology.editable = false
  //  val likeScala = new CheckBox("I like Scala")
  //  likeScala.selected = true
  val crosslingualStatus = new RadioButton("Cross-lingual matching")
  val monolingualStatus = new RadioButton("Monolingual matching")
  crosslingualStatus.selected = true // do Cross-lingual matching by default
  val statusGroup1: ButtonGroup = new ButtonGroup(crosslingualStatus, monolingualStatus)
  val statsForO1 = new RadioButton("Statistics for the first ontology")
  val statsForO2 = new RadioButton("Statistics for the second ontology")
  val statsGroup2: ButtonGroup = new ButtonGroup(statsForO1, statsForO2)
  val naturalLanguageForO1 = new ComboBox(List("Detect language", "English", "German", "French", "Arabic"))
  val naturalLanguageForO2 = new ComboBox(List("Detect language", "English", "German", "French", "Arabic"))
  //  val gender = new ComboBox(List("don't know", "female", "male"))
  //  val commentField = new TextArea { rows = 8; lineWrap = true; wordWrap = true }
  //  val pressMe = new ToggleButton("Press me!")
  //  pressMe.selected = false
  //  restrictHeight(firstOntology)
  //  restrictHeight(secondOntology)
  //  restrictHeight(gender)
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new BoxPanel(Orientation.Vertical) {
      border = LineBorder(Color.gray)
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
      contents += Swing.VStrut(5)
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

    contents += new BoxPanel(Orientation.Vertical) {
      border = LineBorder(Color.gray)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new Label("Ontology Statistics:")
        contents += Swing.HStrut(5)
        contents += statsForO1
        contents += Swing.HStrut(5)
        contents += statsForO2
        contents += Swing.HStrut(5)
        contents += Button("Get statistics") {
          getStats()
        }
      }
//      contents += Swing.VStrut(5)
//      contents += new BoxPanel(Orientation.Horizontal) {
//        border = EmptyBorder(10)
//        contents += new Label("Statistics for the second ontology:")
//        contents += Swing.HStrut(5)
//        contents += Button("Get statistics") {
//          getStatsForO2()
//        }
//      }
    }

    //    contents += Swing.VStrut(5)
    //    contents += likeScala
    //    contents += Swing.VStrut(5)
    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Vertical) {
      border = LineBorder(Color.gray)
      contents += new BoxPanel(Orientation.Horizontal) {
        border = EmptyBorder(10)
        contents += new Label("Matching type:")
        contents += Swing.HStrut(5)
        contents += crosslingualStatus //Cross-lingual matching
        contents += Swing.HStrut(10)
        contents += monolingualStatus //Monolingual matching
        //      contents += Swing.HStrut(10)
        //      contents += status3
        contents += Swing.HStrut(5)
        contents += Button("Match") {
          doMatch()
        }
      }
    }
    //    contents += Swing.VStrut(5)
    //    contents += new BoxPanel(Orientation.Horizontal) {
    //      contents += Button("Match"){}
    //    }
    //    contents += Swing.VStrut(5)
    //    contents += new BoxPanel(Orientation.Horizontal) {
    //      contents += new Label("Gender")
    //      contents += Swing.HStrut(20)
    //      contents += gender
    //    }
    contents += Swing.VStrut(5)
    //    contents += new Label("Comments")
    //    contents += Swing.VStrut(3)
    //    contents += new ScrollPane(commentField)
    //    contents += Swing.VStrut(5)
    contents += new BoxPanel(Orientation.Horizontal) {
      //      contents += pressMe
      contents += Swing.HGlue
      contents += Button("Close") {
        closeAndExit()
      }
    }
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
        O1triples = sparkSession1.rdf(lang1)(O1).distinct(2)
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
        O2triples = sparkSession1.rdf(lang1)(O2).distinct(2)
        Some(chooser.selectedFile)
      }
    } else None
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
    } else {
      if (crosslingualStatus.selected == true) {
        val res = Dialog.showMessage(contents.head, "Matching type is " + crosslingualStatus.text, title)
        println("Matching type is: " + crosslingualStatus.text)
      } else {
        val res = Dialog.showMessage(contents.head, "Matching type is " + monolingualStatus.text, title)
        println("Matching type is: " + monolingualStatus.text)
      }
      ontoMatch.MatchOntologies(O1triples, O2triples, "Conference-de", crosslingualStatus.selected)
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