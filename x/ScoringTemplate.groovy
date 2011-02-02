package x
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.swabunga.spell.event.SpellChecker
import com.swabunga.spell.engine.SpellDictionaryHashMap



public abstract class ScoringTemplate {
  static SpellChecker spellChecker = new SpellChecker()
  static {
    //InputStream is = getClass().getResourceAsStream("dict/english.0");
    //spellChecker.addDictionary(new SpellDictionaryHashMap(new InputStreamReader(is)));
    spellChecker.addDictionary(new SpellDictionaryHashMap(new File("dict/english.0")))
  }

  // This field is not re-initialized
  List<ScoreDefinition> scoreDefinitions
  protected abstract loadScoreDefinitions()
  public ScoringTemplate() { scoreDefinitions = loadScoreDefinitions() }

  // These are transient and re-initialized for each user/paper
  int maxScore
  String text
  String displayText
  List paragraphs
  List references
  List prohibitedSites
  boolean invalidFile

  public void grade(String filename) {
    initialize()
    if (filename.endsWith("docx")) parseXml(filename)
    else if (filename.endsWith(".doc")) parseDoc(filename)
    else {
      println ("Invalid file format: $filename.  Skipping")
      invalidFile = true
      return
    }

    // some debugging, get rid of this eventually
    //println "par.len="+ paragraphs.size()
    //println "ref.len="+ references.size()

    // run the scoring
    scoreDefinitions.each { it.runAction() }
  }

  public void clearScores() {
    scoreDefinitions*.clear()
  }

  private void initialize() {
    text = null
    displayText = null
    paragraphs = []
    references = []
    invalidFile = false
  }

  public boolean referencesSorted() {
    references == references.sort()
  }

  public boolean checkForProhibitedSites() {
    return findIndexOf(references.join(" ").toLowerCase(), prohibitedSites)
  }

  public boolean contentTooBrief(int minWords) {
    int words = text.split().size() - 30 // subtract 30 for title page
    words < minWords
  }

  public boolean contentTooLong(int maxWords) {
    int words = text.split().size() - 30 // subtract 30 for title page
    words > maxWords
  }

  public boolean containsWord(String word)
    { containsWord([word]) }

  public boolean containsWord(List words) {
    findMatch(text, words)
  }

  public boolean checkSpelling() {
    int badWords = 0
    text.split().each { it
      if (!spellChecker.isCorrect(modifyWord(it.trim()))) {
        //println "misspelled word: "+ it
        badWords++
      }
    }
    badWords > 10
  }

  protected String modifyWord(String word) {
    if (word == null || word.length() == 0 || word.equals("a") ||
        Character.isUpperCase(word.charAt(0)) || word.indexOf("(") != -1 ||
        word.indexOf(")") != -1 || word.indexOf("\"") != -1 ||
        word.indexOf("-") != -1 ||
        Character.isDigit(word.charAt(0)) ) {
      return "the"
    } else if ( (word.endsWith(",") || word.endsWith(".") ||
                 word.endsWith(";") || word.endsWith(":")) &&
                word.length() > 2 &&
                (word.substring(0, word.length()-1)).indexOf(".") == -1) {
      return word.substring(0, word.length()-1)
    } else {
      return word
    }
  }

  def parseXml(String filename) {
    XWPFDocument doc = new XWPFDocument(new FileInputStream(filename));
    boolean inReferences = false
    StringBuffer sb = new StringBuffer(32*1024)
    StringBuffer dsb = new StringBuffer(32*1024)
    doc.getParagraphs().each {
      dsb.append(it.getParagraphText()).append("\n")
      if (isStartOfReferences(it.getParagraphText()?.trim())) {
        inReferences = true
      } else if (inReferences) {
        references << it.getParagraphText()?.trim()
      } else {
        paragraphs << it.getParagraphText()
        sb.append(" ").append(it.getParagraphText()?.trim())
      }
    }
    displayText = dsb.toString()
    text = sb.toString()
  }

  def parseDoc(String filename) {
    def fs = new POIFSFileSystem(new FileInputStream(filename));
    HWPFDocument doc = new HWPFDocument(fs);
    displayText = doc.getRange().text()
    org.apache.poi.hwpf.usermodel.Range range = doc.getRange();

    boolean inReferences = false
    StringBuffer sb = new StringBuffer(32*1024)
    for (int i=0; i < range.numParagraphs(); i++) {
      String text = range.getParagraph(i).text()
      if (isStartOfReferences(text?.trim())) {
        inReferences = true
      } else if (inReferences) {
        references << text?.trim()
      } else {
        paragraphs << text
        sb.append(" ").append(text?.trim())
      }
    }
    text = sb.toString()
  }

  private boolean findMatch(String text, List words) {
    def val = text.split().find() { String word ->
      return words.find() { it.equalsIgnoreCase(word) }
    }
    return val != null
  }

  private boolean findIndexOf(String text, List words) {
    def val = text.split().find() { String word ->
      return words.find() { word.indexOf(it) != -1 }
    }
    return val != null
  }

  boolean isStartOfReferences(String text) {
    text?.equalsIgnoreCase("references") || text?.equalsIgnoreCase("references:")
  }
}
