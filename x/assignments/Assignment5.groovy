package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment5 extends ScoringTemplate {
  String assignmentName = "Unit 5 Group Project"

  protected def loadScoreDefinitions() {
    maxScore = 200
    prohibitedSites = ["wikipedia.com", "wikipedia.org"]
    [
    new ScoreDefinition(5, "There was no abstract or APA reference.", { !references }),
    new ScoreDefinition(5, "References were not in proper APA format, ie, alphabetized.", { references && !referencesSorted() }),
    new ScoreDefinition(5, "Paper did not meet 5-8 pages guidelines (not including cover or reference pages).", { contentTooLong(2000) }),
    new ScoreDefinition(10, "There are many misspellings or grammatical errors.", { checkSpelling() }),
    new ScoreDefinition(5, "Content was far too brief.", { contentTooBrief(1100) }),
    new ScoreDefinition(200, "Prohibited sites were used, therefore no credit is given.", { checkForProhibitedSites() }),
    new ScoreDefinition(5, "There was no detailed mention of work teams.", { }),
    new ScoreDefinition(200, "You did not contribute to this group project, thus no credit is given.", { }),
    new ScoreDefinition(0, "Otherwise, your final group project shows a cohesive team effort. Thank you for your full submission.", { }),
    new ScoreDefinition(0, "Your final group project shows a cohesive effort. Thank you for your final submission.", { }),
    ]
  }
}
