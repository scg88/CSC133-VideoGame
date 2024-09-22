import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class Game extends Pane { //container for all game objects

    private boolean win = false;
    private boolean gameOver = false;

    public void setGameOver(){
        gameOver = true;
    }

    public boolean getGameOver(){
        return gameOver;
    }

    public void setWin() {
        win = true;
    }

    public boolean getWin() {
        return win;
    }

    public Label displayWin(int score, int pondRadius) {
        Label winLabel = new Label();
        winLabel.setText("      Win!\nScore:" + score * pondRadius/10);
        winLabel.setTextFill(Color.ORANGE);
        winLabel.setFont(Font.font(30));
        winLabel.setTranslateX(350);
        winLabel.setTranslateY(100);

        return winLabel;
    }

    public Label notifyWin() {
        Label winLabel = new Label();
        winLabel.setText("The Pond has been restored!\nReturn to the HeliPad to complete the mission");
        winLabel.setTextFill(Color.BLACK);
        winLabel.setFont(Font.font(20));
        winLabel.setTranslateX(3);
        winLabel.setTranslateY(20);

        return winLabel;
    }

    public Label notifyGameOver() {
        Label winLabel = new Label();
        winLabel.setText("Game Over!\nYou ran out of fuel :(");
        winLabel.setTextFill(Color.BLACK);
        winLabel.setFont(Font.font(20));
        winLabel.setTranslateX(3);
        winLabel.setTranslateY(80);

        return winLabel;
    }
}

interface Updatable {
    void update();
}

abstract class GameObject extends Group  {

    final static double TAU = Math.PI * 2;

    protected Translate myTranslation;
    protected Rotate myRotation;
    protected Scale myScale;

    public GameObject() {

        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation, myRotation, myScale);
    }

    public void rotate(double degrees){
        myRotation.setAngle(degrees);
        myRotation.setPivotX(0);
        myRotation.setPivotY(0);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }

    public void translate(double tx, double ty){
        myTranslation.setX(tx);
        myTranslation.setY(ty);
    }

    public double getMyRotation(){
        return myRotation.getAngle();
    }

    public void  update(double delta){
        for(Node n : getChildren()){
            if(n instanceof Updatable)
                ((Updatable)n).update();
        }
    }

    void add (Node node) {
        this.getChildren().add(node);
    }

    static double rand(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    static Point2D vecAngle(double angle, double mag) {
        return new Point2D(Math.cos(angle), Math.sin(angle)).multiply(mag);
    }

}//END GAME OBJECT


class Pond extends GameObject {

    Ellipse pond = new Ellipse(20,20);
    Label pondLabel = new Label();
    int pondPercent = 20;

    public void setRadius(double x){
        pond.setRadiusX(x);
        pond.setRadiusY(x);
        pondPercent = (int) pond.getRadiusX();
        pondLabel.setText(pondPercent + "%");
    }

    public int getRadius(){
        return (int) pond.getRadiusX();
    }
    public Pond(Group parent, Point2D p){
        super();
        pond.setFill(Color.BLUE);

        pond.setCenterX(rand(100,800));
        pond.setCenterY(rand(100,650));
        pondLabel.setTextFill(Color.WHITE);
        pondLabel.setText(pondPercent + "%");
        pondLabel.setTranslateY(pond.getCenterY()- 10);
        pondLabel.setTranslateX(pond.getCenterX() - 15);

        add(pond);
        add(pondLabel);
    }

}

class Cloud extends GameObject {

    Ellipse cloud = new Ellipse(30,30);
    Label cloudLabel = new Label();
    int cloudPercent = 0;
    int grayScale = 250;
    double windSpeed = 0.2;

    public int getCloudPercent()
    {
        return cloudPercent;
    }

    public void setCloudPercent(int pondPercent)
    {
        cloudPercent = pondPercent - 20;
        grayScale = 250 - ((pondPercent - 20) * 2);
        cloudLabel.setText(String.format("%d%%", cloudPercent));
        cloud.setFill(Color.rgb(grayScale,grayScale,grayScale));
    }

    public void moveCloud()
    {
        cloud.setCenterX(cloud.getCenterX() + windSpeed);
        cloudLabel.setTranslateX(cloud.getCenterX() - 10);

        if (cloud.getCenterX() > 850)
        {
            cloud.setCenterX(rand(0,0));
            cloud.setCenterY(rand(0,650));
            cloudLabel.setTranslateY(cloud.getCenterY()- 10);
            cloudLabel.setTranslateX(cloud.getCenterX() - 10);
        }
    }


