import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFileChooser;

/**
 * EDITS FOR CS10 can be found as the first few methods in the program.
 * Added methods: reduceColors, computeColors, clusterColors, computeCentroid, 
 * findClosestColor, mapToColorList, and getColorDistance.
 * 
 * 
 * 
 * A class that represents a picture.  This class inherits from 
 * SimplePicture and allows the student to add functionality to
 * the Picture class.  
 * 
 * Copyright Georgia Institute of Technology 2004-2008
 * @author Barbara Ericson ericson@cc.gatech.edu
 * @author Chris Leech edited 9/30/2013
 * @author Chris Leech further edited 11/17/2014
 */

public class Picture extends SimplePicture { 

  ///////////////////// constructors //////////////////////////////////
  
  /**
   * Constructor that takes no arguments 
   */
  public Picture () {
    
    /* not needed but use it to show students the implicit call to super()
     * child constructors always call a parent constructor 
     */
    super();  
  }
  
  /**
   * Constructor that takes a file name and creates the picture 
   * @param fileName the name of the file to create the picture from
   */
  public Picture(String fileName) {
    // let the parent class handle this fileName
    super(fileName);
  }
  
  /**
   * Constructor that takes the width and height
   * @param width the width of the desired picture
   * @param height the height of the desired picture
   */
  public Picture(int width, int height) {
    // let the parent class handle this width and height
    super(width,height);
  }
  
  /**
   * Constructor that takes a picture and creates a 
   * copy of that picture
   */
  public Picture(Picture copyPicture) {
    // let the parent class do the copy
    super(copyPicture);
  }
  
  /**
   * Constructor that takes a buffered image
   * @param image the buffered image to use
   */
  public Picture(BufferedImage image) {
    super(image);
  }
  
  ////////////////////// methods ///////////////////////////////////////

  /**
   * reduceColors
   * Method that takes an int number of colors, and chooses the best colors to map to this image.
   * Then, it returns an image with the colors mapped to it.
   * @param the int number of colors to map to the image.
   * @return a new picture with the correct number of colors.
   */

  public Picture reduceColors(int number) {
  	return this.mapToColorList(computeColors(number));	
  }
  
  /**
   * getFirstNColors
   * Method that takes an int number of colors, and finds the first N unique colors in the image.
   * @param the int number of colors to return.
   * @return an ArrayList full of colors
   */
  
  public ArrayList<Color> getFirstNColors(int colorNumber) {
  	Pixel [] origArray = this.getPixels();
  	
  	// Code for creating an initial list of the first n pixels in the picture
  	// Gets the first pixel
  	// Adds it to the list
  	// sets two indices: number of unique colors and a list index
  	Color firstColor = origArray[0].getColor();
  	ArrayList<Color> initColors = new ArrayList<Color>();
  	initColors.add(firstColor);
  	int numberOfInitialColors = 1;
  	int index = 1;
  	
  	// While we don't yet have enough colors
  	// If origArray[index] isn't in the list, add it
  	while (numberOfInitialColors < colorNumber) {
  		
  		if (!initColors.contains(origArray[index].getColor())) {	
  		    initColors.add(origArray[index].getColor());
  		    numberOfInitialColors ++;
  		}
  		index ++;
  	}
  	return initColors;
  }
  
  /**
   * computeColors
   * Method that takes an int number of colors, and chooses the best colors to map to this image.
   * @param the int number of colors to map to the image.
   * @return an ArrayList of the correct colors
   */
  
