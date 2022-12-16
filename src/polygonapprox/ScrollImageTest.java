package polygonapprox;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ScrollImageTest extends JPanel {
    private static final long serialVersionUID = 1L;
    private BufferedImage image;
    private JPanel canvas;

    public ScrollImageTest() {
    	String imgFileName = "./data/beetle-1.gif";
        try {
            this.image = ImageIO.read(new File(imgFileName));
        }catch(IOException ex) {
            Logger.getLogger(ScrollImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.canvas = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                //super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        //canvas.add(new JButton("Currently I do nothing"));
        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        JScrollPane sp = new JScrollPane(canvas);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel p = new ScrollImageTest();
                JFrame f = new JFrame();
                f.setContentPane(p);
                f.setSize(400, 300);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);
            }
        });
    }
}