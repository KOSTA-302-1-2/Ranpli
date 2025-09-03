package app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static final Properties props = new Properties();
  public static void load(){
    try (InputStream is = Config.class.getClassLoader().getResourceAsStream("application.properties")){
      if (is != null) props.load(is);
      else System.err.println("application.properties를 찾지 못했습니다.");
    } catch (IOException e){ throw new RuntimeException(e); }
  }
  public static String get(String key){ return props.getProperty(key); }
}
