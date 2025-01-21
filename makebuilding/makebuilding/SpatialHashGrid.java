// || Swami-Shriji ||
package makebuilding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.coor.LatLon;

import jtsjosm.JtsJosmAction;

public class SpatialHashGrid {

  Map<Integer, List<Way>> hashMap = new HashMap<>();
  int mapLength;
  Node center;
  int mapWidth;
  int mapHeight;
  int mapSize;
  int gridSize;


  public SpatialHashGrid(Node center, int gridSize, int width, int height) {
    this.mapWidth = (int) Math.ceil((double) width / (double) gridSize);
    this.mapHeight = (int) Math.ceil((double) height / (double) gridSize);
    this.mapSize = mapWidth * mapHeight;
    this.center = center;
    this.gridSize = gridSize;
    populateMap();
  }

  private void populateMap() {
    for(int i = -1*mapSize/2; i < mapSize/2 -1; i++) {
      this.hashMap.put(i, new ArrayList<Way>());
    }
  }

  public Node getCentroid(Way way) {
        if (way == null || way.getNodesCount() == 0) {
            return null; // Handle case where way is null or has no nodes
        }

        double sumLat = 0.0;
        double sumLon = 0.0;

        for (Node node : way.getNodes()) {
            sumLat += node.getCoor().lat();
            sumLon += node.getCoor().lon();
        }

        double centroidLat = sumLat / way.getNodesCount();
        double centroidLon = sumLon / way.getNodesCount();

        return new Node(new LatLon(centroidLat, centroidLon));
  }

  public boolean removeFromHash(Way w) {
    List<Way> waysOfHash = this.hashMap.get(getSpatialHash(w));
    for (Way way: waysOfHash) {
      if (w == way) {
        waysOfHash.remove(way);
        return false;
      }
    }
    return true;
  }

  public void addToHash(Way w) {
    //System.out.println("--------------------------------------------------");
    //System.out.println("Hash: " + getSpatialHash(w));
    this.hashMap.get(getSpatialHash(w)).add(w);
  }

  public int getSpatialHash(Way w) {
    Node n = getCentroid(w);
    int x = (int) (Math.floor(n.getEastNorth().getX() -
                              this.center.getEastNorth().getX()
                             )) / this.gridSize;
    int y = (int) (Math.floor(n.getEastNorth().getY() -
                              this.center.getEastNorth().getY()
                             )) / this.gridSize;

    return (y * this.mapWidth) + x;
  }

  public List<Way> getAdjacentWays(Way w) {
    return getAdjacentWays(getSpatialHash(w));
  }

  public List<Way> getAdjacentWays(int hash) {

    int[][] directions = {
      {-1,-1}, {0, -1}, {1, -1},
      {-1, 0}, {0, 0}, {1, 0},
      {-1, 1}, {0, 1}, {1, 1}
    };

    List<Way> resultantWays = new ArrayList<Way>();

    for(int[] direction : directions) {
      int x = hash % this.mapWidth + direction[0];
      int y = hash / this.mapWidth + direction[1];
      // Check if the hash is in bounds
      if(x < 0 && x >= this.mapWidth) {
        continue;
      }
      if(y < 0 && y >= this.mapHeight) {
        continue;
      }
      resultantWays.addAll(this.hashMap.get(hash - x - (y * this.mapWidth)));
    }
    return resultantWays;
  }
}