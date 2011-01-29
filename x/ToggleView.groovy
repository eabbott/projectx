package x
import javax.swing.JToggleButton

class ToggleView extends JToggleButton implements Observer {
    ScoreDefinition model;

    void setModel(ScoreDefinition model) {
        this.model?.removeObserver(this)
        this.model = model
        model.addObserver(this)
    }

    public void update(Observable o, Object flag) {
println("this has been updated")
      selected = flag
    }
}
