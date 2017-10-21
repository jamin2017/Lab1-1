package team.mxj; // NOPMD by lumi on 17-10-20 下午12:21

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.sound.midi.Transmitter;


/**
 * 有向图类
 */
class DWgraph {
  

// 存放顶点的字典（映射），key为顶点字符串，value为顶点类（边集），
  // 实际上是一个邻接表的形式存放图结构
  private Map<String, Vertex> vertexs;

  // 读取文件时用于存取单词并保持次序的单次数组
  private Vector<String> words;

  // 生成图像时存储基本的DOT操作语句的字符串，用于在最短路径中的作图
  private String baseDot;

  /**
   * 处理每条传入的字符串，获取其中的单词数组，全部为小写
   * 
   * @param lineStr
   * 
   */
  private void readFromStr(final String lineStr) {
    final String lineStrNew = Pattern.compile("[^a-z]").matcher(lineStr.toLowerCase()).replaceAll(" ").trim(); // NOPMD by lumi on 17-10-20 下午3:49
    final String[] vertexList = lineStrNew.split(" +");
    for (int i = 0; i < vertexList.length; i++) {
      if (vertexList[i] != null && vertexList[i].length() != 0) {
        words.add(vertexList[i]);
      }
    }
  }

  /*
   * 根据获取的总单词数组，生成图类的结构
   */
  private void generate() {
	final int ttt = 1;
    if (words.size() > ttt) {
      Iterator<String> iterWord = words.iterator();
      String fromVertexStr = iterWord.next();
      do {
        final String toVertexStr = iterWord.next();
        Vertex fromV = vertexs.get(fromVertexStr);
        Vertex toV = vertexs.get(toVertexStr);
        if (fromV == null) {
          vertexs.put(fromVertexStr, new Vertex());
        }
        if (toV == null) {
          vertexs.put(toVertexStr, new Vertex());
        }
        vertexs.get(fromVertexStr).addEdge(toVertexStr);
        fromVertexStr = toVertexStr;
      } while (iterWord.hasNext());
    } else if (words.size() == 1) {
      vertexs.put(words.get(0), new Vertex());
    }
  }

  /*
   * 读取一个txt文件，将其中的单次信息变为图
   * 
   * @param fileDir
   */
  public void readFile(File fileDir) {
	
    vertexs = new HashMap<String, Vertex>();
    words = new Vector<String>();
    if (fileDir.isFile() && Pattern.matches("^.+\\.txt$", fileDir.getName())) {
      try {
        BufferedReader txtIn = new BufferedReader(new FileReader(fileDir));
        String data = txtIn.readLine();
        while (data != null) {
          readFromStr(data);
          data = txtIn.readLine();
          System.out.println(data);
        }
        txtIn.close();
        generate();
      } catch (FileNotFoundException e) {
    	final Logger logger = Logger.getLogger("error"); 
        logger.severe(e.toString());
      } catch (IOException e) {
    	final Logger logger = Logger.getLogger("error"); 
        logger.severe(e.toString());
      }
    }
  }

  /*
   * 获取图的顶点个数，用于在GUI开始界面是否进入后序功能的判断
   * 
   * @return
   */
  public int size() {
    return vertexs.size();
  }

  /*
   * 获取绘制该图的基本DOT操作的字符串，用于最短路径中的绘制
   * 
   * @return
   */
  public String getBaseDot() {
    return baseDot;
  }

  /*
   * 绘制根据图结构生成的图片，并保存到临时文件中，提供以后的用户自定义保存的源
   */
  public void showImg() {
    Iterator<Entry<String, Vertex>> iterV = vertexs.entrySet().iterator();
    GraphViz graphViz = new GraphViz();
    graphViz.addln(graphViz.start_graph());
    while (iterV.hasNext()) {
      Entry<String, Vertex> tvPmd = iterV.next();
      graphViz.addln(tvPmd.getKey());
      Iterator<Edge> iterTv = tvPmd.getValue().iterator();
      while (iterTv.hasNext()) {
        Edge edge = iterTv.next();
        graphViz.add(tvPmd.getKey() + "->" + edge.toVertex);
        graphViz.addln("[label=\"" + Integer.toString(edge.weight) + "\"]");
      }
    }
    baseDot = graphViz.getDotSource();
    graphViz.addln(graphViz.end_graph());
    File out = new File("src/source/image/out.png");
    graphViz.writeGraphToFile(graphViz.getGraph(graphViz.getDotSource(), "png"), out);
  }

