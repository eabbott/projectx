package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment4 extends ScoringTemplate {
  String assignmentName = "Unit 4 Individual Project"

  protected def loadScoreDefinitions() {
    maxScore = 125
    prohibitedSites = ["wikipedia.com", "wikipedia.org"]
    [
    new ScoreDefinition(5, "There was no APA cover page (-5 points).", { }),
    new ScoreDefinition(5, "There was no APA reference page (-5 points).", { !references }),
    new ScoreDefinition(5, "References were not in proper APA format, ie, alphabetized (-5 points).", { references && !referencesSorted() }),
    new ScoreDefinition(5, "Please provide analysis of benefits and disadvantages in greater depth (-5 points).", { }),
    new ScoreDefinition(5, "Paper did not meet 3-5 pages guidelines, not including cover or reference pages (-5 points).", { contentTooLong(1300) }),
    new ScoreDefinition(10, "There are many misspellings or grammatical errors (-10 points).", { checkSpelling() }),
    new ScoreDefinition(5, "Content was far too brief (-5 points).", { contentTooBrief(600) }),
    new ScoreDefinition(125, "Prohibited sites were used, therefore no credit is given.", { checkForProhibitedSites() }),
    new ScoreDefinition(5, "Paper was submitted late (-5 points).", { }),
    new ScoreDefinition(0, "That said, the content was enjoyable to read.", { }),
    new ScoreDefinition(0, "Your fourth individual project shows your comprehension of the material at hand...good work!.", { }),
    ]
  }
}
