package com.itri.corenlp

import edu.stanford.nlp.simple._
import edu.stanford.nlp.ie.machinereading.structure.Span
import scala.collection.JavaConverters._
object CoreNLPexample {

  def main(args: Array[String]): Unit = {
    val sent = new Sentence("Lucy is in the sky with diamonds.")
    val nerTags = sent.nerTags()

    val doc = new Document("add your text here! It can contain multiple sentences.")
    val sentences1 = doc.sentences.asScala.toList

    for (sent1 <- sentences1) { // Will iterate over two sentences
      // We're only asking for words -- no need to load any models yet
      System.out.println("The second word of the sentence '" + sent1 + "' is " + sent1.word(1))
      // When we ask for the lemma, it will load and run the part of speech tagger
      System.out.println("The third lemma of the sentence '" + sent1 + "' is " + sent1.lemma(2))
      // When we ask for the parse, it will load and run the parser
      System.out.println("The parse of the sentence '" + sent1 + "' is " + sent1.parse)
    }
      val sent2 = new Sentence("your text should go here")
      sent2.algorithms.headOfSpan(new Span(0, 2)) // Should return 1


  }

}
