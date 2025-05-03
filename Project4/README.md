# Project 4: Curves

A visual Bézier curve editor with support for:
- Quadratic Bézier curves
- Cubic Bézier curves
- Poly Bézier chains (bonus)

## Features

- Interactive control‐point handles (drag to reshape)
- Dashed helper grid for guidance
- Color-coded curves and handles
- Two modes (Basic vs. Bonus) via tabs

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- A terminal or command-line interface

## Project Structure

Project4/
├─ Main.java
├─ BezierSketch.java
└─ PolyBezierEditor.java


## Running the Application

1. **Open a terminal** and `cd` into the project folder:
   ```bash
   cd ICS415-Assignments/Project4

2. **Compile all Java sources:** 
    javac Main.java BezierSketch.java PolyBezierEditor.java

3. **Launch the editor:**
    java Main

4. **In the GUI**, switch between:
    - Basic Bézier tab (quadratic & cubic)
    - Poly Bézier (Bonus) tab (chained cubic segments)

