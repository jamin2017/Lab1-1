package team.mxj;

/*
 * 边类，toVertex为该边指向顶点，weight为该边权值
 * @author Ghost
 *
 */
class Edge {

  public String toVertex;
  public int weight;

  public Edge(String toVertex, int weight) {
    this.toVertex = toVertex;
    this.weight = weight;
  }

}