    public Cloud(Group parent, Point2D p){
        super();
        cloud.setCenterX(rand(0,0));
        cloud.setCenterY(rand(0,650));
        cloud.setFill(Color.WHITE);

        cloudLabel.setTextFill(Color.BLUE);
        cloudLabel.setText(String.format("%d%%", cloudPercent));
        cloudLabel.setTranslateY(cloud.getCenterY()- 10);
        cloudLabel.setTranslateX(cloud.getCenterX() - 10);

        add(cloud);
        add(cloudLabel);
    }

}

class HeliPad extends GameObject{

    public HeliPad(Group parent, Point2D p){
        super();
        Rectangle platform = new Rectangle(80,80);
        Ellipse helipad = new Ellipse(35,35);

        platform.setStrokeWidth(3);
        platform.setStroke(Color.BLACK);
        platform.setFill(Color.TRANSPARENT);
        helipad.setFill(Color.GRAY);

        platform.setX(360);
        platform.setY(660);
        helipad.setCenterX(400);
        helipad.setCenterY(700);

        add(platform);
        add(helipad);
    }

}
class Helicopter extends GameObject {

    private int fuel = 15000;
    private double rot = 0;
    Label fuelLabel = new Label();
    Rotate rotate = new Rotate();


    abstract class AbstractHeliState
    {

    }
    class OffState extends AbstractHeliState
    {

    }
    class StartingState extends AbstractHeliState
    {

    }
    class ReadyState extends AbstractHeliState
    {

    }
    class StoppingState extends AbstractHeliState
    {

    }

    AbstractHeliState heliState;


    public void setFuel() {

        if (fuel > 0) {
            fuel = fuel - 3;
            rot = rot + 10;}

        fuelLabel.setText(String.valueOf(fuel));
        rotate.setAngle(rot);
    }

    public int getFuel(){
        return fuel;
    }
    public Helicopter(Group parent, Point2D p) throws FileNotFoundException {
        super();

        Image image = new Image(new FileInputStream("src\\assets\\Helicopter.png"));
        ImageView imageView = new ImageView(image);
        imageView.setX(375);
        imageView.setY(663);
        imageView.setFitHeight(90);
        imageView.setFitWidth(50);

        Line rotor = new Line();
        rotor.setStrokeWidth(3);
        rotor.setStroke(Color.BLACK);
        rotor.setStartX(370);
        rotor.setStartY(700);
        rotor.setEndX(430);
        rotor.setEndY(700);

        fuelLabel.setText(String.valueOf(fuel));
        fuelLabel.setTextFill(Color.YELLOW);
        fuelLabel.setTranslateX(imageView.getX() + 5);
        fuelLabel.setTranslateY(750);

        rotate.setAngle(0);
        rotate.setPivotX(400);
        rotate.setPivotY(700);
        rotor.getTransforms().add(rotate);

        add(fuelLabel);
        add(imageView);
        add(rotor);
    }

}

public class GameApp extends Application {

    Point2D size = new Point2D(800, 800);
    Set<KeyCode> keysDown = new HashSet<>();
    int key(KeyCode k) {
        return keysDown.contains(k) ? 1 : 0;
    }
    Point2D p = new Point2D(200,700);
    double theta = 1;

