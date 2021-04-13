import com.github.vickumar1981.stringdistance.StringDistance._
import com.github.vickumar1981.stringdistance.impl.{ConstantGap, LinearGap}
import com.rockymadden.stringmetric._
import com.rockymadden.stringmetric.phonetic._
import com.rockymadden.stringmetric.similarity._
import info.debatty.java.stringsimilarity.Jaccard
import me.xdrop.fuzzywuzzy.FuzzySearch
import com.github.vickumar1981.stringdistance.StringConverter._

object Try {
  def main(args: Array[String]): Unit = {
    println("############################## Sequence-based measures ##############################")
    //      Jaro Metric:
    println("Jaro Metric:")
    println(JaroMetric.compare("dwayne", "duane").head) // 0.8222222222222223
    println(JaroMetric.compare("jones", "johnson").head) // 0.7904761904761904
    println(JaroMetric.compare("MARTHA", "MARHTA").head)
    println(JaroMetric.compare("fvie", "ten").head) // 0.0
    //    Jaro-Winkler Metric:
    println("Jaro-Winkler Metric:")
    println(JaroWinklerMetric.compare("dwayne", "duane").head) // 0.8400000000000001
    println(JaroWinklerMetric.compare("jones", "johnson").head) // 0.8323809523809523
    println(JaroWinklerMetric.compare("MARTHA", "MARHTA").head)
    println(JaroWinklerMetric.compare("fvie", "ten").head) // 0.0
    //    Levenshtein Metric:
    println("Levenshtein Metric:")
    println(LevenshteinMetric.compare("sitting", "kitten").head) // 3
    println(LevenshteinMetric.compare("cake", "drake").head) // 2
    println(LevenshteinMetric.compare("example", "samples").head) // 3
    //      Hamming Metric:
    println("Hamming Metric:")
    println(HammingMetric.compare("toned", "roses").head) // 3
    println(HammingMetric.compare("alex", "john").head) // 4
    // Needleman Wunsch
    val needlemanWunsch: Double = NeedlemanWunsch.score("dva", "deeva", ConstantGap())  // 0.667 "martha", "marhta"
    println("Needleman Wunsch = "+needlemanWunsch)
//    Simple Ratio
//    val ratio = FuzzySearch.ratio("mysmilarstring","myawfullysimilarstirng") //72
//    val ratio = FuzzySearch.ratio("Robert","Rupert") //67
    val ratio = FuzzySearch.ratio("Sue","sue") //67
    println("Ratio = " + ratio)
//    Partial Ratio
    val partialRation: Int = FuzzySearch.partialRatio("similar", "somewhresimlrbetweenthisstring") //71
    println("Partial ratio =" + partialRation)
//    Partial Token Sort
    val partailTokenSort = FuzzySearch.tokenSortPartialRatio("order words out of","  words out of order") //100
    println("Partial token sort = "+partailTokenSort)
    //    Token Sort
//    val tokenSort = FuzzySearch.tokenSortRatio("order words out of","  words out of order") //100
    val tokenSort = FuzzySearch.tokenSortRatio("great is scala","java is great") //81
    println("Token sort = "+tokenSort)
    // Smith Waterman Similarities
    val smithWaterman: Double = SmithWaterman.score("cat", "hat", (LinearGap(gapValue = -1), Integer.MAX_VALUE)) //"martha", "marhta"
    println("Smith Waterman Similarities = "+smithWaterman)
    val smithWatermanGotoh1: Double = SmithWatermanGotoh.score("cat", "hat", ConstantGap())
    println("Smith Waterman score = "+smithWatermanGotoh1)
    println("latest = " +"martha".smithWatermanGotoh("marhta"))


    println("############################## Token-based measures ##############################")
    // Cosine Similarity
    val cosSimilarity: Double = Cosine.score("hello", "chello")  // 0.935
    println("Cosine similarity = "+cosSimilarity)
    println("Cosine similarity = "+Cosine.score("data", "science")) //0.7071067811865475
     //    Dice / Sorensen Metric:
    println("Dice / Sorensen Metric:")
    val d: Double = DiceSorensenMetric(1).compare("night", "nacht").head
    println("Dice similarity = "+d) // 0.6
    println("Dice similarity = " + DiceSorensenMetric(1).compare("context", "contact").head) // 0.7142857142857143
    println("Dice similarity = " + DiceSorensenMetric(1).compare("data", "science").head) //0.6666666666666666
    //    Jaccard Metric:
    println("Jaccard Metric:")
    println(JaccardMetric(1).compare("night", "nacht").head) // 0.3
    println(JaccardMetric(1).compare("context", "contact").head) // 0.35714285714285715
    val j = new Jaccard(1)
    var jaccardSim = j.similarity("night", "nacht")
    println("jaccardSim = "+jaccardSim)
    //      Overlap Metric:
    println("Overlap Metric:")
    println(OverlapMetric(1).compare("night", "nacht").head) // 0.6
    println(OverlapMetric(1).compare("context", "contact").head) // 0.7142857142857143
    // Tversky Similarity
    val tversky: Double = Tversky.score("karolin", "kathrin", 0.5)  // 0.333
    println("Tversky Similarity = "+tversky)
    // TfIdf

    //############################## Hybrid-based measures ##############################
/*
* GeneralizedJaccard
MongeElkan
SoftTfIdf
*/

    //############################## Phonetic-based measures ##############################
    //    Soundex Metric:
    println("Soundex Metric:")
    println(SoundexMetric.compare("robert", "rupert").head) // true
    println(SoundexMetric.compare("robert", "rubin").head) // false

//######################################################################################################
    //      Hamming Metric:
    println("Hamming Metric:")
    println(HammingMetric.compare("toned", "roses")) // 3
    println(HammingMetric.compare("1011101", "1001001")) // 2
        //    N-Gram Metric:
    println("N-Gram Metric:")
    println(NGramMetric(1).compare("night", "nacht")) // 0.6
    println(NGramMetric(2).compare("night", "nacht")) // 0.25
    println(NGramMetric(2).compare("context", "contact")) // 0.5
    //      Ratcliff/Obershelp Metric:
    println("Ratcliff/Obershelp Metric:")
    println(RatcliffObershelpMetric.compare("aleksander", "alexandre")) // 0.7368421052631579
    println(RatcliffObershelpMetric.compare("pennsylvania", "pencilvaneya")) // 0.6666666666666666
    //    Weighted Levenshtein Metric:
    println("Weighted Levenshtein Metric:")
    println(WeightedLevenshteinMetric(10, 0.1, 1).compare("book", "back")) // 2
    println(WeightedLevenshteinMetric(10, 0.1, 1).compare("hosp", "hospital")) // 0.4
    println(WeightedLevenshteinMetric(10, 0.1, 1).compare("hospital", "hosp")) // 40
    //Phonetic package
    //    Metaphone Metric:
    println("Metaphone Metric:")
    println(MetaphoneMetric.compare("merci", "mercy")) // true
    println(MetaphoneMetric.compare("dumb", "gum")) // false
    //    Metaphone Algorithm:
    println("Metaphone Algorithm:")
    println(MetaphoneAlgorithm.compute("dumb")) // tm
    println(MetaphoneAlgorithm.compute("knuth")) // n0
    //    NYSIIS Metric:
    println("NYSIIS Metric:")
    println(NysiisMetric.compare("ham", "hum")) // true
    println(NysiisMetric.compare("dumb", "gum")) // false
    //    NYSIIS Algorithm:
    println("NYSIIS Algorithm:")
    println(NysiisAlgorithm.compute("macintosh")) // mcant
    println(NysiisAlgorithm.compute("knuth")) // nnat
    //    Refined NYSIIS Metric:
    println("Refined NYSIIS Metric:")
    println(RefinedNysiisMetric.compare("ham", "hum")) // true
    println(RefinedNysiisMetric.compare("dumb", "gum")) // false
    //    Refined NYSIIS Algorithm:
    println("Refined NYSIIS Algorithm:")
    println(RefinedNysiisAlgorithm.compute("macintosh")) // mcantas
    println(RefinedNysiisAlgorithm.compute("westerlund")) // wastarlad
    //    Refined Soundex Metric:
    println("Refined Soundex Metric:")
    println(RefinedSoundexMetric.compare("robert", "rupert")) // true
    println(RefinedSoundexMetric.compare("robert", "rubin")) // false
    //    Refined Soundex Algorithm:
    println("Refined Soundex Algorithm:")
    println(RefinedSoundexAlgorithm.compute("hairs")) // h093
    println(RefinedSoundexAlgorithm.compute("lambert")) // l7081096

    //    Soundex Algorithm:
    println("Soundex Algorithm:")
    println(SoundexAlgorithm.compute("rupert")) // r163
    println(SoundexAlgorithm.compute("lukasiewicz")) // l222
    //Convenience objects
    //    StringAlgorithm:
    println("StringAlgorithm:")
    println(StringAlgorithm.computeWithMetaphone("abcdef"))
    println(StringAlgorithm.computeWithNysiis("abcdef"))
    //    StringMetric:
    println("StringMetric:")
    println(StringMetric.compareWithJaccard(1)("abcdef", "abcxyz"))
    println(StringMetric.compareWithJaroWinkler("abcdef", "abcxyz"))
    /*
        // Cosine Similarity
        val cosSimilarity: Double = Cosine.score("hello", "chello")  // 0.935
        println("Cosine similarity = "+cosSimilarity)

        // Damerau-Levenshtein Distance
        val damerauDist: Int = Damerau.distance("martha", "marhta")  // 1
        println("Damerau distance = "+damerauDist)
        val damerau: Double = Damerau.score("martha", "marhta")  // 0.833
        println("Damerau score = "+damerau)

        // Dice Coefficient
        val diceCoefficient: Double = DiceCoefficient.score("martha", "marhta")  // 0.4
        println("Dice Coefficient = "+diceCoefficient)

        // Hamming Distance
        val hammingDist: Int = Hamming.distance("martha", "marhta")  // 2
        println("Hamming Distance = "+hammingDist)
        val hamming: Double = Hamming.score("martha", "marhta")  // 0.667
        println("Hamming score = "+hamming)

        // Jaccard Similarity
        val jaccard: Double = Jaccard.score("karolin", "kathrin", 1)
    //    val jaccard: Double = Jaccard.score("night", "nacht", 1)
        println("Jaccard Similarity = "+jaccard)

        // Jaro and Jaro Winkler
        val jaro: Double = Jaro.score("martha", "marhta")  // 0.944
        println("Jaro score = "+jaro)
        val jaroWinkler: Double = JaroWinkler.score("martha", "marhta", 0.1)  // 0.961
        println("JaroWinkler score = "+jaroWinkler)

        // Levenshtein Distance
        val levenshteinDist: Int = Levenshtein.distance("martha", "marhta")  // 2
        println("Levenshtein Distance = "+levenshteinDist)
        val levenshtein: Double = Levenshtein.score("martha", "marhta")  // 0.667
        println("Levenshtein score = "+levenshtein)

        // Longest Common Subsequence
        val longestCommonSubSeq: Int = LongestCommonSeq.distance("martha", "marhta")  // 5
        println("Longest Common Subsequence = "+longestCommonSubSeq)

        // Needleman Wunsch
        val needlemanWunsch: Double = NeedlemanWunsch.score("martha", "marhta", ConstantGap())  // 0.667
        println("Needleman Wunsch = "+needlemanWunsch)

        // N-Gram Similarity and Distance
        val ngramDist: Int = NGram.distance("karolin", "kathrin", 1)  // 5
        println("N-Gram distance = "+ngramDist)
        val bigramDist: Int = NGram.distance("karolin", "kathrin", 2)  // 2
        println("bi-Gram distance = "+bigramDist)
        val ngramSimilarity: Double = NGram.score("karolin", "kathrin", 1)  // 0.714
        println("N-Gram similarity = "+ngramSimilarity)
        val bigramSimilarity: Double = NGram.score("karolin", "kathrin", 2)  // 0.333
        println("bi-Gram similarity = "+bigramSimilarity)

        // N-Gram tokens, returns a List[String]
        val tokens: List[String] = NGram.tokens("martha", 2)  // List("ma", "ar", "rt", "th", "ha")
        println("N-Gram tokens: ")
        tokens.foreach(println(_))

        // Overlap Similarity
        val overlap: Double = Overlap.score("karolin", "kathrin", 1)  // 0.286
        println("Overlap Similarity = "+overlap)
        val overlapBiGram: Double = Overlap.score("karolin", "kathrin", 2)  // 0.667
        println("Overlap score = "+overlapBiGram)

        // Smith Waterman Similarities
        val smithWaterman: Double = SmithWaterman.score("martha", "marhta", (LinearGap(gapValue = -1), Integer.MAX_VALUE))
        println("Smith Waterman Similarities = "+smithWaterman)
        val smithWatermanGotoh: Double = SmithWatermanGotoh.score("martha", "marhta", ConstantGap())
        println("Smith Waterman score = "+smithWatermanGotoh)

        // Tversky Similarity
        val tversky: Double = Tversky.score("karolin", "kathrin", 0.5)  // 0.333
        println("Tversky Similarity = "+tversky)

        // Phonetic Similarity
        val metaphone: Boolean = Metaphone.score("merci", "mercy")  // true
        println("metaphone = "+metaphone)
        val soundex: Boolean = Soundex.score("merci", "mercy")  // true
        println("soundex = "+soundex)*/
  }

}
