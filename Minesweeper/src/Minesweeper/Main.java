package Minesweeper;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage theStage) {
		// difficulty base numbers
		int boardHeight = 8;
		int boardWidth = 8;
		int mineCountAtStart = 10;
		// Pane pain
		BorderPane theBorderPane = new BorderPane();
		BorderPane header = new BorderPane();
		BorderPane faceBorder = new BorderPane();

		HBox bombCounter = new HBox();
		BorderPane bombBorder = new BorderPane();
		bombBorder.setCenter(bombCounter);

		header.setLeft(bombBorder);

		HBox timer = new HBox();
		BorderPane timerBorder = new BorderPane();
		timerBorder.setCenter(timer);

		header.setRight(timerBorder);

		Pane dividerPane = new Pane();
		dividerPane.setStyle("-fx-background-color: #D3D3D3;");
		dividerPane.setMinSize(10, 10);

		GridPane theGridPane = new GridPane();
		ButtonClick[][] buttons = new ButtonClick[boardHeight][boardWidth];

		FaceClick face = new FaceClick(0);

		// face button settings
		face.setOnMouseClicked(e -> {
			MouseButton button = e.getButton();
			System.out.println("Button was " + button);
			System.out.println(face.state);
			start(theStage);
		});

		faceBorder.setCenter(face);

		header.setCenter(faceBorder);
		header.setBottom(dividerPane);
		theBorderPane.setBorder(
				new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, null, new BorderWidths(10))));

		header.setStyle("-fx-background-color: #D3D3D3;");
		theBorderPane.setTop(header);
		theBorderPane.setCenter(theGridPane);

		for (int y = 0; y < buttons.length; y++) {
			for (int x = 0; x < buttons[0].length; x++) {
				buttons[y][x] = new ButtonClick();
				ButtonClick b = buttons[y][x];
				b.y = y;
				b.x = x;
				b.setOnMouseClicked(e -> {
					MouseButton button = e.getButton();
					System.out.println("Button was " + button);
					if (button.toString().equals("PRIMARY")) {
						System.out.println(b.state);

						/* attempting to reveal numbers based on flags
						if (b.subState < 1 && b.clicked) {
							numberClick(buttons, b.y, b.x);
						}
						*/
						if (b.subState < 1 && !b.clicked) {
							if (!b.boardGenerated) {
								generateBoard(buttons, b.y, b.x);
								recursiveBoardReveal(buttons, b.y, b.x);
							}
							
							switch (b.state) {
							case 9:
								b.setGraphic(b.imageMineRed);
								face.setGraphic(face.faceDead);
								b.clicked = true;
								theGridPane.setMouseTransparent(true);
								theGridPane.setFocusTraversable(false);
								lostGameResolution(buttons);
								break;
							default:
								recursiveBoardReveal(buttons, b.y, b.x);
								b.numberClicked();
								if (Won(buttons)) {
									face.setGraphic(face.faceWin);
									theGridPane.setMouseTransparent(true);
									theGridPane.setFocusTraversable(false);
								}
								break;
							}
						}

					} else {
						b.subState++;
						b.subState %= 2;
						System.out.println(b.subState);
						if (!b.clicked) {
							switch (b.subState) {
							case 0:
								b.setGraphic(b.imageStart);
								bombBorder.setCenter(imageCounter(knownMineCount(buttons)));
								break;
							case 1:
								b.setGraphic(b.imageFlag);
								bombBorder.setCenter(imageCounter(knownMineCount(buttons)));
								break;
							}
						}
					}
				});

				b.setOnMousePressed(e -> {
					face.setGraphic(face.faceO);
				});

				b.setOnMouseReleased(e -> {
					face.setGraphic(face.faceSmile);
				});

				theGridPane.add(b, x, y);
			}
		}

		// generateBoard(buttons);
		bombBorder.setCenter(imageCounter(mineCountAtStart));
		timerBorder.setCenter(imageCounter(000));

		theStage.setScene(new Scene(theBorderPane));
		theStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public void recursiveBoardReveal(ButtonClick[][] buttons, int yClick, int xClick) {
		//System.out.println(yClick + ", " + xClick);
		//System.out.println("recurse start");
		if (yClick < 0 || xClick < 0 || yClick >= buttons.length || xClick >= buttons.length)
			return;

		if (buttons[yClick][xClick].state == 0 && !buttons[yClick][xClick].clicked) {
			for (int y = yClick - 1; y <= yClick + 1; y++) {
				for (int x = xClick - 1; x <= xClick + 1; x++) {
					buttons[yClick][xClick].numberClicked();
					recursiveBoardReveal(buttons, y, x);
				}
			}
		}

		buttons[yClick][xClick].numberClicked();
		return;
	}

	public void lostGameResolution(ButtonClick[][] buttons) {
		
		for (int y = 0; y < buttons.length; y++) {
			for (int x = 0; x < buttons[0].length; x++) {
				ButtonClick b = buttons[y][x];
				if (b.state == 9 && b.subState == 0 && !b.clicked) {
					b.setGraphic(b.imageMineGrey);
				}
				if (b.state != 9 && b.subState > 0) {
					b.setGraphic(b.imageMineMisflagged);
				}
			}
		}
	}

	public static HBox imageCounter(int number) {
		int width = 25;
		int height = 45;
		int firstDigit = (number / 10) / 10;
		int secondDigit = (number / 10) % 10;
		int thirdDigit = number % 10;
		ImageView imageOne = new ImageView(new Image("file:res/digits/" + firstDigit + ".png"));
		ImageView imageTwo = new ImageView(new Image("file:res/digits/" + secondDigit + ".png"));
		ImageView imageThree = new ImageView(new Image("file:res/digits/" + thirdDigit + ".png"));

		imageOne.setFitWidth(width);
		imageOne.setFitHeight(height);

		imageTwo.setFitWidth(width);
		imageTwo.setFitHeight(height);

		imageThree.setFitWidth(width);
		imageThree.setFitHeight(height);

		HBox counter = new HBox(imageOne, imageTwo, imageThree);
		return counter;
	}

	public int knownMineCount(ButtonClick[][] buttons) {
		int mineCount = 0;
		for (int y = 0; y < buttons.length; y++) {
			for (int x = 0; x < buttons[0].length; x++) {
				int tempSubState = buttons[y][x].getSubState();
				int tempState = buttons[y][x].getState();
				if (tempState == 9) {
					mineCount++;
				}
				if (tempSubState > 0) {
					mineCount--;
				}
			}
		}
		return mineCount;
	}

	public boolean Won(ButtonClick[][] buttons) {
		int count = 0;
		int mineCount = 0;
		int allCount = 0;
		for (int y = 0; y < buttons.length; y++) {
			for (int x = 0; x < buttons[0].length; x++) {
				boolean tempClicked = buttons[y][x].getClicked();
				int tempState = buttons[y][x].getState();
				if (tempClicked && !(tempState == 9)) {
					count++;
				}
				if (tempState == 9) {
					mineCount++;
				}
				allCount++;
			}
		}

		if (allCount - count == mineCount) {
			return true;
			// System.out.println(count + " " + mineCount + " " + allCount);
		}
		System.out.println(count + " " + allCount + " " + mineCount);
		return false;
	}

	public static void generateBoard(ButtonClick[][] buttons, int yClick, int xClick) {
		int boardHeight = buttons.length;
		int boardWidth = buttons[0].length;
		int mineCount = 0;
		int minemax = 10;
		// no mine zone setting
		// buttons[yClick][xClick].clicked = true;
		buttons[yClick][xClick].canBeMine = false;

		if (yClick > 0) {
			buttons[yClick - 1][xClick].canBeMine = false;
			if (xClick > 0)
				buttons[yClick - 1][xClick - 1].canBeMine = false;
			if (xClick < buttons[0].length - 1)
				buttons[yClick - 1][xClick + 1].canBeMine = false;
		}
		if (yClick < buttons.length - 1) {
			buttons[yClick + 1][xClick].canBeMine = false;
			if (xClick > 0)
				buttons[yClick + 1][xClick - 1].canBeMine = false;
			if (xClick < buttons[0].length - 1)
				buttons[yClick + 1][xClick + 1].canBeMine = false;
		}
		if (xClick > 0)
			buttons[yClick][xClick - 1].canBeMine = false;
		if (xClick < buttons[0].length - 1)
			buttons[yClick][xClick + 1].canBeMine = false;

		// mine Selection
		while (mineCount < minemax) {
			int ranNum = (int) (Math.random() * boardHeight);
			int ranNum2 = (int) (Math.random() * boardWidth);
			System.out.println(ranNum + ", " + ranNum2);
			if (!(buttons[ranNum][ranNum2].state == 9) && (buttons[ranNum][ranNum2].canBeMine)) {
				buttons[ranNum][ranNum2].state = 9;
				mineCount++;
			}
		}

		// Number Setting
		for (int y = 0; y < buttons.length; y++) {
			for (int x = 0; x < buttons[0].length; x++) {

				int tempState = buttons[y][x].state;
				if (tempState == 9) {
					System.out.println("Mine Found");
					if (y > 0) {
						if (buttons[y - 1][x].state != 9)
							buttons[y - 1][x].state += 1;
						if (x > 0) {
							if (buttons[y - 1][x - 1].state != 9)
								buttons[y - 1][x - 1].state += 1;
						}

						if (x < buttons[0].length - 1) {
							if (buttons[y - 1][x + 1].state != 9)
								buttons[y - 1][x + 1].state += 1;
						}
					}

					if (y < buttons.length - 1) {
						if (buttons[y + 1][x].state != 9)
							buttons[y + 1][x].state += 1;

						if (x > 0) {
							if (buttons[y + 1][x - 1].state != 9)
								buttons[y + 1][x - 1].state += 1;
						}

						if (x < buttons[0].length - 1) {
							if (buttons[y + 1][x + 1].state != 9)
								buttons[y + 1][x + 1].state += 1;
						}
					}

					if (x > 0) {
						if (buttons[y][x - 1].state != 9)
							buttons[y][x - 1].state += 1;
					}

					if (x < buttons[0].length - 1) {
						if (buttons[y][x + 1].state != 9)
							buttons[y][x + 1].state += 1;
					}
				}
				buttons[y][x].boardGenerated = true;
			}
		}

//print full mine board
		for (int y = 0; y < buttons.length; y++) {
			for (int x = 0; x < buttons[0].length; x++) {
				System.out.print(buttons[y][x].state);
			}
			System.out.println();
		}
	}
}

