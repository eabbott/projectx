package x

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlForm
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTextInput

public class Aiu {

  String username = "scott.krawitz"
  String password = "aiu4free!"
  String loginUrl = "https://mycampus.aiu-online.com/Home/pages/login.aspx?ReturnUrl=/"
  String assignment
  String zipFile = "download.zip"

  WebClient web
  HtmlPage page
  HtmlAnchor courseDesc, anchor

  HtmlPage loginPage
  HtmlForm form
  def tags

  Iterator rows

  public UserRow getNextUserRow() {
    if (rows == null) {
  rows = [new UserRow(fname: "Abel", lname: "Alvarez"),
          new UserRow(fname: "Figglestein", lname: "Trogdar"),
          new UserRow(fname: "Tarver", lname: "Tawnee")].iterator()
    }
    rows.hasNext() ? rows.next() : null
  }

  public void downloadZip() {

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

    anchor = page.getAnchorByText("Download All Files")
    def downloadedPage = anchor.click()
    new FileOutputStream(zipFile).write(downloadedPage.getInputStream().getBytes())
  }

  public void rollingThroughUsers() {
    println "Printing stuff"
    def rows = page.getByXPath("//table[@class='table2']/tbody/tr")
    //def rows = page.getByXPath("//table[@class='table2']/tbody/tr/td[@align='left']")
    rows.each { println "class="+ it.getClass().name +", rows=$it" }

    // 1) Download the file
    // 2) Score it, hash of user to score
    // 3) Roll through users, setting score + comments
    // 4) submit page

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
      if (tag.getAttribute("id").endsWith("_CodeHL")) {
        courseLink = tag
      }
    }
    courseLink?.click()
  }

  public List testingstuff() {
    WebClient web = new WebClient()
    HtmlPage loginPage = web.getPage("http://www.wired.com")
    println "page="+ loginPage.toString()
    HtmlForm form = loginPage.getFormByName("search")
    def tags = form.getElementsByTagName("input")
    tags?.each {
      println "tag $it.nodeName: "+ it.getAttribute("name") + it.toString()
    }

    HtmlTextInput textField = form.getInputByName("query");

    // Change the value of the text field
    textField.setValueAttribute("fish legs");

    def submit = form.getElementById("gs_submit");
    HtmlPage page2 = submit.click();
    println "page2 = "+ page2.toString()

    web.closeAllWindows()
    []
  }

}
