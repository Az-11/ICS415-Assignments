// PolyBezierEditor.java
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PolyBezierEditor extends JPanel
    implements MouseListener, MouseMotionListener
{
    private static final int R = 8;
    private static final int STEPS = 80;
    private final List<Point> pts = new ArrayList<>();
    private Point hover=null, drag=null;

    public PolyBezierEditor(){
        // Gradient background is handled in the GradientPaintBackground class
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override protected void paintComponent(Graphics g0){
        super.paintComponent(g0);
        Graphics2D g=(Graphics2D)g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw chained cubics
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for(int i=0; i+3<pts.size(); i+=3){
            List<Point> seg = pts.subList(i,i+4);
            Point prev=deCasteljau(seg,0f);
            for(int k=1;k<=STEPS;k++){
                float t=k/(float)STEPS;
                Point cur=deCasteljau(seg,t);
                g.setColor(new Color(255,255,255,180));
                g.drawLine(prev.x,prev.y,cur.x,cur.y);
                prev=cur;
            }
        }

        // draw control points as triangles
        for(Point p:pts){
            if(p==hover){
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.WHITE);
            }
            Polygon tri=new Polygon(
                new int[]{p.x, p.x-R, p.x+R},
                new int[]{p.y-R, p.y+R, p.y+R},
                3
            );
            g.fill(tri);
            g.setColor(Color.DARK_GRAY);
            g.draw(tri);
        }
    }

    private Point deCasteljau(List<Point> P, float t){
        int n=P.size();
        double[] xs=new double[n], ys=new double[n];
        for(int i=0;i<n;i++){ xs[i]=P.get(i).x; ys[i]=P.get(i).y; }
        for(int r=1;r<n;r++){
            for(int i=0;i<n-r;i++){
                xs[i] = xs[i]*(1-t) + xs[i+1]*t;
                ys[i] = ys[i]*(1-t) + ys[i+1]*t;
            }
        }
        return new Point((int)xs[0], (int)ys[0]);
    }

    @Override public void mouseClicked(MouseEvent e){
        pts.add(new Point(e.getX(), e.getY()));
        repaint();
    }
    @Override public void mousePressed(MouseEvent e){
        hover=findHandle(e.getX(), e.getY());
        drag=hover;
    }
    @Override public void mouseReleased(MouseEvent e){
        drag=null;
    }
    @Override public void mouseDragged(MouseEvent e){
        if(drag!=null){
            drag.setLocation(e.getX(), e.getY());
            repaint();
        }
    }
    @Override public void mouseMoved(MouseEvent e){
        hover=findHandle(e.getX(), e.getY());
        setCursor(hover!=null
            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            : Cursor.getDefaultCursor());
        repaint();
    }

    private Point findHandle(int x,int y){
        for(Point p:pts){
            if(p.distance(x,y) < R*1.2) return p;
        }
        return null;
    }

    // unused
    @Override public void mouseEntered(MouseEvent e){}
    @Override public void mouseExited(MouseEvent e){}
}

// small helper for gradient background
class GradientPaintBackground extends JPanel {
    @Override protected void paintComponent(Graphics g0){
        super.paintComponent(g0);
        Graphics2D g=(Graphics2D)g0;
        GradientPaint gp=new GradientPaint(
            0,0,new Color(20,20,40),
            0,getHeight(), new Color(60,20,60)
        );
        g.setPaint(gp);
        g.fillRect(0,0,getWidth(),getHeight());
    }
}
