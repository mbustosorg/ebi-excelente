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

int textRows = 15;
int textSpacing = 40;
int numBalls = 25;
float spring = 1.0;
float gravity = 0.0;
float friction = -0.9;
Ball[] balls = new Ball[numBalls];
ArrayList<EntryText> entryText = new ArrayList<EntryText>();
int lastIdSeen = 0;
int headerWidth = 0;
int ebiIsWidth = 0;
int ebiIsExWidth = 0;
int Xheight = 80;
int TextHeight = 48;

color gold = color(232, 195, 0);
color green = color(0, 232, 87);
color[] ebiColors = { color(0, 195, 0), color(0, 0, 195), color(195, 195, 195) };
int[] tails = new int[textRows];

void setup() {
  size(displayWidth, displayHeight);
  for (int i = 0; i < numBalls; i++) {
    balls[i] = new Ball(random(width), random(height), random(30, 70), i, balls);
  }
  for (int i = 0; i < textRows; i++) {
    tails[i] = width;
  }
  noStroke();
  fill(255, 204);
     
  String user     = "ebiexcelente";
  String pass     = "tnnq656544gh";
  String database = "ebiexcelente";

  msql = new MySQL( this, "mysql.bustos.org", database, user, pass );
  updateEntries();
  
  textSize(TextHeight);
  headerWidth = int(textWidth("ebi is e"));
  ebiIsWidth = int(textWidth("ebi is e"));
  textSize(Xheight);
  headerWidth += textWidth("X");
  ebiIsExWidth = headerWidth;
  textSize(TextHeight);
  headerWidth += textWidth("cellent because ");
}

void updateEntries() {
  if ( msql.connect() )
  {
    msql.query( "SELECT * FROM Entry where id > " + str(lastIdSeen));
    if (!updatingEntries) {      
      updatingEntries = true;
      while (msql.next())
      {
        entryText.add(new EntryText(msql.getString("text")));
        lastIdSeen = max(lastIdSeen, msql.getInt("id"));
      }
      updatingEntries = false;
    }
  }
}

void update() {
  int size = entryText.size();
  for (int i = size - 1; i >= 0; i--) {
    if (entryText.get(i).offScreen()) {
      println("Resetting " + entryText.get(i).message);
      entryText.get(i).resetX();
    }
  }
}

void draw() {
  update();
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
  color currentColor;
  int row = 0;
  
  EntryText(String newMessage) {
    message = newMessage;
    textSize(TextHeight);
    textWidth = headerWidth + textWidth(message);
    row = int(random(15) + 1) - 1;
    y = int((row + 1) / 15.0 * height);
    resetX();
// Big gold X
// cycle through 3 colors for the rest of the text.  Green, blue and white
// Banner black on light grey banners behind each word
// Banner messages
//   ebi X
//   be excelenet to each other
//   sean excelentes unos con otros
// Banner of some sort//
  }
  
  void resetX() {
    currentColor = ebiColors[int(random(3))];
    x = max(tails[row] + textSpacing, int(width + random(width)));
    tails[row] = x + int(textWidth);
  }
  
  void update() {
    x -= 4;
  }

  boolean offScreen() {
    return x <= -textWidth - headerWidth;
  }
  
  void display() {
    textSize(TextHeight);
    fill(currentColor);
    text("ebi is e", x, y);
    textSize(Xheight);
    fill(gold);
    text("X", x + ebiIsWidth, y + 15);
    textSize(TextHeight);
    fill(currentColor);
    text("cellent because", x + ebiIsExWidth, y);
    text(message, x + headerWidth, y); 
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
    vx = random(0.1) - 0.1;
    vy = random(0.1) - 0.1;
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
    fill(40);
    ellipse(x, y, diameter, diameter);
  }
}

