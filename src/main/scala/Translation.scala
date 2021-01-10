import org.apache.spark.rdd.RDD
import zhmyh.yandex.api.translate.{Language, Translate}

object Translation extends Serializable {
  val tr = new Translate("trnsl.1.1.20190411T144635Z.594931b10ad4385a.829ff83b6878ab076f769297aea1e725f168f30a")

  /**
    * Translate a sentence from any language to English.*/
  def yandexTranslationToEnglish(sentence:String, languageTag:String):String={
    var result = ""
      if (languageTag == "de")
        result= tr.translate(sentence, Language.GERMAN, Language.ENGLISH).get
      else if (languageTag == "ar")
        result= tr.translate(sentence, Language.ARABIC, Language.ENGLISH).get
      else if (languageTag == "fr")
        result= tr.translate(sentence, Language.FRENCH, Language.ENGLISH).get
//      else if (langaugeTag == "zh")
//        result= tr.translate(sentence, Language.Chinese, Language.ENGLISH).get
    result
  }


  /**
    * Translate a sentence from English to German.*/
  def yandexTranslationToGerman(sentence:String):String={
    var result = tr.translate(sentence, Language.ENGLISH, Language.GERMAN).get
    result
  }

  /**
    * Translate a sentence from English to Arabic.*/
  def yandexTranslationToArabic(sentence:String):String={
    var result = tr.translate(sentence, Language.ENGLISH, Language.ARABIC).get
    result
  }

  /**
    * Translate a sentence from English to French.*/
  def yandexTranslationToFrench(sentence:String):String={
    var result = tr.translate(sentence, Language.ENGLISH, Language.FRENCH).get
    result
  }



  /**
    * Detect a language of a sentence.*/
  def languageDetection(sentence:String)={
    val lan = tr.detect(sentence)
    println("The detected language is: "+lan.get.toString)
  }


  /**
    * Translate classes and relations to English.*/
  def translateToEnglish(Oclasses: RDD[String], Orelations: RDD[String], langaugeTag:String)={
    val classesWithTranslation = Oclasses.map(x => (x,this.yandexTranslationToEnglish(x, langaugeTag)))
    println("=====================")
    println("Translated classes:")
    println("=====================")
    classesWithTranslation.foreach(println(_))
//    classesWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")
    classesWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")

    val RelationsWithTranslation: RDD[(String, String)] = Orelations.map(x => (x,this.yandexTranslationToEnglish(x, langaugeTag)))
    println("=====================")
    println("Translated relations:")
    println("=====================")
    RelationsWithTranslation.foreach(println(_))
//    RelationsWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
    RelationsWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
  }


  /**
    * Translate classes and relations to German.*/
  def translateToGerman(Oclasses: RDD[String], Orelations: RDD[String])={
    val classesWithTranslation = Oclasses.map(x => (x,this.yandexTranslationToGerman(x)))
    println("=====================")
    println("Translated classes:")
    println("=====================")
    classesWithTranslation.foreach(println(_))
    //    classesWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")
    classesWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")

    val RelationsWithTranslation: RDD[(String, String)] = Orelations.map(x => (x,this.yandexTranslationToGerman(x)))
    println("=====================")
    println("Translated relations:")
    println("=====================")
    RelationsWithTranslation.foreach(println(_))
    //    RelationsWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
    RelationsWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
  }

  /**
    * Translate classes and relations to Arabic.*/
  def translateToArabic(Oclasses: RDD[String], Orelations: RDD[String])={
    val classesWithTranslation = Oclasses.map(x => (x,this.yandexTranslationToArabic(x)))
    println("=====================")
    println("Translated classes:")
    println("=====================")
    classesWithTranslation.foreach(println(_))
    //    classesWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")
    classesWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")

    val RelationsWithTranslation: RDD[(String, String)] = Orelations.map(x => (x,this.yandexTranslationToArabic(x)))
    println("=====================")
    println("Translated relations:")
    println("=====================")
    RelationsWithTranslation.foreach(println(_))
    //    RelationsWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
    RelationsWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
  }

  /**
    * Translate classes and relations to French.*/
  def translateToFrench(Oclasses: RDD[String], Orelations: RDD[String])={
    val classesWithTranslation = Oclasses.map(x => (x,this.yandexTranslationToFrench(x)))
    println("=====================")
    println("Translated classes:")
    println("=====================")
    classesWithTranslation.foreach(println(_))
    //    classesWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")
    classesWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/classesWithTranslation")

    val RelationsWithTranslation: RDD[(String, String)] = Orelations.map(x => (x,this.yandexTranslationToFrench(x)))
    println("=====================")
    println("Translated relations:")
    println("=====================")
    RelationsWithTranslation.foreach(println(_))
    //    RelationsWithTranslation.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
    RelationsWithTranslation.map{case(a, b) =>
      var line = a.toString + "," + b.toString
      line
    }.coalesce(1, shuffle = true).saveAsTextFile("src/main/resources/OfflineDictionaries/Translations/RelationsWithTranslation")
  }
}
