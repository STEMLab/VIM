package lab.stem.vim.indoorGML;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class IndoorGmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        IndoorFeatures indoorFeatures = new IndoorFeatures();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case  XmlPullParser.START_TAG:
                        if (parser.getName().equals("core:PrimalSpaceFeatures")){
                            indoorFeatures.setPrimalSpaceFeatures(readPrimalFeatures(parser));
                        } else if (parser.getName().equals("core:MultiLayeredGraph")){
                            indoorFeatures.setMultiLayeredGraph(readMultiLayeredGraph(parser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }

        } finally {
            in.close();
        }
    }

    public void parse(InputStream in, IndoorFeatures indoorFeatures) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case  XmlPullParser.START_TAG:
                        if (parser.getName().equals("core:PrimalSpaceFeatures")){
                            indoorFeatures.setPrimalSpaceFeatures(readPrimalFeatures(parser));
                        } else if (parser.getName().equals("core:MultiLayeredGraph")){
                            indoorFeatures.setMultiLayeredGraph(readMultiLayeredGraph(parser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
//            Log.d("PARSER", String.valueOf(indoorFeatures.getMultiLayeredGraph().getSpaceLayerMap().keySet()));
        } finally {
            in.close();
        }
    }

    public MultiLayeredGraph readMultiLayeredGraph(XmlPullParser parser) throws XmlPullParserException, IOException  {
        parser.require(XmlPullParser.START_TAG, ns, "core:MultiLayeredGraph");
        MultiLayeredGraph multiLayeredGraph = new MultiLayeredGraph(parser.getAttributeValue(0));

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // Starts by looking for the entry tag
            switch (tagName) {
                case "core:spaceLayers":
                    multiLayeredGraph.setSpaceLayerMap(readSpaceLayers(parser));
                    break;
                case "core:interEdges":
                    multiLayeredGraph.setInterLayerConnectionMap(readInterEdges(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return multiLayeredGraph;
    }

    public Map<String, InterLayerConnection> readInterEdges(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:interEdges");
        Map<String, InterLayerConnection> interLayerConnectionMap = new HashMap<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:interLayerConnectionMember".equals(tagName)) {
                InterLayerConnection interLayerConnection = readInterLayerConnectionMember(parser);
                interLayerConnectionMap.put(interLayerConnection.getId(), interLayerConnection);
            } else {
                skip(parser);
            }
        }
        return interLayerConnectionMap;
    }

    public InterLayerConnection readInterLayerConnectionMember(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:interLayerConnectionMember");
        InterLayerConnection interLayerConnection = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:InterLayerConnection".equals(tagName)) {
                interLayerConnection = readInterLayerConnection(parser);
            } else {
                skip(parser);
            }
        }
        return interLayerConnection;
    }

    public InterLayerConnection readInterLayerConnection(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "core:InterLayerConnection");
        InterLayerConnection interLayerConnection = new InterLayerConnection(parser.getAttributeValue(0));

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "core:typeOfTopoExpression":
                    interLayerConnection.setTypeOfTopoExpression(readTagText(parser,"core:typeOfTopoExpression"));
                    break;
                case "core:interConnects":
                    interLayerConnection.getInterConnects().add(parser.getAttributeValue(0).replace("#",""));
                    parser.next();
                    break;
                case "core:ConnectedLayers":
                    interLayerConnection.getConnectedLayers().add(parser.getAttributeValue(0).replace("#",""));
                    parser.next();
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return interLayerConnection;
    }

    public Map<String, SpaceLayer> readSpaceLayers(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:spaceLayers");
        Map<String, SpaceLayer> spaceLayerMap = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            // Starts by looking for the entry tag
            if ("core:spaceLayerMember".equals(tagName)) {
                SpaceLayer spaceLayer = readSpaceLayerMember(parser);
                spaceLayerMap.put(spaceLayer.getId(), spaceLayer);
            } else {
                skip(parser);
            }
        }
        return spaceLayerMap;
    }

    public SpaceLayer readSpaceLayerMember(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:spaceLayerMember");
        SpaceLayer spaceLayer= null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:SpaceLayer".equals(tagName)) {
                spaceLayer = readSpaceLayer(parser);
            } else {
                skip(parser);
            }
        }
        return spaceLayer;
    }

