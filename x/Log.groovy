package x;
public class Log {
  public static boolean debug = false
  public static void log(String text) {
    println text
  }
  public static void debug(String text) {
    if ( debug ) {
      println text
    }
  }
}