    public void start(Stage stage) throws Exception{

        Pane root = new Pane();
        Scene scene = new Scene(root, size.getX(), size.getY());

        FileInputStream input = new FileInputStream("src\\assets\\Background.png");
        Image image = new Image(input);
        BackgroundImage mapImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background = new Background(mapImage);
        root.setBackground(background);

        stage.setScene(scene);
        stage.setTitle("RainMaker");
        stage.setResizable(false);
        root.setScaleY(1);
        root.setTranslateX(0);
        root.setTranslateY(0);


        Label fpsLabel = new Label();
        fpsLabel.setTranslateX(2);
        fpsLabel.setTextFill(Color.WHITE);

        Group gGame = new Group();
        Group gClouds = new Group();

        List<Cloud> clouds = new LinkedList<>();
        gGame.getChildren().addAll(clouds);

        Cloud cloud = new Cloud(gGame, size);
        Pond pond = new Pond(gGame, size);
        Pond pond2 = new Pond(gGame, size);
        Pond pond3 = new Pond(gGame, size);
        HeliPad heliPad = new HeliPad(gGame, size);
        Helicopter helicopter = new Helicopter(gGame, size);
        Game game = new Game();
        Rotate rotate = new Rotate();
        rotate.setPivotX(400);
        rotate.setPivotY(700);

        clouds.add(new Cloud(gGame, size));
        clouds.add(new Cloud(gGame, size));
        clouds.add(cloud);

        root.getChildren().addAll(fpsLabel, pond, cloud, heliPad, pond2, pond3, helicopter, gGame);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysDown.add(event.getCode());
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysDown.remove(event.getCode());
            }
        });

        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            double outputTimer = 0;
            double moveTimer = 0;
            double rotTimer = 0;
            double moveX = 0;
            double moveY = 0;
            double theta = 0;
            double pondRadius = 20;

            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9; //// Time for 1 frame (new-time - old-time) * 1 ns = seconds
                old = nano;

                elapsedTime += delta;
                outputTimer += delta;
                moveTimer += delta;
                rotTimer += delta;

                if (!helicopter.getBoundsInParent().intersects(heliPad.getBoundsInLocal()))
                {
                    helicopter.setFuel();
                }

                if (key(KeyCode.LEFT) == 1 && rotTimer >= .2
                        && !game.getGameOver())
                {
                    helicopter.getTransforms().clear();
                    helicopter.getTransforms().addAll(new Translate(moveX,moveY), rotate);
                    theta -= 15;
                    rotTimer = 0;
                    rotate.setAngle(theta);
                }

                if (key(KeyCode.RIGHT) == 1 && rotTimer >= .2
                        && !game.getGameOver())
                {
                    helicopter.getTransforms().clear();
                    helicopter.getTransforms().addAll(new Translate(moveX,moveY), rotate);
                    theta += 15;
                    rotTimer = 0;
                    rotate.setAngle(theta);
                }

                if (key(KeyCode.UP) == 1 && moveTimer >= .025
                        && !game.getGameOver()){

                    moveX += 2 * Math.sin(Math.toRadians(theta));
                    moveY -= 2 * Math.cos(Math.toRadians(theta));
                    helicopter.getTransforms().clear();
                    helicopter.getTransforms().addAll(new Translate(moveX,moveY), rotate);
                    moveTimer = 0;
                }

                if (key(KeyCode.DOWN) == 1 && moveTimer >= .025
                        && !game.getGameOver()) {

                    moveX -= Math.sin(Math.toRadians(theta));
                    moveY += Math.cos(Math.toRadians(theta));
                    helicopter.getTransforms().clear();
                    helicopter.getTransforms().addAll(new Translate(moveX, moveY), rotate);

                    moveTimer = 0;
                }

                if(outputTimer >= .1) {
                    fpsLabel.setText(String.format("Time: %.2f  FPS %.2f", elapsedTime, 1/delta));
                    System.out.println(keysDown);
                    System.out.println(theta);
                    outputTimer = 0;
                }

                if (helicopter.getBoundsInParent().intersects(cloud.getBoundsInLocal())
                        && key(KeyCode.SPACE) == 1
                        && cloud.getCloudPercent() < 101)
                {
                    pondRadius += .045;
                    pond.setRadius(pondRadius);
                    pond.getRadius();
                    cloud.setCloudPercent(pond.getRadius());
                }

                if (pondRadius > 21 && pondRadius < 80)
                {
                    pondRadius -= .02;
                    pond.setRadius(pondRadius);
                    cloud.setCloudPercent(pond.getRadius());
                }

                if (pondRadius >= 80 )
                {
                    game.setWin();
                    root.getChildren().add(game.notifyWin());
                }

                if(game.getWin() == true
                        && helicopter.getBoundsInParent().intersects(heliPad.getBoundsInLocal()))
                {
                    root.getChildren().add(game.displayWin(helicopter.getFuel(), (int) pondRadius));
                    game.setGameOver();
                }

                if(helicopter.getFuel() == 0){
                    root.getChildren().add(game.notifyGameOver());
                    game.setGameOver();
                }

                if(game.getGameOver() == false)
                {
                    cloud.moveCloud();
                }


            }

        };
        loop.start();
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}