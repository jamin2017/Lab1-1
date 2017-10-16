package test_main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFileChooser;

import java.awt.CardLayout;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
// c line

class ImageComponent extends JComponent {

    /**
	 * 自定义图像容器
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

class ImageViewer extends JComponent {
	
    /**
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

public class Lab1GUI extends JFrame {

	/**
	 * 用于去除警告
	 */
	private static final long serialVersionUID = 1L;

	static DWGraph graph = new DWGraph();

	private JPanel contentPane;
	private JPanel pSelectFile;
	private JPanel pMain;
	private JPanel pShow;
	private JPanel pShortPath;
	private JPanel pBridge;
	private JPanel pRandom;
	private JTextField dirField;
	private JLabel dirOK;
	private JLabel bridgeHome;
	private JLabel shortHome;
	private JLabel randomHome;
	
	private ImageViewer showImageCom;
	private JTextField fromWordText;
	private JTextField toWordText;
	private JTextArea inputArea;
	private JTextArea randomArea;
	private JTextField outWordF;
	private JTextField inWordF;
	private JList<String> pathList;
	private JPanel pathImageP;
	private ImageViewer pathImageV;
	
	static private Map<Integer, File> mapImg;

	private JLabel errorLabel;
	private JPanel pError;

	/**
	 * Launch the application. 主函数
	 */
	public static void main(String[] args) {
		// 准备临时文件存储路径
		File sourceDir = new File("src/source/");
		File textDir = new File("src/source/text/");
		File imgDir = new File("src/source/image/");
		File tmpDir = new File("src/source/tmp/");
		
		if (!sourceDir.isDirectory()) {
			sourceDir.mkdirs();
		}
		if (!textDir.isDirectory()) {
			textDir.mkdirs();
		}
		if (!imgDir.isDirectory()) {
			imgDir.mkdirs();
		}
		if (!tmpDir.isDirectory()) {
			tmpDir.mkdirs();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Lab1GUI frame = new Lab1GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 展示图片并保存到临时文件
	 * @param G  输入的图类
	 */
	public static void showDirectedGraph(DWGraph G) {
		graph.showIMG();
	}
	
	/**
	 * 查询桥接词
	 * @param word1
	 * @param word2
	 * @return
	 */
	public static String queryBridgeWords(String word1, String word2) {
		String[] tmpList = graph.getBridgingList(word1, word2);
		if (tmpList.length != 0) {
			if (tmpList[0].equals("0")) {
				return "No \"" + word1 + "\" in the graph!";
			} else if (tmpList[0].equals("1")) {
				return "No \"" + word2 + "\" in the graph!";
			} else if (tmpList[0].equals("2")) {
				return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
			} else {
				String result = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: ";
				if (tmpList.length > 1) {
					result = result + String.join(", ", tmpList);
					result = Pattern.compile(", " + tmpList[tmpList.length - 1] + "$").matcher(result).replaceAll(", and " + tmpList[tmpList.length - 1]);
					return result;
				} else {
					return result + tmpList[0];
				}
			}
		} else {
			return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
		}
	}
	
	/**
	 * 根据桥接词生成文本
	 * @param inputText
	 * @return
	 */
	public static String generateNewText(String inputText) {
		return String.join(" ", graph.makeNewText(inputText));
	}
	
	/**
	 * 获取最短路径
	 * @param word1
	 * @param word2
	 * @return
	 */
	public static Vector<String> calcShortestPath(String word1, String word2) {
		Vector<String> paths = new Vector<String>();
		
		Map<String, Vector<String[]>> tmpMap = graph.getShortestPath(word1, word2);
		Iterator<Entry<String, Vector<String[]>>> iterTM = tmpMap.entrySet().iterator();
		String tMsgStr;
		String tPathStr;
		
		GraphViz graphViz;
		File out;
		int imgCount = 0;
		mapImg = new HashMap<Integer, File>();
		
		while (iterTM.hasNext()) {
			tMsgStr = "";
			tPathStr = "";
			Entry<String, Vector<String[]>> tEntry = iterTM.next();
			if (tEntry.getKey().equals("RetCode")) {
				tMsgStr = "RetCode";
				tPathStr = tEntry.getValue().get(0)[0];
				paths.add(tMsgStr + "==" + tPathStr);
			} else {
				Iterator<String[]> iterTPath = tEntry.getValue().iterator();
				while (iterTPath.hasNext()) {
					String[] tPath = iterTPath.next();
					if (tPath[0].equals("Length")) {
						tMsgStr = tEntry.getKey();
						tMsgStr += "==";
						tMsgStr += tPath[1];
					} else {
						tPathStr = String.join("->", tPath);
						graphViz = new GraphViz();
						graphViz.addln(graph.getBaseDot());
						graphViz.addln("edge[color=\"#FF6347\"]");
						String outV = tPath[0];
						graphViz.addln(outV + "[fillcolor=\"#FFAA22\", style=filled]");
						for (int i = 1; i < tPath.length; i++) {
							graphViz.addln(outV + "->" + tPath[i]);
							outV = tPath[i];
							graphViz.addln(outV + "[fillcolor=\"#FFAA22\", style=filled]");
						}
						graphViz.addln(graphViz.end_graph());
						out = new File("src/source/image/" + Integer.toString(imgCount) + ".png");
						graphViz.writeGraphToFile(graphViz.getGraph(graphViz.getDotSource(), "png"), out);
						mapImg.put(imgCount, out);
						imgCount++;
						paths.add(tMsgStr + "==" + tPathStr);
					}
				}
			}
		}
		
		return paths;
	}
	
	/**
	 * 根据图随机生成一条路径
	 * @return
	 */
	public static String randomWalk() {
		return String.join(" ", graph.randomNext());
	}

	/**
	 * Create the frame. 建立主窗体和绑定事件
	 */
	public Lab1GUI() {
		setResizable(false);
		contentPane = new JPanel();
		pSelectFile = new JPanel();
		pMain = new JPanel();
		pShow = new JPanel();
		pShow.setBackground(Color.RED);
		pBridge = new JPanel();
		pBridge.setBackground(Color.CYAN);
		pShortPath = new JPanel();
		pShortPath.setBackground(Color.GREEN);
		pRandom = new JPanel();
		pRandom.setBackground(Color.PINK);
		
		setForeground(Color.LIGHT_GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1080, 720);
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		pSelectFile.setBackground(Color.LIGHT_GRAY);
		contentPane.add(pSelectFile, "name_87792113351618");
		
		File openFile = new File("src/source/text/");
		JFileChooser tChooser = new JFileChooser(openFile);
		JLabel dirSelect = new JLabel("select");
		dirOK = new JLabel("OK");
		dirOK.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (dirOK.isEnabled()) {
					pSelectFile.setVisible(false);
					try {
						File file = new File(dirField.getText());
						if (file.length() > 200*1024) {
							errorLabel.setText("File is too large!");
							pError.setVisible(true);
						} else {				
							graph.readFile(file);
							
							graph.showIMG();
							
							showImageCom.setImage("src/source/image/out.png");
							
							if (graph.size() > 0) {
								pMain.setVisible(true);
							} else {
								errorLabel.setText("No word in the file!");
								pError.setVisible(true);
							}
						}
					} catch (Exception e1) {
						errorLabel.setText("GraphViz can't handle to many words: " + Integer.toString(graph.size()));
						pError.setVisible(true);
					}
				}
			}
		});
		dirOK.setEnabled(false);
		dirOK.setHorizontalAlignment(SwingConstants.CENTER);
		dirOK.setOpaque(true);
		dirSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("select txt", "txt");
				tChooser.setFileFilter(filter);
				int value = tChooser.showOpenDialog(Lab1GUI.this);
				if (value == JFileChooser.APPROVE_OPTION) {
					File file = tChooser.getSelectedFile();
					dirField.setText(file.getAbsolutePath());
					dirOK.setEnabled(true);
				}
			}
		});
		dirSelect.setHorizontalAlignment(SwingConstants.CENTER);
		dirSelect.setOpaque(true);
		
		dirField = new JTextField();
		dirField.setEditable(false);
		dirField.setColumns(10);
		
		GroupLayout gl_pSelectFile = new GroupLayout(pSelectFile);
		gl_pSelectFile.setHorizontalGroup(
			gl_pSelectFile.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_pSelectFile.createSequentialGroup()
					.addGap(130)
					.addComponent(dirField, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(dirSelect, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(dirOK, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
					.addGap(88))
		);
		gl_pSelectFile.setVerticalGroup(
			gl_pSelectFile.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pSelectFile.createSequentialGroup()
					.addGap(160)
					.addGroup(gl_pSelectFile.createParallelGroup(Alignment.BASELINE)
						.addComponent(dirField, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
						.addComponent(dirSelect, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
						.addComponent(dirOK, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
					.addGap(490))
		);
		pSelectFile.setLayout(gl_pSelectFile);
		
		pMain.setBackground(Color.YELLOW);
		contentPane.add(pMain, "name_87805731835073");
		
		JLabel openShowPage = new JLabel("show");
		openShowPage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				pMain.setVisible(false);
				pShow.setVisible(true);
			}
		});
		openShowPage.setHorizontalAlignment(SwingConstants.CENTER);
		openShowPage.setBackground(Color.RED);
		openShowPage.setOpaque(true);
		
		JLabel openBridgePage = new JLabel("bridge");
		openBridgePage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pMain.setVisible(false);
				pBridge.setVisible(true);
			}
		});
		openBridgePage.setHorizontalAlignment(SwingConstants.CENTER);
		openBridgePage.setBackground(Color.CYAN);
		openBridgePage.setOpaque(true);
		
		JLabel openShortPage = new JLabel("short");
		openShortPage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pMain.setVisible(false);
				pShortPath.setVisible(true);
			}
		});
		openShortPage.setHorizontalAlignment(SwingConstants.CENTER);
		openShortPage.setBackground(Color.GREEN);
		openShortPage.setOpaque(true);
		
		JLabel openRandomPage = new JLabel("random");
		openRandomPage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pMain.setVisible(false);
				pRandom.setVisible(true);
			}
		});
		openRandomPage.setHorizontalAlignment(SwingConstants.CENTER);
		openRandomPage.setBackground(Color.PINK);
		openRandomPage.setOpaque(true);
		GroupLayout gl_pMain = new GroupLayout(pMain);
		gl_pMain.setHorizontalGroup(
			gl_pMain.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pMain.createSequentialGroup()
					.addGap(87)
					.addComponent(openShowPage, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(openBridgePage, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(openShortPage, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(openRandomPage, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addGap(81))
		);
		gl_pMain.setVerticalGroup(
			gl_pMain.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_pMain.createSequentialGroup()
					.addGap(155)
					.addGroup(gl_pMain.createParallelGroup(Alignment.BASELINE)
						.addGroup(gl_pMain.createSequentialGroup()
							.addGap(1)
							.addComponent(openShortPage, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE))
						.addGroup(gl_pMain.createSequentialGroup()
							.addGap(1)
							.addComponent(openBridgePage, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE))
						.addComponent(openShowPage, GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
						.addComponent(openRandomPage, GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
					.addGap(199))
		);
		pMain.setLayout(gl_pMain);
		
		contentPane.add(pShow, "name_87859874744493");
		
		JLabel showHome = new JLabel("home");
		showHome.setBackground(Color.YELLOW);
		showHome.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pShow.setVisible(false);
				pMain.setVisible(true);
			}
		});
		showHome.setOpaque(true);
		showHome.setHorizontalAlignment(SwingConstants.CENTER);
		
		showImageCom = new ImageViewer();
		
		JLabel saveShow = new JLabel("save");
		saveShow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("select png", "png");
				chooser.setFileFilter(filter);
				int value = chooser.showSaveDialog(Lab1GUI.this);
				if (value == JFileChooser.APPROVE_OPTION) {
					File newPng = chooser.getSelectedFile();
					try {
						File outPng = new File("src/source/image/out.png");
						FileInputStream inf = new FileInputStream(outPng);
						FileOutputStream ouf = new FileOutputStream(new File(newPng.getAbsolutePath() + ".png"));
						
						int len;
						byte[] inPng = new byte[1024];
						while ((len=inf.read(inPng)) != -1) {
							ouf.write(inPng, 0, len);
						}
						
						inf.close();
						ouf.close();
					} catch (FileNotFoundException e1) {
						errorLabel.setText("src/source/image/out.png is missing!");
						pShow.setVisible(false);
						pError.setVisible(true);
					} catch (IOException e1) {
						errorLabel.setText("Can't open new file!");
						pShow.setVisible(false);
						pError.setVisible(true);
					}
				}
			}
		});
		saveShow.setOpaque(true);
		saveShow.setHorizontalAlignment(SwingConstants.CENTER);
		saveShow.setBackground(Color.LIGHT_GRAY);
		GroupLayout gl_pShow = new GroupLayout(pShow);
		gl_pShow.setHorizontalGroup(
			gl_pShow.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pShow.createSequentialGroup()
					.addGroup(gl_pShow.createParallelGroup(Alignment.LEADING)
						.addComponent(showHome, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
						.addComponent(saveShow, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
					.addComponent(showImageCom, GroupLayout.PREFERRED_SIZE, 919, GroupLayout.PREFERRED_SIZE))
		);
		gl_pShow.setVerticalGroup(
			gl_pShow.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pShow.createSequentialGroup()
					.addComponent(showHome, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 591, Short.MAX_VALUE)
					.addComponent(saveShow, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
				.addComponent(showImageCom, GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
		);
		pShow.setLayout(gl_pShow);
		
		contentPane.add(pBridge, "name_88270369633578");
		
		bridgeHome = new JLabel("home");
		bridgeHome.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pBridge.setVisible(false);
				pMain.setVisible(true);
			}
		});
		bridgeHome.setBackground(Color.YELLOW);
		bridgeHome.setOpaque(true);
		bridgeHome.setHorizontalAlignment(SwingConstants.CENTER);
		
		JTabbedPane bridgeFuncP = new JTabbedPane(JTabbedPane.TOP);
		
		JScrollPane newTextScrollP = new JScrollPane();
		GroupLayout gl_pBridge = new GroupLayout(pBridge);
		gl_pBridge.setHorizontalGroup(
			gl_pBridge.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pBridge.createSequentialGroup()
					.addComponent(bridgeHome, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
					.addGap(75)
					.addGroup(gl_pBridge.createParallelGroup(Alignment.LEADING)
						.addComponent(newTextScrollP, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 897, Short.MAX_VALUE)
						.addComponent(bridgeFuncP, GroupLayout.DEFAULT_SIZE, 897, Short.MAX_VALUE)))
		);
		gl_pBridge.setVerticalGroup(
			gl_pBridge.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pBridge.createSequentialGroup()
					.addGroup(gl_pBridge.createParallelGroup(Alignment.TRAILING)
						.addComponent(bridgeHome, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
						.addComponent(bridgeFuncP, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(newTextScrollP, GroupLayout.PREFERRED_SIZE, 354, GroupLayout.PREFERRED_SIZE))
		);
		
		JTextArea newTextArea = new JTextArea();
		newTextArea.setWrapStyleWord(true);
		newTextArea.setLineWrap(true);
		newTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
		newTextArea.setEditable(false);
		newTextScrollP.setViewportView(newTextArea);
		
		JPanel bridgeP = new JPanel();
		bridgeP.setBackground(UIManager.getColor("Button.background"));
		bridgeFuncP.addTab("bridge word", null, bridgeP, null);
		
		fromWordText = new JTextField();
		fromWordText.setFont(new Font("宋体", Font.PLAIN, 14));
		fromWordText.setColumns(10);
		
		JLabel fromWordLabel = new JLabel("from word");
		fromWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel toWordLabel = new JLabel("to word");
		toWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		toWordText = new JTextField();
		toWordText.setFont(new Font("宋体", Font.PLAIN, 14));
		toWordText.setColumns(10);
		
		JLabel dridgeDo = new JLabel("do");
		dridgeDo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String bridgeStr = queryBridgeWords(fromWordText.getText(), toWordText.getText());
				newTextArea.setText(bridgeStr);
			}
		});
		dridgeDo.setBackground(Color.MAGENTA);
		dridgeDo.setOpaque(true);
		dridgeDo.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel wordEmpty = new JLabel("empty");
		wordEmpty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fromWordText.setText("");
				toWordText.setText("");
			}
		});
		wordEmpty.setOpaque(true);
		wordEmpty.setBackground(Color.LIGHT_GRAY);
		wordEmpty.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_bridgeP = new GroupLayout(bridgeP);
		gl_bridgeP.setHorizontalGroup(
			gl_bridgeP.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_bridgeP.createSequentialGroup()
					.addGap(153)
					.addGroup(gl_bridgeP.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_bridgeP.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(toWordLabel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(toWordText, GroupLayout.PREFERRED_SIZE, 475, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_bridgeP.createSequentialGroup()
							.addComponent(fromWordLabel, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(fromWordText)))
					.addContainerGap(193, Short.MAX_VALUE))
				.addGroup(gl_bridgeP.createSequentialGroup()
					.addComponent(dridgeDo, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 644, Short.MAX_VALUE)
					.addComponent(wordEmpty, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))
		);
		gl_bridgeP.setVerticalGroup(
			gl_bridgeP.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_bridgeP.createSequentialGroup()
					.addGap(41)
					.addGroup(gl_bridgeP.createParallelGroup(Alignment.BASELINE)
						.addComponent(fromWordText, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(fromWordLabel, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_bridgeP.createParallelGroup(Alignment.LEADING)
						.addComponent(toWordLabel, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(toWordText, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
					.addGroup(gl_bridgeP.createParallelGroup(Alignment.LEADING, false)
						.addComponent(wordEmpty, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(dridgeDo, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)))
		);
		bridgeP.setLayout(gl_bridgeP);
		
		JPanel newTextP = new JPanel();
		bridgeFuncP.addTab("new text", null, newTextP, null);
		
		JLabel inputLabel = new JLabel("input");
		inputLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel newTextDo = new JLabel("do");
		newTextDo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String newStr = generateNewText(inputArea.getText());
				newTextArea.setText(newStr);
			}
		});
		newTextDo.setHorizontalAlignment(SwingConstants.CENTER);
		newTextDo.setBackground(Color.MAGENTA);
		newTextDo.setOpaque(true);
		
		JScrollPane inputScrollP = new JScrollPane();
		
		JLabel newTextEmpty = new JLabel("empty");
		newTextEmpty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				inputArea.setText("");
			}
		});
		newTextEmpty.setBackground(Color.LIGHT_GRAY);
		newTextEmpty.setOpaque(true);
		newTextEmpty.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_newTextP = new GroupLayout(newTextP);
		gl_newTextP.setHorizontalGroup(
			gl_newTextP.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_newTextP.createSequentialGroup()
					.addComponent(inputLabel, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(inputScrollP, GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE))
				.addGroup(gl_newTextP.createSequentialGroup()
					.addComponent(newTextDo, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 645, Short.MAX_VALUE)
					.addComponent(newTextEmpty, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
		);
		gl_newTextP.setVerticalGroup(
			gl_newTextP.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_newTextP.createSequentialGroup()
					.addGroup(gl_newTextP.createParallelGroup(Alignment.TRAILING)
						.addComponent(inputScrollP, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
						.addComponent(inputLabel, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
					.addGap(10)
					.addGroup(gl_newTextP.createParallelGroup(Alignment.BASELINE)
						.addComponent(newTextDo, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
						.addComponent(newTextEmpty, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)))
		);
		
		inputArea = new JTextArea();
		inputArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
		inputScrollP.setViewportView(inputArea);
		newTextP.setLayout(gl_newTextP);
		pBridge.setLayout(gl_pBridge);
		
		contentPane.add(pShortPath, "name_88309217334871");
		
		shortHome = new JLabel("home");
		shortHome.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pShortPath.setVisible(false);
				pMain.setVisible(true);
			}
		});
		shortHome.setBackground(Color.YELLOW);
		shortHome.setOpaque(true);
		shortHome.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane pathScrollP = new JScrollPane();
		
		pathImageP = new JPanel();
		pathImageP.setLayout(new BorderLayout(0, 0));
		pathImageV = new ImageViewer();
		pathImageP.add(pathImageV);
		
		outWordF = new JTextField();
		outWordF.setFont(new Font("宋体", Font.PLAIN, 14));
		outWordF.setColumns(10);
		
		JLabel pathDo = new JLabel("do");
		pathDo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pShortPath.setVisible(false);
				
				Vector<String> paths = calcShortestPath(outWordF.getText(), inWordF.getText());
				Vector<String> showPaths = new Vector<String>();
				boolean enable = true;
				pathImageV.removeImage();
				
				Iterator<String> iterPath = paths.iterator();
				while (iterPath.hasNext()) {
					String tPath = iterPath.next();
					String[] pathInfo = tPath.split("==");
					if (pathInfo[0].equals("RetCode")) {
						if (pathInfo[1].equals("0")) {
							showPaths.add("No \"" + outWordF.getText() + "\" in the graph!");
						} else if (pathInfo[1].equals("1")) {
							showPaths.add("No \"" + inWordF.getText() + "\" in the graph!");
						} else if (pathInfo[1].equals("2")) {
							showPaths.add("No path from \"" + outWordF.getText() + "\" to \"" + inWordF.getText() + "\" in the graph!");
						} else {
							showPaths.add("");
						}
						enable = false;
						break;
					} else {
						String[] pathStr = pathInfo[2].split("->");
						showPaths.add("Info: " + pathStr[0] + "->" + pathInfo[0] + "  Length: " + pathInfo[1] + "  Path: " + pathInfo[2]);
					}
				}
				pathList.setListData(showPaths);
				pathList.setEnabled(enable);
				
				pShortPath.setVisible(true);
			}
		});
		pathDo.setOpaque(true);
		pathDo.setHorizontalAlignment(SwingConstants.CENTER);
		pathDo.setBackground(Color.LIGHT_GRAY);
		
		JLabel outWordLabel = new JLabel("out word");
		outWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel inWordLabel = new JLabel("in word");
		inWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		inWordF = new JTextField();
		inWordF.setFont(new Font("宋体", Font.PLAIN, 14));
		inWordF.setColumns(10);
		
		JLabel pathEmpty = new JLabel("empty");
		pathEmpty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outWordF.setText("");
				inWordF.setText("");
			}
		});
		pathEmpty.setOpaque(true);
		pathEmpty.setHorizontalAlignment(SwingConstants.CENTER);
		pathEmpty.setBackground(Color.LIGHT_GRAY);
		GroupLayout gl_pShortPath = new GroupLayout(pShortPath);
		gl_pShortPath.setHorizontalGroup(
			gl_pShortPath.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_pShortPath.createSequentialGroup()
					.addGroup(gl_pShortPath.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pShortPath.createParallelGroup(Alignment.LEADING, false)
							.addComponent(shortHome, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
							.addComponent(pathDo, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
							.addComponent(outWordF))
						.addComponent(outWordLabel, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(inWordLabel, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(inWordF, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
						.addComponent(pathEmpty, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
					.addGroup(gl_pShortPath.createParallelGroup(Alignment.LEADING, false)
						.addComponent(pathImageP, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(pathScrollP, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE)))
		);
		gl_pShortPath.setVerticalGroup(
			gl_pShortPath.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pShortPath.createSequentialGroup()
					.addGroup(gl_pShortPath.createParallelGroup(Alignment.LEADING)
						.addComponent(shortHome, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
						.addComponent(pathScrollP, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_pShortPath.createParallelGroup(Alignment.TRAILING)
						.addComponent(pathImageP, GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
						.addGroup(gl_pShortPath.createSequentialGroup()
							.addGap(159)
							.addComponent(pathEmpty, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(outWordLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(outWordF, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
							.addGap(13)
							.addComponent(inWordLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(inWordF, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
							.addComponent(pathDo, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))))
		);
		
		pathList = new JList<String>();
		pathList.setToolTipText("双击");
		pathList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && pathList.isEnabled()) {
					pathImageV.setImage(mapImg.get(pathList.getSelectedIndex()).getAbsolutePath());
				}
			}
		});
		pathList.setEnabled(false);
		pathList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pathList.setFont(new Font("宋体", Font.PLAIN, 16));
		pathScrollP.setViewportView(pathList);
		pShortPath.setLayout(gl_pShortPath);
		
		contentPane.add(pRandom, "name_88659864355682");
		
		randomHome = new JLabel("home");
		randomHome.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pRandom.setVisible(false);
				pMain.setVisible(true);
			}
		});
		randomHome.setBackground(Color.YELLOW);
		randomHome.setOpaque(true);
		randomHome.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane randomScrollP = new JScrollPane();
		
		JLabel emptyRandom = new JLabel("empty");
		emptyRandom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				randomArea.setText("");
			}
		});
		emptyRandom.setHorizontalAlignment(SwingConstants.CENTER);
		emptyRandom.setOpaque(true);
		
		JLabel saveRandom = new JLabel("save");
		saveRandom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("select txt", "txt");
				chooser.setFileFilter(filter);
				int value = chooser.showSaveDialog(Lab1GUI.this);
				if (value == JFileChooser.APPROVE_OPTION) {
					File newTxt = new File(chooser.getSelectedFile().getAbsolutePath() + ".txt");
					try {
						FileWriter newTxtWriter = new FileWriter(newTxt);
						newTxtWriter.write(randomArea.getText());
						newTxtWriter.close();
					} catch (IOException e1) {
						errorLabel.setText("Can't open new file!");
						pRandom.setVisible(false);
						pError.setVisible(true);
					}
				}
			}
		});
		saveRandom.setHorizontalAlignment(SwingConstants.CENTER);
		saveRandom.setOpaque(true);
		
		JLabel randomDo = new JLabel("do");
		randomDo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String randomStr = randomWalk();
				randomArea.setText(randomStr);
			}
		});
		randomDo.setOpaque(true);
		randomDo.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_pRandom = new GroupLayout(pRandom);
		gl_pRandom.setHorizontalGroup(
			gl_pRandom.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pRandom.createSequentialGroup()
					.addGroup(gl_pRandom.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pRandom.createParallelGroup(Alignment.LEADING, false)
							.addComponent(emptyRandom, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(randomHome, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
						.addComponent(saveRandom, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
						.addComponent(randomDo, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
					.addComponent(randomScrollP, GroupLayout.PREFERRED_SIZE, 913, GroupLayout.PREFERRED_SIZE))
		);
		gl_pRandom.setVerticalGroup(
			gl_pRandom.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pRandom.createSequentialGroup()
					.addComponent(randomHome, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 383, Short.MAX_VALUE)
					.addComponent(randomDo, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(saveRandom, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(emptyRandom, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
				.addComponent(randomScrollP, GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
		);
		
		randomArea = new JTextArea();
		randomArea.setWrapStyleWord(true);
		randomArea.setLineWrap(true);
		randomArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
		randomArea.setEditable(false);
		randomScrollP.setViewportView(randomArea);
		pRandom.setLayout(gl_pRandom);
		
		pError = new JPanel();
		pError.setVisible(false);
		contentPane.add(pError, "name_373545610334493");
		pError.setLayout(new BorderLayout(0, 0));
		
		errorLabel = new JLabel("");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setBackground(Color.LIGHT_GRAY);
		errorLabel.setOpaque(true);
		errorLabel.setFont(new Font("宋体", Font.PLAIN, 40));
		pError.add(errorLabel, BorderLayout.CENTER);
	}
}
