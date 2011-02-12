package x.assignments
import x.ScoreDefinition
import x.ScoringTemplate

public class Assignment1 extends ScoringTemplate {
  String assignmentName = "Unit 1 Individual Project"

  protected def loadScoreDefinitions() {
    [
      new ScoreDefinition(5, "Paper did not meet 3-5 pages guidelines, not including cover or reference pages (-5 points).", { contentTooLong() }),
      new ScoreDefinition(5, "There was no mention of elections (-5 points).", { containsWord(["election", "elections"]) }),
      new ScoreDefinition(5, "The required memo template was not used (-5 points).", { containsWord("memo") }),
      new ScoreDefinition(0, "Otherwise, the content of your first individual assignment was interesting to read.", { }, ScoreDefinition.ENJOYED),
      new ScoreDefinition(0, "Your first individual assignment was well done and I enjoyed reading it.", { }, ScoreDefinition.PERFECT),
    ]
  }
}