//    public List<SpaceLayer> readSpaceLayerMember(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "core:spaceLayerMember");
//        List<SpaceLayer> spaceLayers = new ArrayList<>();
//        int i = 0;
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
//            String tagName = parser.getName();
//            switch (tagName) {
//                case "core:SpaceLayer":
//                    Log.d("parser", i + ": i");
//                    spaceLayers.add(readSpaceLayer(parser));
//                    i++;
//                    break;
//                default:
//                    skip(parser);
//                    break;
//            }
//        }
//        Log.d("parser", String.valueOf(spaceLayers.get(0).getId()));
//        return spaceLayers;
//    }

    public SpaceLayer readSpaceLayer (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:SpaceLayer");
        SpaceLayer spaceLayer = new SpaceLayer(parser.getAttributeValue(0));

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "core:nodes":
                    spaceLayer.setNode(readNodes(parser));
                    break;
                case "core:edges":
                    spaceLayer.setEdge(readEdges(parser));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return spaceLayer;
    }

    public Edge readEdges (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:edges");
        Edge edge = new Edge(parser.getAttributeValue(0));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:transitionMember".equals(tagName)) {
                Transition transition = readTransitionMember(parser);
                edge.getTransitionMap().put(transition.getId(), transition);
            } else {
                skip(parser);
            }
        }
        return edge;
    }

    public Transition readTransitionMember (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:transitionMember");
        Transition transition = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:Transition".equals(tagName)) {
                transition = readTransition(parser);
            } else {
                skip(parser);
            }
        }
        return transition;
    }

    public Transition readTransition (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:Transition");
        Transition transition = new Transition(parser.getAttributeValue(0));

        List<String> connectKeys = new ArrayList<>();
        List<Point> connectPoints = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "gml:description":
                    transition.setDescription(readTagText(parser, "gml:description"));
                    break;
                case "gml:name":
                    transition.setName(readTagText(parser, "gml:name"));
                    break;
                case "core:weight":
                    transition.setWeight(Double.parseDouble(readTagText(parser, "core:weight")));
                    parser.next();
                    break;
                case "core:connects":
                    connectKeys.add(parser.getAttributeValue(0).replace("#", ""));
                    parser.next();
                    break;
                case "core:geometry":
                    connectPoints = readGeometry(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        for (int i = 0; i < connectKeys.size(); i++) {
            transition.getConnectMap().put(connectKeys.get(i), connectPoints.get(i));
        }

        return transition;
    }

    public Node readNodes(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:nodes");
        Node node = new Node(parser.getAttributeValue(0));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:stateMember".equals(tagName)) {
                State state = readStateMember(parser);
                node.getStateMap().put(state.getId(), state);
            } else {
                skip(parser);
            }
        }
        return node;
    }

    public State readStateMember (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:stateMember");
        State state = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:State".equals(tagName)) {
                state = readState(parser);
            } else {
                skip(parser);
            }
        }
        return state;
    }

    public State readState (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:State");
        State state = new State(parser.getAttributeValue(0));

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "gml:description":
                    state.setDescription(readTagText(parser, "gml:description"));
                    break;
                case "gml:name":
                    state.setName(readTagText(parser, "gml:name"));
                    break;
                case "core:duality":
                    state.setDuality(parser.getAttributeValue(0).replace("#", ""));
                    parser.next();
                    break;
                case "core:connects":
                    state.getConnects().add(parser.getAttributeValue(0).replace("#", ""));
                    parser.next();
                    break;
                case "core:geometry":
                    state.setPoint(readGeometry(parser).get(0));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return state;
    }

    public List<Point> readGeometry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:geometry");
        List<Point> points = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "gml:Point":
                    points = new ArrayList<>();
                    points.add(readPoint(parser));
                    break;
                case "gml:LineString":
                    points = readLineString(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return points;
    }

    public Point readPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:Point");
        Point point = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("gml:pos".equals(tagName)) {
                point = new Point(readTagText(parser, "gml:pos"));
            } else {
                skip(parser);
            }
        }
        return point;
    }

    public List<Point> readLineString(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:LineString");
        List<Point> points = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();

            if ("gml:pos".equals(tagName)) {
                points.add(new Point(readTagText(parser, "gml:pos")));
            } else {
                skip(parser);
            }
        }
        return points;
    }

    public PrimalSpaceFeatures readPrimalFeatures(XmlPullParser parser) throws XmlPullParserException, IOException  {
        parser.require(XmlPullParser.START_TAG, ns, "core:PrimalSpaceFeatures");
        PrimalSpaceFeatures primalSpaceFeatures = new PrimalSpaceFeatures(parser.getAttributeValue(0));

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("core:cellSpaceMember")) {
                List<Object> cellSpaceMember = readCellSpaceMember(parser);
                primalSpaceFeatures.addCellSpace((String)cellSpaceMember.get(0), (CellSpace) cellSpaceMember.get(1));
//                Log.d(TAG, (String) cellSpaceMember.get(0));
            } else {
                skip(parser);
            }
        }
