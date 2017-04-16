package nearestNeigh;

public class DataGenerator{

   public static void main(String[] args) {
      // hard-coded coordinates constraining latitudes and longitudes 
      // to the Melbourne region.
      Double xmax = 149.00;
      Double xmin = 141.00;
      Double ymax = -36.00;
      Double ymin = -39.00;

      // Loop to generate random values within the x and y ranges. The categories
      // of restaurant, education and hospital were swapped out manually, along with
      // the int i values to generate pseudo-random data distributions.
      for (int i = 774; i < 1000; i++) {
         Double xran = xmin + (double)(Math.random() * ((xmax - xmin) + 1));
         Double yran = ymin + (double)(Math.random() * ((ymax - ymin) + 1));
         System.out.printf("id%d education ",i);
         System.out.printf("%.10f %.10f",yran,xran);
         System.out.println();
         
      // Three files were produced from this loop, n10, n100, and n1000. The split
      // of restaurant, hospital and education was divided roughly into a ratio of
      // 4:3:3. Each file created was unchanged while testing each implementation.
      }
   }
}