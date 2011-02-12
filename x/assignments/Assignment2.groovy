package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment2 extends ScoringTemplate {
  String assignmentName = "Unit 2 Individual Project"

  protected def loadScoreDefinitions() {
    [
      new ScoreDefinition(0, "Otherwise, the content of your second individual assignment was interesting to read.", { }, ScoreDefinition.ENJOYED),
      new ScoreDefinition(0, "Your second individual assignment was well done and I enjoyed reading it.", { }, ScoreDefinition.PERFECT),
    ]
  }
}
