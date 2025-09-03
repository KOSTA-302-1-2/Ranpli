package app.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * 30초 previewUrl을 단발 재생.
 * - 실제 일시정지/재개를 완벽히 하려면 MediaPlayer 인스턴스를 전역으로 들고 있어야 함.
 *   (MVP에서는 간단화를 위해 재개 시 다시 playOnce 호출)
 */
public class AudioPlayerFx {
  private static boolean inited = false;
  private static void initToolkit(){
    if (inited) return;
    new javafx.embed.swing.JFXPanel();
    inited = true;
  }

  public static void playOnce(String url){
    initToolkit();
    Media media = new Media(url);
    MediaPlayer player = new MediaPlayer(media);
    player.setOnEndOfMedia(player::dispose);
    player.play();
  }
}