class ButtonClick extends Button {
	int state;
	int subState;
	ImageView imageStart, imageMineRed, imageFlag, imageBlank, imageNumber, imageMineGrey, imageMineMisflagged;
	double size = 50;
	boolean clicked;
	boolean canBeMine;
	boolean boardGenerated;
	int y;
	int x;

	public ButtonClick() {
		state = 0;
		subState = 0;
		clicked = false;
		canBeMine = true;
		boardGenerated = false;
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		imageStart = new ImageView(new Image("file:res/blank.gif"));
		imageMineRed = new ImageView(new Image("file:res/mine-red.png"));
		imageFlag = new ImageView(new Image("file:res/flag.png"));
		imageBlank = new ImageView(new Image("file:res/cover.png"));

		imageMineGrey = new ImageView(new Image("file:res/mine-grey.png"));
		imageMineMisflagged = new ImageView(new Image("file:res/mine-misflagged.png"));

		if (state < 9) {
			imageNumber = new ImageView(new Image("file:res/" + state + ".png"));
			imageNumber.setFitWidth(size);
			imageNumber.setFitHeight(size);
		}

		imageStart.setFitWidth(size);
		imageStart.setFitHeight(size);

		imageMineRed.setFitWidth(size);
		imageMineRed.setFitHeight(size);

		imageFlag.setFitWidth(size);
		imageFlag.setFitHeight(size);

		imageBlank.setFitWidth(size);
		imageBlank.setFitHeight(size);

		imageMineGrey.setFitWidth(size);
		imageMineGrey.setFitHeight(size);

		imageMineMisflagged.setFitWidth(size);
		imageMineMisflagged.setFitHeight(size);

		setGraphic(imageStart);
	}

