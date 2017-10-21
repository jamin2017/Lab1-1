package team.mxj;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/*
 * �����࣬���б�������
 * 
 * @author Ghost
 *
 */
class Vertex {

  private Vector<Edge> edges = new Vector<Edge>();

  /*
   * ��ȡ�ߵ�Ȩֵ�����߲�����ʱȨֵΪ0
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
   * Ϊ�������һ���ߣ����ߴ���ʱΪ��Ȩֵ��һ
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
   * ���ر���ĵ����������ڱ�������ı�
   * 
   * @return
   */
  public Iterator<Edge> iterator() {
    return edges.iterator();
  }

  /*
   * ����ͼ������·�����ɹ���
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
