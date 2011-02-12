package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment2 extends ScoringTemplate {
  String assignmentName = "Unit 2 Individual Project"

  protected def loadScoreDefinitions() {
    [
      new ScoreDefinition(5, "Paper did not meet 3-5 pages guidelines, not including cover or reference pages (-5 points).", { contentTooLong() }),
      new ScoreDefinition(0, "Otherwise, the content of your second individual assignment was interesting to read.", { }, ScoreDefinition.ENJOYED),
      new ScoreDefinition(0, "Your second individual assignment was well done and I enjoyed reading it.", { }, ScoreDefinition.PERFECT),
    ]
  }
}