  public ArrayList<Color> computeColors(int colorNumber) {
  	
  	// Creates an array of the pixels in the image
  	Pixel [] origArray = this.getPixels();
  	ArrayList<Color> colorArray = new ArrayList<Color>();
  	for (Pixel pixelObj : origArray) {
  		colorArray.add(pixelObj.getColor());
  	}
  	
  	// Creates an initial list of the first N colors of the picture
  	//ArrayList<Color> initColors = getFirstNColors(colorNumber);
  	
  	// Creates an initial list of the correct number of random colors
  	ArrayList<Color> initColors = randomColorList(colorNumber);  	
  	
  	
  	// initializes a current list and the next iteration list
  	ArrayList<Color> currentColors = new ArrayList<Color>();
  	ArrayList<Color> nextColors = new ArrayList<Color>();
  	
  	// initializes the list of lists for the clusters
  	ArrayList<ArrayList<Color>> colorGrid = new ArrayList<ArrayList<Color>>();
  	
  	// start by filling the next list with the initial colors
  	for (Color listColor : initColors) {
  		nextColors.add(listColor);
  	}
  	
  	// initialize our array of arrays and an index int
  	ArrayList<ArrayList<Color>> clusters = new ArrayList<ArrayList<Color>>();
  	
  	// add an array to the grid for each color in the list
  	for (Color listColor : initColors ) {
  		ArrayList<Color> e = new ArrayList<Color>();
  		clusters.add(e);
  	}
  	
  	// MAIN LOOP:
  	// while the lists are not equal:
  	// transfer colors from next to current
  	// cluster the colors under current colors
  	// find the centroid of each color
  	// add the set of centroids to nextcolors
        while (!nextColors.equals(currentColors)) {
    	
  		currentColors.clear();
  		for (Color listColor : nextColors) {
  			currentColors.add(listColor);
  		}
  		nextColors.clear();
  		
  		for (ArrayList list : clusters) {
  			list.clear();
  		}
  		clusters = clusterColors(colorArray, currentColors, clusters);
  		
  		for(int count = 0; count<clusters.size(); count++)
  			if(clusters.get(count).size()==0)
  				clusters.remove(count);
  		
  		for (ArrayList<Color> pixelList : clusters ) {
  			nextColors.add(computeCentroid(pixelList));
  		}
  	}
  	// when we exit loop, we have the best colors. return these.
  	return nextColors;
  	
  }
  
  /**
   * clusterColors
   * Method that takes an array of pixels and an ArrayList of colors 
   * and matches each pixel to its closest color in the list.
   * @param the list of colors to map to.
   * @param an array of pixels to match with colors.
   * @return an ArrayList of ArrayLists full of pixels, one sub-list per color.
   */
  
  private ArrayList<ArrayList<Color>> clusterColors(ArrayList<Color> colorArray, ArrayList<Color> colorlist, 
  ArrayList<ArrayList<Color>> clusters) {

  	int listIndex;
  
  	// put each pixel in the correct sublist

  	for (Color color : colorArray) {

  		listIndex = findClosestColor(color, colorlist);
  		clusters.get(listIndex).add(color);
  	}

  	// return the grid
  	return clusters;
  }
  
  
  /**
   * computeCentroid
   * Method that takes an array of colors (each representing a pixel) and finds the average color. 
   * @param an ArrayList of colors, one for each pixel in question
   * @return the average Color of the pixels.
   */
  
  private Color computeCentroid(ArrayList<Color> pixels) {
  	
	// initialize variables for the length, and total RGB values of each centroid
  	int length = 0;
  	int totalRed = 0;
  	int totalGreen = 0;
  	int totalBlue = 0;
  	
  	// for each pixel, increment RGB values and increase length by one.
  	for (Color pixelColor : pixels) {
  		length += 1;
  		totalRed += pixelColor.getRed();
  		totalGreen += pixelColor.getGreen();
  		totalBlue += pixelColor.getBlue();
  	}
  	// if empty, prevent division by 0
  	if (length == 0) {
  		length = 1;
  	}
  	Color centroid = new Color(totalRed/length, totalGreen/length, totalBlue/length);
  	return centroid;
  } 
  
  /**
   * findClosestColor
   * Method that takes a pixel and an ArrayList of colors 
   * and matches the pixel to its closest color in the list.
   * @param the pixel to match with the list colors.
   * @param an ArrayList of Colors to match to.
   * @return the closest Color to the pixel in the list.
   */
  
