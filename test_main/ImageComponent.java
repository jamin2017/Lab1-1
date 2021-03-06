package team.mxj;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

class ImageComponent extends JComponent {

  /*
   * �Զ���ͼ������
   */
  private static final long serialVersionUID = 1L;

  BufferedImage img;

  public ImageComponent(String path) throws IOException {
    img = ImageIO.read(new File(path));
    setZoom(1);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension dim = getPreferredSize();
    g.drawImage(img, 0, 0, dim.width, dim.height, this);
  }

  public void setZoom(double zoom) {
    int w = (int) (zoom * img.getWidth());
    int h = (int) (zoom * img.getHeight());
    setPreferredSize(new Dimension(w, h));
    revalidate();
    repaint();
  }
}