//        Log.d(TAG,primalSpaceFeatures.getCellSpaceMap().get("Stair4").getPolygon().getPointList().toString());
        return primalSpaceFeatures;
    }

    public List<Object> readCellSpaceMember(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:cellSpaceMember");
        List<Object> returnValueList = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if ("core:CellSpace".equals(tagName)) {
                returnValueList.add(parser.getAttributeValue(0));
                returnValueList.add(readCellSpace(parser));
            } else {
                skip(parser);
            }
        }

        return returnValueList;
    }

    public CellSpace readCellSpace(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "core:CellSpace");
        String id = parser.getAttributeValue(0);
        String description = null;
        String name = null;
        Polygon polygon = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case "gml:description":
                    description = readTagText(parser,tagName);
                    break;
                case "gml:name":
                    name = readTagText(parser,tagName);
                    break;
                case "core:cellSpaceGeometry":
                    Queue<String> path = new LinkedList<>();
                    path.add("core:Geometry3D");
                    path.add("gml:Solid");
                    path.add("gml:exterior");
                    path.add("gml:Shell");
                    path.add("gml:surfaceMember");
                    path.add("gml:Polygon");
                    path.add("gml:exterior");
                    path.add("gml:LinearRing");
                    List<Point> points = new ArrayList<>();
                    readLinearRing(parser, path, points);
                    polygon = new Polygon(points);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

//        Log.d(TAG, polygon.getPointList().toString());
        return new CellSpace(id, description, name, polygon);
    }

    private String readTagText(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return text;
    }
    
    private void readPolygon(XmlPullParser parser, List<Point> points) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:LinearRing");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals("gml:pos")){
                String[] pos = readTagText(parser, parser.getName()).split(" ");
                double x = Double.parseDouble(pos[0]);
                double y = Double.parseDouble(pos[1]);
                double z = Double.parseDouble(pos[2]);
                points.add(new Point(x, y, z));
            }
        }
    }

    private void readLinearRing(XmlPullParser parser, Queue<String> path, List<Point> points) throws IOException, XmlPullParserException {
        if (path.size() > 0) {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String tagName = path.poll();
                if (parser.getName().equals(tagName)){
                    readLinearRing(parser, path, points);
                }else {
                    skip(parser);
                }
            }
        } else {
            readPolygon(parser,points);
        }
    }

//    private Polygon readLinearRing(XmlPullParser parser, Queue<String> path) throws IOException, XmlPullParserException {
//        Polygon polygon = null;
//        while (path.size() > 0) {
//            while (parser.next() != XmlPullParser.END_TAG) {
//                if (parser.getEventType() != XmlPullParser.START_TAG) {
//                    continue;
//                }
//                String tagName = path.poll();
//                if (path.size() == 0 && parser.getName().equals(tagName)){
//                    polygon = readPolygon(parser);
//                }  else  {
//                    skip(parser);
//                }
//                Log.d(TAG,path.size() + "hello");
//            }
//
//        }
//        Log.d(TAG,parser.getName() + "hello");
//        return polygon;
//    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}