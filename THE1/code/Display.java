import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    public Display() { this.setBackground(new Color(180, 180, 180)); }

    @Override
    public Dimension getPreferredSize() { return super.getPreferredSize(); }

    //Entities' draw methods are called here
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Common.getGoldPrice().draw((Graphics2D) g);
        g.drawLine(0, Common.getUpperLineY(), Common.getWindowWidth(), Common.getUpperLineY());
        // TODO
        Common.getChina().draw((Graphics2D) g);
        Common.getUsa().draw((Graphics2D) g);
        Common.getIsrael().draw((Graphics2D) g);
        Common.getRussia().draw((Graphics2D) g);
        Common.getTurkey().draw((Graphics2D) g);
        Common.getMss().draw((Graphics2D) g);
        Common.getCia().draw((Graphics2D) g);
        Common.getMit().draw((Graphics2D) g);
        Common.getMossad().draw((Graphics2D) g);
        Common.getSvr().draw((Graphics2D) g);
        for(Order order : Common.getOrders()) {
            order.draw((Graphics2D) g);
        }
    }
}