	public void updateImage() {
		if (state < 9) {
			imageNumber = new ImageView(new Image("file:res/" + state + ".png"));
			imageNumber.setFitWidth(size);
			imageNumber.setFitHeight(size);
		}
	}

	public boolean getClicked() {
		return clicked;
	}

	public int getState() {
		return state;
	}

	public int getSubState() {
		return subState;
	}

	public boolean getCanBeMine() {
		return canBeMine;
	}

	public void numberClicked() {
		updateImage();
		setGraphic(imageNumber);
		clicked = true;
	}

}

class FaceClick extends Button {
	int state;
	ImageView faceSmile, faceDead, faceO, faceWin;

	public FaceClick(int state) {
		double size = 75;
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		faceSmile = new ImageView(new Image("file:res/face-smile.png"));
		faceDead = new ImageView(new Image("file:res/face-dead.png"));
		faceO = new ImageView(new Image("file:res/face-O.png"));
		faceWin = new ImageView(new Image("file:res/face-win.png"));

		faceSmile.setFitWidth(size);
		faceSmile.setFitHeight(size);

		faceDead.setFitWidth(size);
		faceDead.setFitHeight(size);

		faceO.setFitWidth(size);
		faceO.setFitHeight(size);

		faceWin.setFitWidth(size);
		faceWin.setFitHeight(size);

		setGraphic(faceSmile);
	}
}
