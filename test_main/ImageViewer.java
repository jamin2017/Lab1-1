package team.mxj;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class ImageViewer extends JComponent {

  /*
   * 自定义可承载图像容器的组件
   */
  private static final long serialVersionUID = 1L;

  JSlider slider;
  ImageComponent image;
  JScrollPane scrollPane;

  public ImageViewer() {
    slider = new JSlider(0, 1000, 500);
    scrollPane = new JScrollPane();
    slider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        image.setZoom(2. * slider.getValue() / slider.getMaximum());
      }
    });

    this.setLayout(new BorderLayout());
    this.add(slider, BorderLayout.NORTH);
    this.add(scrollPane);
    slider.setVisible(false);
    scrollPane.setVisible(false);
  }

  public void setImage(String path) {
    slider.setValue(500);
    try {
      image = new ImageComponent(path);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    scrollPane.setViewportView(image);
    slider.setVisible(true);
    scrollPane.setVisible(true);
  }

  public void removeImage() {
    slider.setVisible(false);
    scrollPane.setVisible(false);
  }
}
