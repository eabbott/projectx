package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment4 extends ScoringTemplate {
  String assignmentName = "Unit 4 Individual Project"

  protected def loadScoreDefinitions() {
    [
      new ScoreDefinition(5, "Paper did not meet 3-5 pages guidelines, not including cover or reference pages (-5 points).", { contentTooLong() }),
      new ScoreDefinition(5, "Please provide analysis of benefits and disadvantages in greater depth (-5 points).", { }),
      new ScoreDefinition(0, "Otherwise, the content of your fourth individual assignment was interesting to read.", { }, ScoreDefinition.ENJOYED),
      new ScoreDefinition(0, "Your fourth individual project shows your comprehension of the material at hand...good work!.", { }, ScoreDefinition.PERFECT),
    ]
  }
}
