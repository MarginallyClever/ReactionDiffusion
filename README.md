## Reaction Diffusion

![Screenshot 2026-06-11 120957.png](Screenshot%202026-06-11%20120957.png)

This is a Java maven project that implements a reaction diffusion system in Java.

It has a simple GUI to visualize the reaction diffusion process and control settings.

Resizing the window will restart the process.

## Running

Execute `Main.main()` to start the app.  Maven will package a fat jar that can be executed with `java -jar target/reaction-diffusion-1.0.jar`.

Mouse left click+drag will paint in the grid.

Right click will report to the console the current state of the grid at the mouse position.

Use `File > Starter` or `File > Load` to load an image in the top left corner as a reaction starting point.


## Threading

The reaction diffusion process runs better in a multi-core CPU, as the process is parallelized.  If you have a single core CPU, you may want to reduce the number of iterations per frame to improve responsiveness.

If the app is not responding well, try this:  locate `gui.OutputPanel.paintComponent()`
and change

```java
for(int i=0;i<10;++i) {
    model.performReactionDiffusion();
}
```
by replacing 10 with a smaller number.  The animation is running 10 iterations per frame, and a lower number will slow
the process down, but it will be more responsive.

## Colors

All the color choices are made in `View.updateImage()`.  Try replacing `rainbow()` with something else.

## See also

https://www.karlsims.com/rd.html
https://github.com/jasonwebb/morphogenesis-resources#reaction-diffusion