  private int findClosestColor(Color pixelColor, ArrayList<Color> colors) {
		
      // initialize a return variable and 2 int index variables
      // Save the pixel's color in the variable pixelColor

	  int smallestDist = 0;
	  int temp = 0;
	  int index = 0;
	  
	  // for each color in the list
	  // find the distance between the color and the given pixel
	  // if it is the smallest distance, set smallest distance to the new record low
	  // and set the return variable to that list color
	  for (int i = 0; i < colors.size(); i ++) {
	    
	    temp = getColorDistance(pixelColor, colors.get(i));
	 
	    if (temp < smallestDist || smallestDist == 0) {
	      smallestDist = temp;
	      index = i;
	    }
      }
      return index;
  }
	
  
  /**
   * mapToColorList
   * Method that takes an ArrayList of Colors and returns the picture in just those colors.
   * @param the list of colors to map to.
   * @return the picture in the correct field of colors.
   */
  
  public Picture mapToColorList(ArrayList<Color> colors) {
    // create a copy of the given picture to work on
  	Picture target = this.copy();
  	// separate the original and the target list of pixels
    Pixel [] targetArray = target.getPixels();
    Pixel [] origArray = this.getPixels();

    // for each pixel in the original, replace target with the closest color in the list
    for (int i = 0; i < origArray.length; i ++) {
        targetArray[i].setColor(colors.get(findClosestColor(origArray[i].getColor(), colors)));
    }
    // return new picture
    return target;
  }
  
  
  /**
   * randomColorList
   * Method that takes an int number of colors and generates that many random colors.
   * @param the int number of colors required.
   * @return an ArrayList full of random colors.
   */
  
