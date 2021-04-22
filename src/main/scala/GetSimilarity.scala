import com.github.vickumar1981.stringdistance.StringDistance._
import com.rockymadden.stringmetric.phonetic.SoundexMetric
import com.rockymadden.stringmetric.similarity._
import edu.cmu.lti.lexical_db.NictWordNet
import edu.cmu.lti.ws4j.impl.Path
import edu.cmu.lti.ws4j.util.WS4JConfiguration
import me.xdrop.fuzzywuzzy.FuzzySearch
/*
* Created by Shimaa 17.02.2021
* */
class GetSimilarity extends Serializable{
  val db = new NictWordNet with Serializable
  val processing = new PreProcessing()

  /**
    * Get the similarity between two sentences using Jaccard or WordNet.*/
  def getSimilarity(sentence1: String, sentence2: String): Double={
    val sent1 = processing.sentenceLemmatization(sentence1)
    val sent2 = processing.sentenceLemmatization(sentence2)
//    var sim = this.getJaroStringSimilarity(sent1, sent2)
//    var sim = this.getJaroWinklerStringSimilarity(sent1, sent2)
//    var sim = this.getLevenshteinStringSimilarity(sent1, sent2)
    var sim = this.getHammingDistanceSimilarity(sent1, sent2)
//    var sim = this.getRatioSimilarity(sent1, sent2)
//    var sim = this.getPartialRatioSimilarity(sent1, sent2)
//    var sim = this.getPartialTokenSortSimilarity(sent1, sent2)
//    var sim = this.getTokenSortSimilarity(sent1, sent2)
//    var sim = this.getCosineSimilarity(sent1, sent2)
//    var sim = this.getDiceSimilarity(sent1, sent2)
//    var sim = this.getJaccardStringSimilarity(sent1, sent2)
//    var sim = this.getOverlapCoefficientStringSimilarity(sent1, sent2)
//    var sim = this.getTverskyStringSimilarity(sent1, sent2)


    //    var jaccardSim = 1.0
//    if (sim != 1)
//      sim = (this.sentenceSimilarity(sent1,sent2)+this.sentenceSimilarity(sent2,sent1))/2
    sim
  }

  //############################# Sequence-based measures ########################################
  /**
    * Get the Jaro similarity between two strings.*/
  def getJaroStringSimilarity(s1: String, s2: String): Double={
    val jaroSim = JaroMetric.compare(s1, s2).head
    jaroSim
  }
  /**
    * Get the Jaro-Winkler similarity between two strings.*/
  def getJaroWinklerStringSimilarity(s1: String, s2: String): Double={
    val jaroWiinklerSim = JaroWinklerMetric.compare(s1, s2).head
    jaroWiinklerSim
  }
  /**
    * Get the Levenshtein similarity between two strings.*/
  def getLevenshteinStringSimilarity(s1: String, s2: String): Double={
//    val levenshteinSim = LevenshteinMetric.compare(s1, s2).head
    //Computes the normalized Levenshtein similarity score between two strings
    val levenshteinSim = Levenshtein.score(s1, s2)
    levenshteinSim
  }
  /**
    * Get Hamming distance between two strings.*/
  def getHammingDistanceSimilarity(s1: String, s2: String): Double={
//    val hammDistSim = HammingMetric.compare(s1, s2).head
    //Normalized Hamming
    val hammDistSim = Hamming.score(s1, s2)
    hammDistSim
  }
  /**
    * Get ratio similarity between two strings.*/
  def getRatioSimilarity(s1: String, s2: String): Double={
    val ratioSim = FuzzySearch.ratio(s1, s2)/100
    ratioSim
  }
  /**
    * Get partial ratio similarity between two strings.*/
  def getPartialRatioSimilarity(s1: String, s2: String): Double={
    val partialRatioSim = FuzzySearch.partialRatio(s1, s2)/100
    partialRatioSim
  }
  /**
    * Get partial token sort similarity between two strings.*/
  def getPartialTokenSortSimilarity(s1: String, s2: String): Double={
    val partialTokenSortSim = FuzzySearch.tokenSortPartialRatio(s1, s2)/100
    partialTokenSortSim
  }
  /**
    * Get token sort similarity between two strings.*/
  def getTokenSortSimilarity(s1: String, s2: String): Double={
    val tokenSortSim = FuzzySearch.tokenSortRatio(s1, s2)/100
    tokenSortSim
  }

  //############################# Token-based measures ########################################
  /**
    * Get cosine similarity between two strings.*/
  def getCosineSimilarity(s1: String, s2: String): Double={
    val cosineSim = Cosine.score(s1, s2)
    cosineSim
  }
  /**
    * Get Dice similarity between two strings.*/
  def getDiceSimilarity(s1: String, s2: String): Double={
    val diceSim = DiceSorensenMetric(1).compare(s1, s2).head
    diceSim
  }
  /**
    * Get the Jaccard similarity between two strings.*/
  def getJaccardStringSimilarity(s1: String, s2: String): Double={
//    val j = new Jaccard(3)
//    val jaccardSim = j.similarity(s1, s2)
    val jaccardSim = JaccardMetric(1).compare(s1, s2).head
    jaccardSim
  }
  /**
    * Get the Overlap Coefficient similarity between two strings.*/
  def getOverlapCoefficientStringSimilarity(s1: String, s2: String): Double={
    val overlapSim = OverlapMetric(1).compare(s1, s2).head
    overlapSim
  }
  /**
    * Get Tversky similarity between two strings.*/
  def getTverskyStringSimilarity(s1: String, s2: String): Double={
    val tverskySim = Tversky.score(s1, s2, 0.5)
    tverskySim
  }
  // TfIdf

