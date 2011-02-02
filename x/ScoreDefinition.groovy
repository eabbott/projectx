package x

class ScoreDefinition extends Observable {
  def text, score
  boolean enabled
  Closure action

  public ScoreDefinition(def score, def text, Closure action) {
    this.score = score
    this.text = text
    this.action = action
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