  public static ArrayList<Color> randomColorList(int length) {
  	
  	// initializes a list and a set of randoms
  	ArrayList<Color> thelist = new ArrayList<Color>();
  	Random random = new Random();
  	
  	// up until the int limit, add another color to the list
  	for (int counter = 0; counter < length; counter ++) {
  		thelist.add(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
  	}
  	// return our completed list
  	return thelist;
  }
  
  /**
   * getColorDistance
   * Method that takes 2 colors and finds the distance between them.
   * @param the first color.
   * @param the second color
   * @return an int distance between the colors.
   */
  
  private int getColorDistance(Color color1, Color color2) {
  	
  	// Creates a int for distance and sets it to the distance formula (without the sqrt)
  	int distance;
  	distance = ((color1.getRed() - color2.getRed())*(color1.getRed() - color2.getRed()) + 
        (color1.getBlue() - color2.getBlue())*(color1.getBlue() - color2.getBlue()) + (color1.getGreen() - 
        color2.getGreen())*(color1.getGreen() - color2.getGreen()));
  	return distance;
  }
  
  /** 
   * A Class that convolves a single pixel
   * @param matrix fed into convolve
   * @param original picture
   * @param target picture
   * @param x location of the convolved pixel
   * @param y location of the convolved pixel
   */
  
  public void convolve_pixel(float [][] matrix, Picture original, Picture target, int x, int y) {
    
    // Finds current pixel
    Pixel currentPixel = target.getPixel(x, y);
    
    // Initializes variables for the final color of the target pixel
    float final_red = 0;
    float final_green = 0;
    float final_blue = 0;
    
    // Creates a 3x3 matrix of pixels around the target pixel
    
    Pixel [][] pixelList = new Pixel [3][3];
    
    pixelList[0][0] = original.getPixel(x - 1, y - 1);
    pixelList[0][1] = original.getPixel(x, y - 1);
    pixelList[0][2] = original.getPixel(x + 1, y - 1);
    pixelList[1][0] = original.getPixel(x - 1, y);
    pixelList[1][1] = original.getPixel(x, y);
    pixelList[1][2] = original.getPixel(x + 1, y);
    pixelList[2][0] = original.getPixel(x - 1, y + 1);
    pixelList[2][1] = original.getPixel(x, y + 1);
    pixelList[2][2] = original.getPixel(x + 1, y + 1);
    
    for (int currentX = 0; currentX < 3; currentX ++) {
      for (int currentY = 0; currentY < 3; currentY ++) {
        
        // Loops through matrix and adds scaled colors of surrounding pixels to the final values
        
        final_red += pixelList[currentX][currentY].getRed() * matrix[currentX][currentY];
        final_green += pixelList[currentX][currentY].getGreen() * matrix[currentX][currentY];
        final_blue += pixelList[currentX][currentY].getBlue() * matrix[currentX][currentY];
        
      }
    }
    
    // Makes sure values are appropriate
    
    if (final_red < 0) final_red = 0;
    if (final_green < 0) final_green = 0;
    if (final_blue < 0) final_blue = 0;
    if (final_red > 255) final_red = 255;
    if (final_green > 255) final_green = 255;
    if (final_blue > 255) final_blue = 255;

    // Sets color of pixel in target image
    currentPixel.setColor(new Color( (int) final_red, (int) final_green, (int) final_blue));    
    
  }
  
  /**
   * Convolve method for SA2
   * @param convolve matrix
   */
  
  public Picture convolve(float [][] matrix) {
    
    //Finds width and height
    int width = this.getWidth();
    int height = this.getHeight();
    
    // Creates a target copy of the picture
    Picture target = this.copy();
    
    // Loops through pixels and convolves each (does not convolve edge)
    for (int currentX = 1; currentX < width - 1; currentX ++) {
      for (int currentY = 1; currentY < height - 1; currentY ++) {
    
        convolve_pixel(matrix, this, target, currentX, currentY);    
        
      }
    }
    
    //Returns the convolved picture
    return target;
  }
  
  /**
   * Method to return a string with information about this picture.
   * @return a string with information about the picture such as fileName,
   * height and width.
   */
    
  public String toString() {
    String output = "Picture, filename " + getFileName() + 
    " height " + getHeight() + " width " + getWidth();
    return output;
    
  }
  
  /**
   * Class method to let the user pick a file name and then create the picture 
   * and show it
   * @return the picture object
   */
    
  public static Picture pickAndShow() {
    String fileName = FileChooser.pickAFile();
    Picture picture = new Picture(fileName);
    picture.show();
    return picture;
  }
  
  /**
   * Class method to create a picture object from the passed file name and 
   * then show it
   * @param fileName the name of the file that has a picture in it
   * @return the picture object
   */
    
  public static Picture showNamed(String fileName) {
    Picture picture = new Picture(fileName);
    picture.show();
    return picture;
  }
  
  /**
   * A method create a copy of the current picture and return it
   * @return the copied picture
   */
    
  public Picture copy() {
    return new Picture(this);
  }
  
  /**
   * Method to increase the red in a picture.
   */
    
  public void increaseRed() {
    Pixel [] pixelArray = this.getPixels();
    for (Pixel pixelObj : pixelArray) {
      pixelObj.setRed(pixelObj.getRed()*2);
    }
  }
  
  /**
   * Method to negate a picture
   */
    
  public void negate() {
    Pixel [] pixelArray = this.getPixels();
    int red,green,blue;
    
    for (Pixel pixelObj : pixelArray) {
      red = pixelObj.getRed();
      green = pixelObj.getGreen();
      blue = pixelObj.getBlue();
      pixelObj.setColor(new Color(255-red, 255-green, 255-blue));
    }
  }
  
  /**
   * Method to flip a picture 
   */
    
  public Picture flip() {
    Pixel currPixel = null;
    Pixel targetPixel = null;
    Picture target = new Picture(this.getWidth(),this.getHeight());
    
    for (int srcX = 0, trgX = getWidth()-1; srcX < getWidth();
    srcX++, trgX--) {
      for (int srcY = 0, trgY = 0; srcY < getHeight();
      srcY++, trgY++) {
        
        // get the current pixel
        currPixel = this.getPixel(srcX,srcY);
        targetPixel = target.getPixel(trgX,trgY);
        
        // copy the color of currPixel into target
        targetPixel.setColor(currPixel.getColor());
      }
    }
    return target;
  }
  
  /**
   * Method to decrease the red by half in the current picture
   */
    
  public void decreaseRed() {
  
    Pixel pixel = null; // the current pixel
    int redValue = 0;       // the amount of red

    // get the array of pixels for this picture object
    Pixel[] pixels = this.getPixels();

    // start the index at 0
    int index = 0;

    // loop while the index is less than the length of the pixels array
    while (index < pixels.length) {

      // get the current pixel at this index
      pixel = pixels[index];
      // get the red value at the pixel
      redValue = pixel.getRed();
      // set the red value to half what it was
      redValue = (int) (redValue * 0.5);
      // set the red for this pixel to the new value
      pixel.setRed(redValue);
      // increment the index
      index++;
    }
  }
  
  /**
   * Method to decrease the red by an amount
   * @param amount the amount to change the red by
   */
    
  public void decreaseRed(double amount) {
 
    Pixel[] pixels = this.getPixels();
    Pixel p = null;
    int value = 0;

    // loop through all the pixels
    for (int i = 0; i < pixels.length; i++) {
 
      // get the current pixel
      p = pixels[i];
      // get the value
      value = p.getRed();
      // set the red value the passed amount time what it was
      p.setRed((int) (value * amount));
    }
  }
  
  /**
   * Method to compose (copy) this picture onto a target picture
   * at a given point.
   * @param target the picture onto which we copy this picture
   * @param targetX target X position to start at
   * @param targetY target Y position to start at
   */
    
  public void compose(Picture target, int targetX, int targetY) {
 
    Pixel currPixel = null;
    Pixel newPixel = null;

    // loop through the columns
    for (int srcX=0, trgX = targetX; srcX < this.getWidth();
         srcX++, trgX++) {
  
      // loop through the rows
      for (int srcY=0, trgY=targetY; srcY < this.getHeight();
           srcY++, trgY++) {

        // get the current pixel
        currPixel = this.getPixel(srcX,srcY);

        /* copy the color of currPixel into target,
         * but only if it'll fit.
         */
        if (trgX < target.getWidth() && trgY < target.getHeight()) {
          newPixel = target.getPixel(trgX,trgY);
          newPixel.setColor(currPixel.getColor());
        }
      }
    }
  }
  
  /**
   * Method to scale the picture by a factor, and return the result
   * @param factor the factor to scale by (1.0 stays the same,
   * 0.5 decreases each side by 0.5, 2.0 doubles each side)
   * @return the scaled picture
   */
    
  public Picture scale(double factor) {
    
    Pixel sourcePixel, targetPixel;
    Picture canvas = new Picture((int) (factor*this.getWidth())+1,
    (int) (factor*this.getHeight())+1);
    // loop through the columns
    for (double sourceX = 0, targetX=0;
         sourceX < this.getWidth();
         sourceX+=(1/factor), targetX++) {
      
      // loop through the rows
      for (double sourceY=0, targetY=0;
           sourceY < this.getHeight();
           sourceY+=(1/factor), targetY++) {
        
        sourcePixel = this.getPixel((int) sourceX,(int) sourceY);
        targetPixel = canvas.getPixel((int) targetX, (int) targetY);
        targetPixel.setColor(sourcePixel.getColor());
      }
    }
    return canvas;
  }
  
  /**
   * Method to do chromakey using an input color for the background
   * and a point for the upper left corner of where to copy
   * @param target the picture onto which we chromakey this picture
   * @param bgColor the color to make transparent
   * @param threshold within this distance from bgColor, make transparent
   * @param targetX target X position to start at
   * @param targetY target Y position to start at
   */
    
  public void chromakey(Picture target, Color bgColor, int threshold,
  int targetX, int targetY) {
 
    Pixel currPixel = null;
    Pixel newPixel = null;

    // loop through the columns
    for (int srcX=0, trgX=targetX;
        srcX<getWidth() && trgX<target.getWidth();
        srcX++, trgX++) {

      // loop through the rows
      for (int srcY=0, trgY=targetY;
        srcY<getHeight() && trgY<target.getHeight();
        srcY++, trgY++) {

        // get the current pixel
        currPixel = this.getPixel(srcX,srcY);

        /* if the color at the current pixel is within threshold of
         * the input color, then don't copy the pixel
         */
        if (currPixel.colorDistance(bgColor)>threshold) {
          target.getPixel(trgX,trgY).setColor(currPixel.getColor());
        }
      }
    }
  }
  
  /**
   * Method to do chromakey assuming a blue background 
   * @param target the picture onto which we chromakey this picture
   * @param targetX target X position to start at
   * @param targetY target Y position to start at
   */
    
  public void blueScreen(Picture target, int targetX, int targetY) {

    Pixel currPixel = null;
    Pixel newPixel = null;

    // loop through the columns
    for (int srcX=0, trgX=targetX;
         srcX<getWidth() && trgX<target.getWidth();
         srcX++, trgX++) {

      // loop through the rows
      for (int srcY=0, trgY=targetY;
           srcY<getHeight() && trgY<target.getHeight();
           srcY++, trgY++) {

        // get the current pixel
        currPixel = this.getPixel(srcX,srcY);

        /* if the color at the current pixel mostly blue (blue value is
         * greater than red and green combined), then don't copy pixel
         */
        if (currPixel.getRed() + currPixel.getGreen() > currPixel.getBlue()) {
          target.getPixel(trgX,trgY).setColor(currPixel.getColor());
        }
      }
    }
  }
  
  /**
   * Method to change the picture to gray scale with luminance
   */
    
  public void grayscaleWithLuminance() {
    Pixel[] pixelArray = this.getPixels();
    Pixel pixel = null;
    int luminance = 0;
    double redValue = 0;
    double greenValue = 0;
    double blueValue = 0;

    // loop through all the pixels
    for (int i = 0; i < pixelArray.length; i++) {
      // get the current pixel
      pixel = pixelArray[i];

      // get the corrected red, green, and blue values
      redValue = pixel.getRed() * 0.299;
      greenValue = pixel.getGreen() * 0.587;
      blueValue = pixel.getBlue() * 0.114;

      // compute the intensity of the pixel (average value)
      luminance = (int) (redValue + greenValue + blueValue);

      // set the pixel color to the new color
      pixel.setColor(new Color(luminance,luminance,luminance));

    }
  }
  
  /** 
   * Method to do an oil paint effect on a picture
   * @param dist the distance from the current pixel 
   * to use in the range
   * @return the new picture
   */
    
  public Picture oilPaint(int dist) {
    
    // create the picture to return
    Picture retPict = new Picture(this.getWidth(),this.getHeight());
    
    // declare pixels
    Pixel currPixel = null;
    Pixel retPixel = null;
    
    // loop through the pixels
    for (int x = 0; x < this.getWidth(); x++) {
      for (int y = 0; y < this.getHeight(); y++) {
        currPixel = this.getPixel(x,y);
        retPixel = retPict.getPixel(x,y);
        retPixel.setColor(currPixel.getMostCommonColorInRange(dist));
      }
    }
    return retPict;
  }
  
  public static String getFilePath() {
    //Create a file chooser
    JFileChooser fc = new JFileChooser();
     
    int returnVal = fc.showOpenDialog(null);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String pathName = file.getAbsolutePath();
      return pathName;
    }
    else
      return "";
  } 
  
  public static void main(String[] args) throws IOException {
    
  	Picture p = new Picture(1024, 1024);
  	Pixel [] pixels = p.getPixels();
  	int total = 1024 * 1024;
  	BufferedBitReader bitInput = new BufferedBitReader(getFilePath());
  	
  	int bit;
  	for(int i = 0; i < total; i++) {
  		
  		bit = bitInput.readBit();
  		System.out.println(i);
  		if (bit == 1) {
  			pixels[i].setColor(Color.black);
  		}
  		else {
  			pixels[i].setColor(Color.white);
  		}
        
  		
  	}
  	p.show();
  }
        
} 
 
