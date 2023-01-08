package client;
import client.particle.GhostParticle;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

class GhostE extends Status{
  private ArrayList<GhostParticle> particles = new ArrayList<GhostParticle>();
  private int x,y;
  private static int RADIUS = 25;
  GhostE(int x, int y){
    this.x = x;
    this.y = y;
  }
  
  public void draw(Graphics2D g2, int playerX, int playerY, int index){
    particles.add(new GhostParticle(x + getXyAdjust()[0], y + getXyAdjust()[1], (int) ((Math.random() * 5 + 5))));
    
    //Draws particles
    for (int i = 0; i < particles.size(); i++) {
      try {
        if (particles.get(i).update()) {
          particles.remove(i);
        } else {
          particles.get(i).render(g2);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}