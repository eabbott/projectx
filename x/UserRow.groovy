package x
import com.gargoylesoftware.htmlunit.html.HtmlInput

class UserRow {
  HtmlInput scoreInput, commentInput
  String fname, lname
  String currentScore, currentComments

  public UserRow() { }
  public UserRow(String lastFirst) {
    def names = lastFirst?.split(",")
    if (names?.size() > 1) {
      lname = names[0].trim()
      fname = names[1].trim()
    }
  }

  void setScore(int score) {
    println ("setting score=$score")
    if (scoreInput) {
      scoreInput.setValueAttribute(String.valueOf(score))
    }
  }

  void setComments(String comments) {
    println ("setting comments=$comments")
    if (commentInput) {
      commentInput.setValueAttribute(comments)
    }
  }

  void setScoreInput(HtmlInput scoreInput) {
    this.scoreInput = scoreInput
    this.currentScore = scoreInput?.getValueAttribute()
  }

  void setCommentInput(HtmlInput commentInput) {
    this.commentInput = commentInput
    this.currentComments = commentInput?.getValueAttribute()
  }

  boolean isValid() {
    lname && fname && scoreInput && commentInput
  }

  boolean hasCurrentScore() {
    currentScore != null && currentScore != ""
  }

  String toString() {
    fname +" "+ lname +": "+ isValid() +"["+
        (currentScore ? currentScore : "") +"] "+
        (currentComments ? currentComments : "")
  }

  String scoreSummary() {
    fname +" "+ lname +": ["+
        (scoreInput ? scoreInput.getValueAttribute() : "") +"] "+
        (commentInput ? commentInput.getValueAttribute() : "")
  }
}
