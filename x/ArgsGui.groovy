package x

import groovy.swing.SwingBuilder
import groovy.beans.Bindable
import javax.swing.*
import java.awt.*
import x.*

public class ArgsGui {
ProjX projX
def argsFrame
def assignments
String lastCourse

public ArgsGui(ProjX projX, String lastCourse, def assignments) {
  this.projX = projX
  this.lastCourse = lastCourse
  this.assignments = assignments
}

public void show() {
  argsFrame.show()
  SwingUtilities.updateComponentTreeUI(argsFrame)
}

public void close() {
  argsFrame.dispose()
}

public SwingBuilder build() {
  def swing = new SwingBuilder()
  swing.setVariable("lastCourse", lastCourse)

  argsFrame = swing.frame(title:'Args', size: [400, 300], pack:true, show:false) {
       panel(id: "argsPanel") {
         tableLayout(cellpadding: 3) {
           tr {
             td(align: "right") { label("Course: ") }
             td { textField(id:'course', columns: 20, text: lastCourse) } 
           }
           tr {
             td(align: "right") { label("Assignment: ") }
             td { comboBox(id: "assignmentChooser", items: assignments) }
           }
           tr {
             td(align: "right") { label("Only grade late assignments") }
             td { checkBox(id: "late") }
           }
           tr {
             td { label("") }
             td { button(text:'Start Grading', actionPerformed: {projX.continueWithArguments()}) }
           }
         }
       }
  }
  return swing
}
}