  /**
   * 根据输入获取桥接词数组
   * @param fromVertexStr
   * @param toVertexStr
   * @return 当返回的数组内带有RetCode(含有大写，所以不会和顶点中的retcode单词冲突)时代表获取的顶点字符串有误 0 表示出点不存在
   *         1表示入点不存在 2表示两点都不存在 当返回空数组时表示无桥接词
   */
  public String[] getBridgingList(String fromVertexStr, String toVertexStr) {
    fromVertexStr = fromVertexStr.toLowerCase();
    toVertexStr = toVertexStr.toLowerCase();
    Vector<String> bridgingWords = new Vector<String>();
    Vertex fromV = vertexs.get(fromVertexStr);
    Vertex toV = vertexs.get(toVertexStr);
    if (fromV == null && toV != null) {
      bridgingWords.add("0");
    } else if (fromV != null && toV == null) {
      bridgingWords.add("1");
    } else if (fromV == null && toV == null) {
      bridgingWords.add("2");
    } else if (fromV != null) {
      Iterator<Edge> iterFromE = fromV.iterator();
      while (iterFromE.hasNext()) {
        Edge tmpFromE = iterFromE.next();
        Iterator<Edge> iterToE = vertexs.get(tmpFromE.toVertex).iterator();
        while (iterToE.hasNext()) {
          Edge tmpToE = iterToE.next();
          if (tmpToE.toVertex.equals(toVertexStr)) {
            bridgingWords.add(tmpFromE.toVertex);
          }
        }
      }
    }
    return bridgingWords.toArray(new String[bridgingWords.size()]);
  }

  /*
   * 根据桥接词返回一个根据输入生成的句子，以单次数组的形式返回，原单词保持原样，去除非单词符号
   * 
   * @param inputText
   * @return
   */
  public String[] makeNewText(String inputtext) {
	String inputText = inputtext;
    inputText = Pattern.compile("[^a-zA-Z]").matcher(inputText).replaceAll(" ").trim();
    String[] inputWords = inputText.split(" +");
    Vector<String> newWords = new Vector<String>();
    if (inputWords.length > 1) {
      newWords.add(inputWords[0]);
      for (int i = 1; i < inputWords.length; i++) {
        String[] tmpWords = getBridgingList(inputWords[i - 1], inputWords[i]);
        if (tmpWords.length != 0 && !tmpWords[0].equals("0") 
            && !tmpWords[0].equals("1") && !tmpWords[0].equals("2")) {
          newWords.add(tmpWords[new Random().nextInt(tmpWords.length)]);
        }
        newWords.add(inputWords[i]);
      }
    } else if (inputWords.length == 1) {
      newWords.add(inputWords[0]);
    }
    return newWords.toArray(new String[newWords.size()]);
  }

  /**
   * 用于存放最短路径信息的类，在最短路径中的处理中用到
   * 
   * @author Ghost
   *
   */
  private class DisToVertex {
    public String vertexStr;
    public boolean beenQueue;
    public int pathLength;
    public Vector<Vector<String>> vertexStrInPath;

    public DisToVertex(String vertexStr) {
      this.vertexStr = vertexStr;
      this.beenQueue = false;
      this.pathLength = Integer.MAX_VALUE;
      this.vertexStrInPath = new Vector<Vector<String>>();
    }
  }

