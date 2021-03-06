package x
import javax.swing.JTextField

class ScoreView extends JTextField implements Observer {
    ScoreDefinition model;

    void setModel(ScoreDefinition model) {
        this.model?.removeObserver(this)
        this.model = model
        model.addObserver(this)
    }

    public void update(Observable o, Object updatedText) {
      text = updatedText
    }
}
