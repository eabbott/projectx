package x

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*

public class Aiu {

  String username = "scott.krawitz"
  String password = "aiu4free!"
  String loginUrl = "https://mycampus.aiu-online.com/Home/pages/login.aspx?ReturnUrl=/"
  String assignment
  String course
  String zipFile = "download.zip"

  WebClient web
  HtmlPage page
  HtmlAnchor courseDesc, anchor

  HtmlPage loginPage
  HtmlForm form
  def tags

  def userRows
  int currentUserRow

  public UserRow getNextUserRow() {
    return currentUserRow < userRows?.size() ?
               userRows[currentUserRow++] : null
    /*
    userRows = [new UserRow(fname: "Abel", lname: "Alvarez"),
          new UserRow(fname: "Figglestein", lname: "Trogdar"),
          new UserRow(fname: "Tarver", lname: "Tawnee")].iterator()
    rows.hasNext() ? rows.next() : null
    */
  }

  public void postAllGrades() {
    println "we are finished. not yet auto-posting grades though "
  }

  public void navigateToGradingPage() {
    web = new WebClient()
    web.setThrowExceptionOnScriptError(false)

    login(web)
    page = login(web)

    page = gotoCoursePageFromMainPage(
        page.getFrameByName("MainFrame").getEnclosedPage())

    anchor = page.getAnchorByText("Post Grades by Assignment")
    page = anchor.click()
    form = page.getFormByName("aspnetForm")
    HtmlSelect select = form.getElementsByTagName("select")[0]
    HtmlOption option = select.getOptionByText(assignment)
    option.setSelected(true)
    page = select.setSelectedAttribute(option, true)
    page = (form.getElementsByTagName("input").find
                 { it.getAttribute("value") == "Select" }).click()
  }

  public void downloadZip() {
    anchor = page.getAnchorByText("Download All Files")
    def downloadedPage = anchor.click()
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
    tags = mainPage.getAnchors()
    HtmlElement courseLink
    tags?.each { HtmlElement tag ->
      // roll through the tags, finding the link for this course
      if (tag.getAttribute("id").endsWith("_CodeHL") &&
              course.equals(tag.getTextContent())) {
        courseLink = tag
      }
    }
    courseLink?.click()
  }
}