  /*
   * 实现最短路径的算法函数，增加了对同最短路径的存储，即可以将全部最短路径记录
   * 
   * @param fromVertexStr
   * @return
   */
  private Map<String, DisToVertex> getShortestPaths(String fromVertexStr) {
    Comparator<DisToVertex> cmpDisToVertex = new Comparator<DisToVertex>() {
      @Override
      public int compare(DisToVertex obj1, DisToVertex obj2) {
        if (obj1.pathLength > obj2.pathLength) {
          return 1;
        } else if (obj1.pathLength < obj2.pathLength) {
          return -1;
        } else {
          return 0;
        }
      }
    };

    DisToVertex fromVDisToV = new DisToVertex(fromVertexStr);
    fromVDisToV.pathLength = 0;
    Map<String, DisToVertex> pathWords = new HashMap<String, DisToVertex>();
    
    Vector<String> tmpVector = new Vector<String>();
    tmpVector.add(fromVertexStr);
    fromVDisToV.vertexStrInPath.add(tmpVector);
    pathWords.put(fromVertexStr, fromVDisToV);
    Queue<DisToVertex> queueVertex = new PriorityQueue<DisToVertex>(cmpDisToVertex);
    queueVertex.add(fromVDisToV);

    while (!queueVertex.isEmpty()) {
      DisToVertex tmpOutDisToV = queueVertex.poll();
      tmpOutDisToV.beenQueue = true;
      Iterator<Edge> iterToE = vertexs.get(tmpOutDisToV.vertexStr).iterator();
      while (iterToE.hasNext()) {
        Edge tmpToE = (Edge) iterToE.next();
        DisToVertex tmpToEDisToV = pathWords.get(tmpToE.toVertex);
        if (tmpToEDisToV == null) {
          tmpToEDisToV = new DisToVertex(tmpToE.toVertex);
          pathWords.put(tmpToE.toVertex, tmpToEDisToV);
          queueVertex.add(tmpToEDisToV);
        }
        if (!tmpToEDisToV.beenQueue) {
          if (tmpToEDisToV.pathLength > tmpOutDisToV.pathLength + tmpToE.weight) {
            tmpToEDisToV.pathLength = tmpOutDisToV.pathLength + tmpToE.weight;
            tmpToEDisToV.vertexStrInPath = new Vector<Vector<String>>();
            Iterator<Vector<String>> iterTmpVector = tmpOutDisToV.vertexStrInPath.iterator();
            while (iterTmpVector.hasNext()) {
              Vector<String> tvector = iterTmpVector.next();
              Vector<String> cpVector = new Vector<String>(tvector);
              cpVector.add(tmpToE.toVertex);
              tmpToEDisToV.vertexStrInPath.add(cpVector);
            }
          } else if (tmpToEDisToV.pathLength == tmpOutDisToV.pathLength + tmpToE.weight) {
            Iterator<Vector<String>> iterTmpVector = tmpOutDisToV.vertexStrInPath.iterator();
            while (iterTmpVector.hasNext()) {
              Vector<String> tvector = iterTmpVector.next();
              Vector<String> cpVector = new Vector<String>(tvector);
              cpVector.add(tmpToE.toVertex);
              tmpToEDisToV.vertexStrInPath.add(cpVector);
            }
          }
        }
      }
    }

    return pathWords;
  }

  /*
   * 最短路径算法的入口函数，判断顶点是否存在的预处理，和处理最短路径算法返回的信息
   * 
   * @param fromVertexStr
   * @param toVertexStr
   * @return RetCode 0表示出点不存在 1表示入点不存在 2表示无出点到入点的路径
   */
  public Map<String, Vector<String[]>> getShortestPath(String fromVertexStr, String toVertexStr) {
    // 当输入入点为空时，执行第二入口函数
    if (toVertexStr.equals("")) {
      return getShortestPath(fromVertexStr);
    }

    fromVertexStr = fromVertexStr.toLowerCase();
    toVertexStr = toVertexStr.toLowerCase();
    Map<String, Vector<String[]>> result = new HashMap<String, Vector<String[]>>();

    Vector<String[]> tmpVector;
    if (vertexs.get(fromVertexStr) == null) {
      String[] noFromVcode = { "0" };
      tmpVector = new Vector<String[]>();
      tmpVector.add(noFromVcode);
      result.put("RetCode", tmpVector);
    } else if (vertexs.get(toVertexStr) == null) {
      String[] noToVcode = { "1" };
      tmpVector = new Vector<String[]>();
      tmpVector.add(noToVcode);
      result.put("RetCode", tmpVector);
    } else {
      DisToVertex resultDisToV = getShortestPaths(fromVertexStr).get(toVertexStr);
      if (resultDisToV != null) {
        Iterator<Vector<String>> iterVector = resultDisToV.vertexStrInPath.iterator();
        tmpVector = new Vector<String[]>();
        if (iterVector.hasNext()) {
          String[] lenString = { "Length", Integer.toString(resultDisToV.pathLength) };
          tmpVector.add(lenString);
        }
        while (iterVector.hasNext()) {
          Vector<String> tvector = iterVector.next();
          tmpVector.add(tvector.toArray(new String[tvector.size()]));
        }
        result.put(toVertexStr, tmpVector);
      } else {
        String[] noPathToVcode = { "2" };
        tmpVector = new Vector<String[]>();
        tmpVector.add(noPathToVcode);
        result.put("RetCode", tmpVector);
      }
    }

    return result;
  }

