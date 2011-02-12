package x

class ScoreDefinition extends Observable {
  public static final String LATE = "late", ENJOYED = "enjoyed", PERFECT = "perfect"
  def text, score
  def scoreType
  boolean enabled
  Closure action

  public ScoreDefinition(def score, def text, Closure action, String scoreType = null) {
    this.score = score
    this.text = text
    this.action = action
    this.scoreType = scoreType
  }

  String toString() {
    "score="+ score +", enabled="+ enabled +", text="+ text
  }

  public void runAction() {
    enabled = action.call()
  }

  public void clear() {
    enabled = false
  }
}
