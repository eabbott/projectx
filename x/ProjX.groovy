package x
import groovy.swing.SwingBuilder

public class ProjX {

String scratchDirName = "scratch"
def assignment
def listScores
Aiu aiu
def files
def userFileNames
def fileToGrade
UserRow currentUser

def scoreGui
def swing
def userNameLabel
def fileChooser
def skipUserPanel
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
  fileChooser = swing.getVariable("fileChooser")
  skipUserPanel = swing.getVariable("skipUserPanel")
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
    userNameLabel.text = ""
    fileText.text = "Perhaps roll through scores here"
    def finishPanel = swing.button(text: "Post Grades", actionPerformed: {postGrades()})
    fileChooser.getComponent().removeAll()
    fileChooser.getComponent().add(finishPanel)
    finishPanel.setVisible(true)
    def blankPanel = swing.label("")
    skipUserPanel.getComponent().removeAll()
    skipUserPanel.getComponent().add(blankPanel)
    blankPanel.setVisible(true)
    scoreGui.show()
    return
  }

  userFileNames = getFileNames(files, currentUser.fname, currentUser.lname)
  userNameLabel.text = currentUser.fname +" "+ currentUser.lname
  assignment.clearScores()
  listScores.eachWithIndex { ScoreDefinition score, int i ->
    toggles[i].selected = false
  }
  fileText.text = ""
  fileToGrade = null

  // If 0 files, only allow skipping the user
  def newPanel
  if (!userFileNames) {
    newPanel = swing.label("Assignment not found")
  } else if (userFileNames.size() == 1) {
    fileToGrade = userFileNames[0]
    autoGrade()
    newPanel = swing.panel {
      label(getShortFileName(userFileNames[0]))
      button(text: "Record Grade", actionPerformed: {recordGrade()})
    }
  } else {
    def items = ["Select a file to grade"]
    items.addAll(userFileNames.collect{getShortFileName(it)})
    newPanel = swing.panel {
      comboBox(items: items,
        actionPerformed: {
          def name = it.getSource().getSelectedItem()
          fileToGrade = userFileNames.find{it.endsWith(name)}
          autoGrade()
        })
      button(text: "Record", actionPerformed: {recordGrade()})
    }
  }
  fileChooser.getComponent().removeAll()
  fileChooser.getComponent().add(newPanel)
  newPanel.setVisible(true)
  scoreGui.show()
}

  def recordGrade() {
    if (fileToGrade) {
      populateGuiWithNextUser(false)
    }
  }

  def autoGrade() {
    assignment.grade(fileToGrade)
    fileText.text = assignment.displayText
    listScores.eachWithIndex { ScoreDefinition score, int i ->
      toggles[i].selected = score.enabled
    }
  }

  def processCurrentUser() {
    currentUser?.setScore(assignment.maxScore - (listScores.findAll({it.enabled})*.score.sum()))
    currentUser?.setComments(listScores.findAll({it.enabled})*.text.join(" "))
    println "processing current user: "+ listScores
  }

  def postGrades() {
    println "we are finished, post those grades"
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
    File file = new File(scratchDirName + File.separator +
        it.name.split(File.separator == "\\" ?
	     File.separator + File.separator : File.separator)[0])
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

  def getShortFileName(String fullName) {
    //def fn = fullName.split(File.separator) -- split() bites it on \
    int i = fullName.lastIndexOf(File.separator)
    i != -1 ? fullName.substring(i+1) : fullName
  }

  def getShrunkenFileName(String fullName) {
    //def fn = fullName.split(File.separator) -- split() bites it on \
    int i = fullName.lastIndexOf(File.separator)
    String name = i == -1 ? "no file name" : fullName.substring(i+1)
    name.length() < 50 ? name : name.substring(0,20) +" ... "+
         name.substring(name.length() - 20, name.length())
  }

  def gradeSelectedAssignment(String label) {
    println("grade - "+ label)
  }
}
