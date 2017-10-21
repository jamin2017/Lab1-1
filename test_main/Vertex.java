package team.mxj;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/*
 * 顶点类，存有边类数组
 * 
 * @author Ghost
 *
 */
class Vertex {

  private Vector<Edge> edges = new Vector<Edge>();

  /*
   * 获取边的权值，当边不存在时权值为0
   * 
   * @param toVertexStr
   * @return
   */
  public int getEdgeWeight(String toVertexStr) {
    int weight = 0;
    Iterator<Edge> iterE = edges.iterator();
    while (iterE.hasNext()) {
      Edge e = iterE.next();
      if (e.toVertex.equals(toVertexStr)) {
        weight = e.weight;
        break;
      }
    }
    return weight;
  }

  /*
   * 为顶点添加一条边，当边存在时为边权值加一
   * 
   * @param toVertexStr
   */
  public void addEdge(String toVertexStr) {
    boolean flag = false;
    Iterator<Edge> iterE = edges.iterator();
    while (iterE.hasNext()) {
      Edge e = iterE.next();
      if (e.toVertex.equals(toVertexStr)) {
        ++e.weight;
        flag = true;
        break;
      }
    }
    if (!flag) {
      edges.add(new Edge(toVertexStr, 1));
    }
  }

  /*
   * 返回边类的迭代器，用于遍历顶点的边
   * 
   * @return
   */
  public Iterator<Edge> iterator() {
    return edges.iterator();
  }

  /*
   * 辅助图类的随机路径生成功能
   * 
   * @return
   */
  public Edge randomToVertex() {
    if (edges.isEmpty()) {
      return null;
    }
    return edges.get(new Random().nextInt(edges.size()));
  }

}
