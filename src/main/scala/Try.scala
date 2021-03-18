import com.rockymadden.stringmetric._
import com.rockymadden.stringmetric.phonetic._
import com.rockymadden.stringmetric.similarity._

object Try {
  def main(args: Array[String]): Unit = {
    //    Dice / Sorensen Metric:
    println("Dice / Sorensen Metric:")
    println(DiceSorensenMetric(1).compare("night", "nacht")) // 0.6
    println(DiceSorensenMetric(1).compare("context", "contact")) // 0.7142857142857143
    //      Hamming Metric:
    println("Hamming Metric:")
    println(HammingMetric.compare("toned", "roses")) // 3
    println(HammingMetric.compare("1011101", "1001001")) // 2
    //    Jaccard Metric:
    println("Jaccard Metric:")
    println(JaccardMetric(1).compare("night", "nacht")) // 0.3
    println(JaccardMetric(1).compare("context", "contact")) // 0.35714285714285715
    //      Jaro Metric:
    println("Jaro Metric:")
    println(JaroMetric.compare("dwayne", "duane")) // 0.8222222222222223
    println(JaroMetric.compare("jones", "johnson")) // 0.7904761904761904
    println(JaroMetric.compare("fvie", "ten")) // 0.0
    //    Jaro-Winkler Metric:
    println("Jaro-Winkler Metric:")
    println(JaroWinklerMetric.compare("dwayne", "duane")) // 0.8400000000000001
    println(JaroWinklerMetric.compare("jones", "johnson")) // 0.8323809523809523
    println(JaroWinklerMetric.compare("fvie", "ten")) // 0.0
    //    Levenshtein Metric:
    println("Levenshtein Metric:")
    println(LevenshteinMetric.compare("sitting", "kitten")) // 3
    println(LevenshteinMetric.compare("cake", "drake")) // 2
    //    N-Gram Metric:
    println("N-Gram Metric:")
    println(NGramMetric(1).compare("night", "nacht")) // 0.6
    println(NGramMetric(2).compare("night", "nacht")) // 0.25
    println(NGramMetric(2).compare("context", "contact")) // 0.5
    //      Overlap Metric:
    println("Overlap Metric:")
    println(OverlapMetric(1).compare("night", "nacht")) // 0.6
    println(OverlapMetric(1).compare("context", "contact")) // 0.7142857142857143
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
    //    Soundex Metric:
    println("Soundex Metric:")
    println(SoundexMetric.compare("robert", "rupert")) // true
    println(SoundexMetric.compare("robert", "rubin")) // false
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
