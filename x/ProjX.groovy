package x
import groovy.swing.SwingBuilder
import java.util.zip.ZipEntry
import x.assignments.*

public class ProjX {

String scratchDirName = "scratch"
def assignment, course
def listScores
Aiu aiu
def files
def userFileNames
def fileToGrade
UserRow currentUser

def scoreGui
def argsGui
def swing
def userNameLabel
def fileChooser
def skipUserPanel
def fileText
def toggles
def assignments = ["Assignment1","Assignment2","Assignment3","Assignment4","Assignment5"]
def lastCourseChosen

public void main(String[] args) {

  lastCourseChosen = loadLastCourseChosen()
  argsGui = new ArgsGui(this, lastCourseChosen, assignments)
  swing = argsGui.build()
  argsGui.show()
}

def continueWithArguments() {
  course = swing.getVariable("course")?.text
  recordCourseIfDifferent(course, lastCourseChosen)
  def assignmentString = swing.getVariable("assignmentChooser")?.selectedItem
  def gradingLateAssignments = swing.getVariable("late")?.selected
  argsGui.close()

  assignment = chooseAssignment(assignmentString)
  listScores = assignment.getScoreDefinitions()
  assignment.gradingLateAssignments = gradingLateAssignments

  //assignment.grade("scratch/Jordan_Diane/International Business.docx")
  //listScores.each { println it }
  //if (true) return

  // This will download the file and leave the thinger open
  aiu = new Aiu(course: course, assignment: assignment)
  aiu.navigateToGradingPage()
  if (aiu.notOnGradingPage) {
    System.exit(0)
  }
  aiu.loadUserRows()

//  aiu.userRows?.each {
//    println(it.scoreSummary())
//  }
  aiu.downloadZip()

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

    // show the scores
    StringBuffer sb = new StringBuffer(16*1024)
    aiu.userRows?.each {
      sb.append(it.scoreSummary()).append("\n")
    }
    fileText.text = sb.toString()

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
    currentUser?.setScore(assignment.maxScore - assignment.getPointsOff(listScores))
    currentUser?.setComments(listScores.findAll({it.enabled})*.text.join(" "))
  }

  def postGrades() {
    aiu.postAllGrades()
  }

def processDownload(String scratchDirName, String zipFileName) {
  def files = []
  def scratchDir = new File(scratchDirName)
  if (scratchDir.exists()) {
    scratchDir.deleteDir()
  }
  scratchDir.mkdir()
  def zipFile = new java.util.zip.ZipFile(new File(zipFileName))

  zipFile.entries().each { ZipEntry entry ->
    if (entry.isDirectory()) {
      mkdirIfDoesntExist(scratchDirName, entry.name)
    } else {
      mkdirIfDoesntExist(scratchDirName, entry.name.split("/")[0])
      File file = new File(scratchDirName + File.separator + entry.name)
      files << file.absolutePath
      new FileOutputStream(file).write(zipFile.getInputStream(entry).getBytes())
    }
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

  def mkdirIfDoesntExist(String scratchDirName, String name) {
    File dir = new File(scratchDirName + File.separator + name)
    if (!dir.exists()) {
      dir.mkdir()
    }
  }

  def loadLastCourseChosen() {
    def file = new File("lastChosenCourse.txt")
    return file.exists() ? file.text : "Enter the course"
  }

  def recordCourseIfDifferent(String course, String lastCourseChosen) {
    if (course != lastCourseChosen) {
      def file = new File("lastChosenCourse.txt")
      if (file.exists()) { file.delete() }
      new FileOutputStream(file).write(course.bytes)
    }
  }

  def chooseAssignment(String assign) {
    // have to compile for this to work
    //assignment = Class.forName(args[1]).newInstance()
    // can't do this one either...class is diff classloader and won't work in java
    //assignment = Eval.me("new x.assignments."+ args[1] +"()")
    switch (assign) {
      case "Assignment1": return new Assignment1()
      case "Assignment2": return new Assignment2()
      //case "Assignment3": return new Assignment3()
      case "Assignment4": return new Assignment4()
      case "Assignment5": return new Assignment5()
    }
    return null
  }
}
