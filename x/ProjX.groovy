package x
import groovy.swing.SwingBuilder

public class ProjX {

String scratchDirName = "scratch"
def assignment
def listScores
Aiu aiu
def files
UserRow currentUser

def scoreGui
def swing
def userNameLabel
def fileNameLabel
def fileChooser
def fileText
def toggles

public void main(String[] args) {

  if (args.length != 1) {
    println ("pass the assignment number in as an argument such as 'runfile.bat 1'")
    System.exit(0)
  }

  assignment = getAssignment(args[0])
  listScores = assignment.getScoreDefinitions()

  // This will download the file and leave the thinger open
  aiu = new Aiu(assignment: assignment.assignmentName)
  //aiu.downloadZip()

  files = processDownload(scratchDirName, aiu.zipFile)

  // setup UI
  scoreGui = new ScoreGui(this, listScores, assignment.assignmentName)
  swing = scoreGui.build()
  userNameLabel = swing.getVariable("userNameLabel")
  fileNameLabel = swing.getVariable("fileNameLabel")
  fileChooser = swing.getVariable("fileChooser")
  fileText = swing.getVariable("fileText")
  toggles = swing.getVariable("toggles")

  // Start cycle
  populateGuiWithNextUser(false)
}

public void populateGuiWithNextUser(boolean skipUser) {
  if (currentUser && !skipUser) {
    processCurrentUser()
  }
  currentUser = aiu.getNextUserRow()
  if (!currentUser) {
    // put in finish button or something
    userNameLabel.text = "finished"
    fileNameLabel.text = "finished"
    return
  }

  def userFiles = getFileNames(files, currentUser.fname, currentUser.lname)
  userNameLabel.text = currentUser.fname +" "+ currentUser.lname
  //fileNameLabel.text = userFiles ? userFiles.collect({def s=it.split(File.separator);s[s.size()-1]}).join(" ") : "NONE"
  def txt = userFiles ? userFiles.collect({def s=it.split(File.separator);s[s.size()-1]}).join(" ") : "NONE"
  //fileChooser.component = swing.panel { label(text: txt) }
  def newPanel = swing.label(text: txt)
  fileChooser.getComponent().removeAll()
  fileChooser.getComponent().add(newPanel)
  newPanel.setVisible(true)
  scoreGui.show()
  assignment.clearScores()
  if (userFiles) {
    assignment.grade(userFiles[0])
    fileText.text = assignment.displayText
  }
  listScores.eachWithIndex { ScoreDefinition score, int i ->
    toggles[i].selected = score.enabled
  }
}

def processCurrentUser() {
  currentUser?.setScore(assignment.maxScore - (listScores.findAll({it.enabled})*.score.sum()))
  currentUser?.setComments(listScores.findAll({it.enabled})*.text.join(" "))
  println "processing current user: "+ listScores
}

def getAssignment(String assign) {
  switch(assign) {
    case "1": return new Assignment1()
    case "2": return new Assignment2()
  }
  return null
}

def processDownload(String scratchDirName, String zipFileName) {
  def files = []
  def scratchDir = new File(scratchDirName)
  scratchDir.deleteDir()
  scratchDir.mkdir()
  def zipFile = new java.util.zip.ZipFile(new File(zipFileName))

  zipFile.entries().each {
    File file = new File(scratchDirName + File.separator + it.name.split(File.separator)[0])
    file.mkdir()
    file = new File(scratchDirName + File.separator + it.name)
    files << file.absolutePath
    new FileOutputStream(file).write(zipFile.getInputStream(it).getBytes())
  }
  files
}

  def getFileNames(List files, String fname, String lname) {
    files.findAll({ it.toLowerCase().indexOf(fname.toLowerCase()) > -1 }).
          findAll({ it.toLowerCase().indexOf(lname.toLowerCase()) > -1 })
  }

}
