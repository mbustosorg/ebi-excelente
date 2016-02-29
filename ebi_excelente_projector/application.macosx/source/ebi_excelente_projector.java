import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.bezier.data.sql.*; 
import java.util.Date; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ebi_excelente_projector extends PApplet {




MySQL msql;
int lastRefresh = millis();
boolean updatingEntries = false;

int numBalls = 45;
float spring = 1.45f;
float gravity = 0.01f;
float friction = -0.4f;
Ball[] balls = new Ball[numBalls];
ArrayList<EntryText> entryText = new ArrayList<EntryText>();

public void setup() {
  size(displayWidth, displayHeight);
  for (int i = 0; i < numBalls; i++) {
    balls[i] = new Ball(random(width), random(height), random(30, 70), i, balls);
  }
  noStroke();
  fill(255, 204);
     
  String user     = "ebiexcelente";
  String pass     = "tnnq656544gh";
  String database = "ebiexcelente";

  msql = new MySQL( this, "mysql.bustos.org", database, user, pass );
  updateEntries();
}

public void updateEntries() {
  if ( msql.connect() )
  {
    msql.query( "SELECT * FROM Entry" );
    if (!updatingEntries) {      
      updatingEntries = true;
      int size = entryText.size();
      for (int i = size - 1; i >= 0; i--) {
        if (entryText.get(i).offScreen()) {
          println("Removing");
          entryText.remove(i);
        }
      }
      while (msql.next())
      {
        entryText.add(new EntryText(msql.getString("text")));
      }
      updatingEntries = false;
    }
  }
}

public void draw() {
  background(0);
  if (millis() > lastRefresh + 30000) {
    lastRefresh = millis();
    thread("updateEntries");
  }
  for (Ball ball : balls) {
    ball.collide();
    ball.move();
    ball.display();  
  }
  if (!updatingEntries) {
    updatingEntries = true;
    for (EntryText text : entryText) {
      text.update();
      text.display();
    }
    updatingEntries = false;
  }
}

class EntryText {

  String message;
  float textWidth;
  int index;
  int x = 0;
  int y = 0;
  int red = 0;
  int green = 0;
  int blue = 0;
  
  EntryText(String newMessage) {
    message = newMessage;
    textWidth = textWidth(message);
    y = PApplet.parseInt(random(height));
    x = PApplet.parseInt(width + random(width));
    red = PApplet.parseInt(random(200) + 50);
    green = PApplet.parseInt(random(200) + 50);
    blue = PApplet.parseInt(random(200) + 50);
  }
  
  public void update() {
    x -= 1;
  }

  public boolean offScreen() {
    return x <= -textWidth;
  }
  
  public void display() {
    textSize(48);
    fill(red, green, blue);
    text(message, x, y); 
  }
  
}

class Ball {
  
  float x, y;
  float diameter;
  float vx = 0;
  float vy = 0;
  int id;
  Ball[] others;
 
  Ball(float xin, float yin, float din, int idin, Ball[] oin) {
    x = xin;
    y = yin;
    diameter = din;
    id = idin;
    others = oin;
  } 
  
  public void collide() {
    for (int i = id + 1; i < numBalls; i++) {
      float dx = others[i].x - x;
      float dy = others[i].y - y;
      float distance = sqrt(dx*dx + dy*dy);
      float minDist = others[i].diameter/2 + diameter/2;
      if (distance < minDist) { 
        float angle = atan2(dy, dx);
        float targetX = x + cos(angle) * minDist;
        float targetY = y + sin(angle) * minDist;
        float ax = (targetX - others[i].x) * spring;
        float ay = (targetY - others[i].y) * spring;
        vx -= ax;
        vy -= ay;
        others[i].vx += ax;
        others[i].vy += ay;
      }
    }   
  }
  
  public void move() {
    vy += gravity;
    x += vx;
    y += vy;
    if (x + diameter/2 > width) {
      x = width - diameter/2;
      vx *= friction; 
    }
    else if (x - diameter/2 < 0) {
      x = diameter/2;
      vx *= friction;
    }
    if (y + diameter/2 > height) {
      y = height - diameter/2;
      vy *= friction; 
    } 
    else if (y - diameter/2 < 0) {
      y = diameter/2;
      vy *= friction;
    }
  }
  
  public void display() {
    fill(100);
    ellipse(x, y, diameter, diameter);
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "ebi_excelente_projector" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
