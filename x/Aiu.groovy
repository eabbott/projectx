package x

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*

public class Aiu {

  String username = "scott.krawitz"
  String password = "aiu4free!"
  String loginUrl = "https://mycampus.aiu-online.com/Home/pages/login.aspx?ReturnUrl=/"
  def assignment
  String course
  String zipFile = "download.zip"
  boolean notOnGradingPage

  WebClient web
  HtmlPage page
  HtmlAnchor courseDesc, anchor

  HtmlPage loginPage
  HtmlForm form
  def tags

  def userRows
  int currentUserRow

  public UserRow getNextUserRow() {
    if (assignment.gradingLateAssignments) {
      while (currentUserRow < userRows?.size() && userRows[currentUserRow].hasNonZeroScore())
        { currentUserRow++ }
    }

    return currentUserRow < userRows?.size() ?
             userRows[currentUserRow++] : null
  }

  public void postAllGrades() {
    println "The grades are currently being posted, please wait patiently"
    form = page.getFormByName("aspnetForm")
    //form.getElementsByTagName("input").each { println it }
    //println "this thinger -> "+ (form.getElementsByTagName("input").find
    //             { it.getAttribute("value") == "Post Grades for Class" })
    page = (form.getElementsByTagName("input").find
                 { it.getAttribute("value") == "Post Grades for Class" }).click()
    println "The grades have been posted, shutting down."
    System.exit(0)
  }

  public void navigateToGradingPage() {
    Log.debug("navigatingToGradingPage")
    web = new WebClient()
    web.setThrowExceptionOnScriptError(false)
    web.setJavaScriptEnabled(false)

    login(web)
    page = login(web)
    Log.debug("logged in")

    page = gotoCoursePageFromMainPage(
        page.getFrameByName("MainFrame").getEnclosedPage())
    Log.debug("on course page")
    if (!page) {
      Log.debug("not on grading page")
      notOnGradingPage = true
      return
    }

    Log.debug("Posting grades by assignment")
    anchor = page.getAnchorByText("Post Grades by Assignment")
    page = anchor.click()
    form = page.getFormByName("aspnetForm")
    HtmlSelect select = form.getElementsByTagName("select")[0]
    HtmlOption option = select.getOptionByText(assignment.assignmentName)
    option.setSelected(true)
    page = select.setSelectedAttribute(option, true)
    web.setJavaScriptEnabled(true)
    page = (form.getElementsByTagName("input").find
                 { it.getAttribute("value") == "Select" }).click()
    //web.setJavaScriptEnabled(true)
  }

  public void downloadZip() {
    Log.debug("downloading zip")
    anchor = page.getAnchorByText("Download All Files")
    Log.debug("downloading zip -- anchor: "+ anchor.getClass().getName())
    Log.debug("downloading zip -- web.getJavaScriptEnabled="+ web.javaScriptEnabled)
    def downloadedPage = anchor.click()
    Log.debug("downloading zip -- saving: "+ downloadedPage.getClass().getName())
    //downloadedPage.save(new File("gonnaDownload.html"))
    new FileOutputStream(zipFile).write(downloadedPage.getInputStream().getBytes())
  }

  public void loadUserRows() {
    userRows = []
    currentUserRow = 0
    println "Starting to load UserRow data"
    //page.save(new File("grades.html"))
    def rows = page.getByXPath("//table[@class='table2']/tbody/tr")
    rows.each { HtmlTableRow row ->
      // test if this is a user row
      def cells = row.getCells()
      def inputs = cells?.size() > 3 ? cells[3]?.getElementsByTagName("input") : null
      if (inputs) {
        UserRow userRow = new UserRow(row.getCell(0).getTextContent())
        // 4 is grade, 5 is comment
        userRow.scoreInput = inputs[0]
        userRow.commentInput = row.getCell(4).getElementsByTagName("input")[0]
        if (userRow.isValid()) {
          userRows << userRow
        }
      }
    }

    //web.closeAllWindows()
  }

  public HtmlPage login(WebClient web) {
    Log.debug("logging in to site")
    loginPage = web.getPage(loginUrl)
    form = loginPage.getFormByName("aspnetForm")
    def tags = form.getElementsByTagName("input")
    getLoginImage(tags, username, password).click()
  }

  def getLoginImage(def tags, def username, def password) {
    HtmlElement loginImage
    tags?.each { HtmlElement tag ->
      if (tag.getAttribute("name").endsWith("UserNameTextBox")) {
        tag.setValueAttribute(username)
      }
      if (tag.getAttribute("name").endsWith("PasswordTextBox")) {
        tag.setValueAttribute(password)
      }
      if (tag.getAttribute("name").endsWith("LoginImageButton")) {
        loginImage = tag
      }
    }
    return loginImage
  }

  HtmlPage gotoCoursePageFromMainPage(HtmlPage mainPage) {
    Log.debug("gotoCoursePageFromMainPage")
    def courses = []
    tags = mainPage.getAnchors()
    HtmlElement courseLink
    tags?.each { HtmlElement tag ->
      // roll through the tags, finding the link for this course
      if (tag.getAttribute("id").endsWith("_CodeHL")) {
        courses << tag.getTextContent()
        if (course.equals(tag.getTextContent())) {
          courseLink = tag
        }
      }
    }
    if (!courseLink) {
      println("The course requested ("+ course +") could not be found.")
      println("The courses found were as follows: "+ courses)
    }
    courseLink?.click()
  }
}
