// || Swami-Shriji ||
package addbuildings;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.io.*;  
import java.util.Random;
import java.awt.geom.Point2D;
import java.util.Set;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.Tag;

import org.locationtech.jts.geom.Coordinate;

class Building {

    private Node center;
    private float width;
    private float height;
    private float angle;
    private Way geometry;


    public Building(Node center, float width, float height, float angle) {


    }

    public void translate(float x, float y) {

    }

    public void translate(float x, float y, float transformationAngle) {
        
    }

    public void rotate(float rotationAngle) {
        angle += rotationAngle;
    }

    public Way getWay() {

        return geometry;
    }
}