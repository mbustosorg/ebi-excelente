/*

    Copyright (C) 2016 Mauricio Bustos (m@bustos.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

import de.bezier.data.sql.*;
import java.util.Date;

MySQL msql;
int lastRefresh = millis();
boolean updatingEntries = false;

int numBalls = 45;
float spring = 1.45;
float gravity = 0.01;
float friction = -0.4;
Ball[] balls = new Ball[numBalls];
ArrayList<EntryText> entryText = new ArrayList<EntryText>();

void setup() {
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

void updateEntries() {
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

void draw() {
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
    y = int(random(height));
    x = int(width + random(width));
    red = int(random(200) + 50);
    green = int(random(200) + 50);
    blue = int(random(200) + 50);
  }
  
  void update() {
    x -= 1;
  }

  boolean offScreen() {
    return x <= -textWidth;
  }
  
  void display() {
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
  
  void collide() {
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
  
  void move() {
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
  
  void display() {
    fill(100);
    ellipse(x, y, diameter, diameter);
  }
}