  //############################## Hybrid-based measures ##############################
  /*
  * GeneralizedJaccard
  MongeElkan
  SoftTfIdf
  */

  //############################## Phonetic-based measures ##############################
  /**
    * Get Soundex phonetic similarity between two strings.*/
  def getSoundexSimilarity(s1: String, s2: String): Boolean={
    val soundexSim= SoundexMetric.compare(s1, s2).head
    soundexSim
  }



//  /**
//    * Get the Jaccard similarity between two strings.*/
//  def getJaccardStringSimilarity(s1: String, s2: String): Double={
//    val j = new Jaccard(3)
//    var jaccardSim = j.similarity(s1, s2)
//    jaccardSim
//  }
//
//  /**
//    * Get the Levenshtein similarity between two strings.*/
//  def getLevenshteinStringSimilarity(s1: String, s2: String): Double={
//    val l = new Levenshtein()
//    var levenshteinSim = l.distance(s1, s2)
//    levenshteinSim
//  }
//
//  /**
//    * Get the  Jaro-Winkler similarity between two strings.*/
//  def getJaroWinklerStringSimilarity(s1: String, s2: String): Double={
//    val jw = new JaroWinkler()
//    var JaroWinklerSim = jw.similarity(s1, s2)
//    JaroWinklerSim
//  }
//  def getStringSimilarity(s1: String, s2: String): Double={
//    //    val cos = new Cosine(2)
//    //    var cosSim = cos.similarity(s1, s2)
//    ////    System.out.println("Cosine similarity is "+cosSim)
//    //  import info.debatty.java.stringsimilarity.JaroWinkler
//    //  val jw = new JaroWinkler
//    //  var jarSim = jw.similarity(s1, s2)
//    //
//    //    val l = new NormalizedLevenshtein()
//    //    var levenshteinSim = l.distance(s1, s2)
//    //    System.out.println("Normalized Levenshtein similarity is "+levenshteinSim)
//
//    val j = new Jaccard(3)
//    var jaccardSim = j.similarity(s1, s2)
//    //    System.out.println("Jaccard similarity is "+jaccardSim)
//
//    //    import info.debatty.java.stringsimilarity.NGram
//    //    val trigram = new NGram(3)
//    //    var trigramSim = trigram.distance(s1, s2)
//    //    System.out.println("trigram similarity is "+trigram.distance(s1, s2))
//
//    jaccardSim
//
//  }

  /**
    * Get the path similarity between two strings based on wordNet.*/
def getPathSimilarity(word1: String, word2: String): Double={
  WS4JConfiguration.getInstance.setMFS(true)
  val path = new Path(db)
  var pathSim = path.calcRelatednessOfWords(word1,word2)
  if (pathSim>=1.0){
    pathSim = 1.0
    //      println("path similarity between "+word1+" and "+word2+" = "+pathSim)
  }
  else if (pathSim == 0.0)
    pathSim = -1.0
  pathSim
}
  /**
    * Get the similarity between two sentences using path similarity in WordNet.*/
  def sentenceSimilarity(sentence1: String, sentence2: String): Double={
    var simScoure = 0.0
    var count = 0.0
    var s = 0.0
    for(word1 <- sentence1.split(" ")){
      var bestSroce: List[Double] = List()
      for (word2 <- sentence2.split(" "))
      {

        bestSroce ::= this.getPathSimilarity(word1,word2)
      }
      if (bestSroce.length > 1)
        s = max(bestSroce)
      else s = bestSroce.head
//      if (s != -2){
//        simScoure += s.asInstanceOf[Double]
//        count += 1
//      }
      simScoure += s.asInstanceOf[Double]
      count += 1
    }
    simScoure = Math.round((simScoure / count)*1000)/1000.0
    //    println(sentence1 + " ##### "+ sentence2 +" = "+ simScoure)
    simScoure
  }

  /**
    * Calculate the symmetric similarity between two sentences.*/
  def symmetricSentenceSimilarity(sentence1: String, sentence2: String): Double ={
//    var sent1 = processing.sentenceLemmatization(sentence1)
//    var sent2 = processing.sentenceLemmatization(sentence2)
    var sim = (sentenceSimilarity(sentence1,sentence2) + sentenceSimilarity(sentence2,sentence1))/2
//    var sim = (sentenceSimilarity(sent1,sent2) + sentenceSimilarity(sent2,sent1))/2
    sim
  }

  /**
    * Get the maximum value from a list of numbers.*/
  def max(lst: List[Double]): Double={
    var maxValue = lst.max
    if (maxValue == lst.head && maxValue == lst.last)
//      maxValue = -2
      maxValue = 0
    maxValue
  }
}
