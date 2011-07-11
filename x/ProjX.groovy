package x
import groovy.swing.SwingBuilder
import java.util.zip.ZipEntry
import x.assignments.*

public class ProjX {
def lineSeparator = System.getProperty("line.separator")

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
def assignments = ["Assignment1","Assignment2","Assignment4","Assignment5"]
def lastCourseChosen

public void main(String[] args) {
  // ./launch.sh -g Assignment4 tscratch/Guy_Tammy/Tammy.guyUnit4IP.docx
  if (args && args.length == 3 && args[0] == "-g") {
    assignment = chooseAssignment(args[1])
    listScores = assignment.getScoreDefinitions()
    assignment.grade(args[2])
    listScores.each { println it }
    return
  // ./launch.sh -unzip tscratch ~/Downloads/Assignments.zip
  } else if (args && args.length == 3 && args[0] == "-unzip") {
    files = processDownload(args[1], args[2])
    files.each { println it }
    return
  }

  lastCourseChosen = loadLastCourseChosen()
  argsGui = new ArgsGui(this, lastCourseChosen, assignments)
  swing = argsGui.build()
  argsGui.show()
}

def continueWithArguments() {
  Log.debug("continuingWithArguments")
  course = swing.getVariable("course")?.text
  recordCourseIfDifferent(course, lastCourseChosen)
  def assignmentString = swing.getVariable("assignmentChooser")?.selectedItem
  def gradingLateAssignments = swing.getVariable("late")?.selected
  argsGui.close()

  assignment = chooseAssignment(assignmentString)
  listScores = assignment.getScoreDefinitions()
  assignment.gradingLateAssignments = gradingLateAssignments

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
  Log.debug "downloading zip file"
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

  // find a user with files to grade
  while ((currentUser=aiu.getNextUserRow()) != null &&
         !(userFileNames=getFileNames(files, currentUser.fname, currentUser.lname)))
  {
    currentUser?.setScore(ScoringTemplate.NOT_SUBMITTED_SCORE)
    currentUser?.setComments(ScoringTemplate.NOT_SUBMITTED_COMMENTS)
  }

  if (!currentUser) {
    // put in finish button or something
    userNameLabel.text = ""

    // show the scores
    StringBuffer sb = new StringBuffer(16*1024)
    aiu.userRows?.each {
      sb.append(it.scoreSummary()).append(lineSeparator)
    }
    fileText.text = sb.toString()
    writeScoresToDisk(fileText.text)

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

  zipFile.entries().findAll { !it.isDirectory() }.each { ZipEntry entry ->
    mkdirIfDoesntExist(scratchDirName, entry.name.split("/")[0])
    File file = new File(scratchDirName + File.separator + entry.name)
    println "doing entry: "+ entry
    try {
      new FileOutputStream(file).write(zipFile.getInputStream(entry).getBytes())
      files << file.absolutePath
    } catch (Exception e) {
      // filenames with non-english characters can barf
      String err = "File could not be pulled out of zipfile, there is a jdk bug. "+
           "Please open the file "+ file.absolutePath +" and grade accordingly."
      new FileOutputStream(file).write(err.getBytes())
      files << file.absolutePath
      println err
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

  def writeScoresToDisk(String scoreSummary) {
    def file = new File(course +"-"+ assignment.assignmentName +".txt")
    if (file.exists()) { file.delete() }
    println "For backup purposes writing the grades to file: "+ file.name
    new FileOutputStream(file).write(scoreSummary.bytes)
  }
}
