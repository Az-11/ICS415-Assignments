// BezierSketch.java
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

public class BezierSketch extends JPanel
    implements MouseListener, MouseMotionListener
{
    private static final int R = 10;       // half‐size of square handle
    private static final int STEPS = 120;
    static class CP { int x,y; Color c; CP(int x,int y,Color c){this.x=x;this.y=y;this.c=c;} }

    private final List<CP> quad  = new ArrayList<>();
    private final List<CP> cubic = new ArrayList<>();
    private final List<CP> quint = new ArrayList<>();
    private CP hover=null, drag=null;

    public BezierSketch(){
        setBackground(new Color(34, 34, 34));
        // demo control points
        quad .add(new CP(100,150, Color.CYAN));
        quad .add(new CP(250, 80, Color.CYAN));
        quad .add(new CP(400,150, Color.CYAN));
        cubic.add(new CP(100,350, Color.ORANGE));
        cubic.add(new CP(250,280, Color.ORANGE));
        cubic.add(new CP(400,420, Color.ORANGE));
        cubic.add(new CP(550,350, Color.ORANGE));
        quint.add(new CP(100,550, Color.MAGENTA));
        quint.add(new CP(200,480, Color.MAGENTA));
        quint.add(new CP(300,620, Color.MAGENTA));
        quint.add(new CP(400,480, Color.MAGENTA));
        quint.add(new CP(500,550, Color.MAGENTA));
        quint.add(new CP(600,620, Color.MAGENTA));

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override protected void paintComponent(Graphics g0){
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D)g0;
        // anti-aliasing + dashed helper grid
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10f, new float[]{8f,6f},0f);
        g.setStroke(dashed);
        g.setColor(new Color(80,80,80));
        for(int y=0;y<getHeight();y+=40) g.drawLine(0,y,getWidth(),y);
        for(int x=0;x<getWidth();x+=40) g.drawLine(x,0,x,getHeight());

        // draw curves and handles
        drawCurve(g, quad, 3);
        drawCurve(g, cubic,4);
        drawCurve(g, quint,6);
        drawHandles(g, quad);
        drawHandles(g, cubic);
        drawHandles(g, quint);
    }

    private void drawCurve(Graphics2D g, List<CP> pts, int order){
        if(pts.size()<order) return;
        Stroke solid = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g.setStroke(solid);
        Point prev = deCasteljau(pts,0f);
        for(int i=1;i<=STEPS;i++){
            float t = i/(float)STEPS;
            Point curr = deCasteljau(pts,t);
            g.setColor(pts.get(0).c);
            g.drawLine(prev.x,prev.y,curr.x,curr.y);
            prev = curr;
        }
    }

    private Point deCasteljau(List<CP> pts, float t){
        int n=pts.size();
        double[] xs=new double[n], ys=new double[n];
        for(int i=0;i<n;i++){ xs[i]=pts.get(i).x; ys[i]=pts.get(i).y; }
        for(int r=1;r<n;r++){
            for(int i=0;i<n-r;i++){
                xs[i] = xs[i]*(1-t) + xs[i+1]*t;
                ys[i] = ys[i]*(1-t) + ys[i+1]*t;
            }
        }
        return new Point((int)xs[0],(int)ys[0]);
    }

    private void drawHandles(Graphics2D g, List<CP> pts){
        for(CP p:pts){
            // highlight hovered
            if(p==hover){
                g.setColor(Color.YELLOW);
                g.fillRect(p.x-R-2,p.y-R-2,2*R+4,2*R+4);
            }
            g.setColor(p.c.brighter());
            g.fillRect(p.x-R,p.y-R,2*R,2*R);
            g.setColor(p.c.darker());
            g.drawRect(p.x-R,p.y-R,2*R,2*R);
        }
    }

    @Override public void mousePressed(MouseEvent e){
        for(List<CP> list:Arrays.asList(quad,cubic,quint)){
            for(CP p:list){
                if(Math.hypot(e.getX()-p.x,e.getY()-p.y) < R*1.5){
                    drag=p; return;
                }
            }
        }
    }
    @Override public void mouseReleased(MouseEvent e){ drag=null; }
    @Override public void mouseDragged(MouseEvent e){
        if(drag!=null){
            drag.x=e.getX(); drag.y=e.getY();
            repaint();
        }
    }
    @Override public void mouseMoved(MouseEvent e){
        hover=null;
        for(List<CP> list:Arrays.asList(quad,cubic,quint)){
            for(CP p:list){
                if(Math.hypot(e.getX()-p.x,e.getY()-p.y) < R*1.5){
                    hover=p; break;
                }
            }
            if(hover!=null) break;
        }
        setCursor(hover!=null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                              : Cursor.getDefaultCursor());
        repaint();
    }

    // unused
    @Override public void mouseClicked(MouseEvent e){}
    @Override public void mouseEntered(MouseEvent e){}
    @Override public void mouseExited(MouseEvent e){}
}