  /*
   * 最短路径算法的第二入口函数，当输入入点为空时将会进入此函数，功能与返回同第一入口函数
   * 
   * @param fromVertexStr
   * @return 这里RetCode 只有0，因为不含入点且出点一定包含一条到自身的最短路径0
   */
  public Map<String, Vector<String[]>> getShortestPath(String fromVertexStr) {
    fromVertexStr = fromVertexStr.toLowerCase();
    Map<String, Vector<String[]>> result = new HashMap<String, Vector<String[]>>();

    Vector<String[]> tmpVector;
    if (vertexs.get(fromVertexStr) == null) {
      String[] noFromVcode = { "0" };
      tmpVector = new Vector<String[]>();
      tmpVector.add(noFromVcode);
      result.put("RetCode", tmpVector);
    } else {
      Iterator<Entry<String, DisToVertex>> iterMap = 
          getShortestPaths(fromVertexStr).entrySet().iterator();
      while (iterMap.hasNext()) {
        Entry<String, DisToVertex> tmap = iterMap.next();
        Iterator<Vector<String>> iterVector = tmap.getValue().vertexStrInPath.iterator();
        tmpVector = new Vector<String[]>();
        if (iterVector.hasNext()) {
          String[] lenString = {"Length", Integer.toString(tmap.getValue().pathLength)};
          tmpVector.add(lenString);
        }
        while (iterVector.hasNext()) {
          Vector<String> tvector = iterVector.next();
          tmpVector.add(tvector.toArray(new String[tvector.size()]));
        }
        result.put(tmap.getKey(), tmpVector);
      }
    }
    return result;
  }

  /*
   * 执行随机路径函数的功能，会返回当前出点的随机入点
   * 
   * @param fromVertexStr
   * @return 0表示无出点 1表示无入点（出点无边指向其他点）
   */
  private String randomNext(String fromVertexStr) {
    String next;
    Vertex fromV = vertexs.get(fromVertexStr);
    if (fromV != null) {
      Edge nextE = fromV.randomToVertex();
      if (nextE != null) {
        next = nextE.toVertex;
      } else {
        next = "1";
      }
    } else {
      next = "0";
    }
    return next;
  }

  /*
   * 生成随即路径的入口函数
   * 
   * @return
   */
  public String[] randomNext() {
    Vector<String> nextList = new Vector<String>();

    Iterator<Entry<String, Vertex>> iterV = vertexs.entrySet().iterator();
    int stop = new Random().nextInt(vertexs.size());
    String fromVertexStr = "";
    while (iterV.hasNext()) {
      Entry<String, Vertex> tv = iterV.next();
      if (stop-- == 0) {
        fromVertexStr = tv.getKey();
        break;
      }
    }
    if (fromVertexStr.equals("")) {
      nextList.add("0");
    } else {
      boolean flag;
      String lastV;
      String nextStr;
      do {
        nextList.add(fromVertexStr);
        nextStr = randomNext(fromVertexStr);
        if (nextStr.equals("1") || nextStr.equals("0")) {
          break;
        }
        Iterator<String> iterVInRan = nextList.iterator();
        lastV = iterVInRan.next();
        flag = true;
        while (iterVInRan.hasNext()) {
          String vinRan = iterVInRan.next();
          if (lastV.equals(fromVertexStr) && vinRan.equals(nextStr)) {
            nextList.add(nextStr);
            flag = false;
            break;
          }
          lastV = vinRan;
        }
        fromVertexStr = nextStr;
      } while (flag);
    }

    return nextList.toArray(new String[nextList.size()]);
  }

}
