package x

import groovy.swing.SwingBuilder
import groovy.beans.Bindable
import javax.swing.*
import java.awt.*
import x.*

public class ScoreGui {
def listScores
String assignmentName
ProjX projX
def mainFrame
def scoreFrame

public ScoreGui(ProjX projX, def listScores, String assignmentName) {
  this.projX = projX
  this.listScores = listScores
  this.assignmentName = assignmentName
}

public void show() {
  mainFrame.show()
  scoreFrame.show()
  SwingUtilities.updateComponentTreeUI(mainFrame)
}

public SwingBuilder build() {
  def swing = new SwingBuilder()
  swing.setVariable("listScores", listScores)
  swing.setVariable("assignmentName", assignmentName)
  def toggles = []
  swing.setVariable("toggles", toggles)

  mainFrame = swing.frame(title:'Project X', size: [900, 600],
      defaultCloseOperation:JFrame.EXIT_ON_CLOSE, pack:true, show:true) {
        splitPane(orientation:JSplitPane.VERTICAL_SPLIT,
              constraints: BorderLayout.NORTH, dividerLocation:100) {
          panel(constraints: "bottom", id: "controlPanel") {
            tableLayout(cellpadding: 3) { 
              tr {
                td { label("Assignment Name: ") }
                td { label(assignmentName) }
              }
              tr {
                td { label(" Name: ") }
                td { label(id: "userNameLabel", text: "") }
              }
              tr {
                td(id: "fileChooser") {
                  panel {
                    button(id: "fileNameLabel", text:'Record Grade',
                      actionPerformed: {projX.populateGuiWithNextUser(false)})
                  }
                }
                td(id: "skipUserPanel") {
                  panel {
                    button(text:'Skip User',
                      actionPerformed: {projX.populateGuiWithNextUser(true)})
                  }
                }
              }
            }
          }
          scrollPane(constraints: "bottom", preferredSize: [800, 600]) {
            fileText = textArea(lineWrap: true, text: "")
          }
        }
    }
  scoreFrame = swing.frame(title:'Scoring', size: [400, 300],
      defaultCloseOperation:JFrame.EXIT_ON_CLOSE, pack:true, show:false) {
        panel(id: "scorePanel") {
         tableLayout(cellpadding: 3) { 
          for (int i=0; i < listScores.size(); i++) {
            tr {
              td {
              toggles << widget(new ToggleView(), model: listScores[i],
                   action: swing.action(closure: { event ->
                     event.source.model.enabled = event.source.selected
                    }))
              }
              td { label(String.valueOf(listScores[i].score)) }
              td { label(listScores[i].text) }
            }
          }
        }
      }
    }
  return swing
}
}
