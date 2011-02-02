package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment1 extends ScoringTemplate {
  String assignmentName = "Unit 1 Individual Project"

  protected def loadScoreDefinitions() {
    maxScore = 125
    prohibitedSites = ["wikipedia.com", "wikipedia.org"]
    [
    new ScoreDefinition(5, "There was no mention of elections", { containsWord(["election", "elections"]) }),
    new ScoreDefinition(5, "The required memo template was not used", { containsWord("memo") }),
    new ScoreDefinition(5, "There was no abstract or APA reference.", { !references }),
    new ScoreDefinition(5, "References were not in proper APA format, ie, alphabetized.", { references && !referencesSorted() }),
    new ScoreDefinition(5, "Content was far too brief.", { contentTooBrief(600) }),
    new ScoreDefinition(5, "Paper did not meet 3-5 pages guidelines (not including cover or reference pages).", { contentTooLong(1300) }),
    new ScoreDefinition(125, "Prohibited sites were used, therefore no credit is given.", { checkForProhibitedSites() }),
    new ScoreDefinition(10, "There are many misspellings or grammatical errors.", { checkSpelling() }),
    new ScoreDefinition(0, "Otherwise, the content of your first individual assignment was interesting to read.", { }),
    new ScoreDefinition(0, "Your first individual assignment was well done and I enjoyed reading it.", { }),
    ]
  }
}
