package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment5 extends ScoringTemplate {
  String assignmentName = "Unit 5 Group Project"

  protected def loadScoreDefinitions() {
    maxScore = 200
    [
      new ScoreDefinition(200, "Prohibited sites were used, therefore no credit is given.", { checkForProhibitedSites() }),
      new ScoreDefinition(5, "There was no detailed mention of work teams.", { }),
      new ScoreDefinition(200, "You did not contribute to this group project, thus no credit is given.", { }),
      new ScoreDefinition(0, "Otherwise, your final group project shows a cohesive team effort. Thank you for your full submission.", { }, ScoreDefinition.ENJOYED),
      new ScoreDefinition(0, "Your final group project shows a cohesive effort. Thank you for your final submission.", { }, ScoreDefinition.PERFECT),
    ]
  }
}
