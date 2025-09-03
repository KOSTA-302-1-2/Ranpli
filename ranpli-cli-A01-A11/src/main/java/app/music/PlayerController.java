package app.music;

import app.music.model.Track;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayDeque;
import java.util.Deque;

/** JavaFX MediaPlayer 기반 재생 제어기 (실시간 제어 지원) */
public class PlayerController {
  private final Deque<Track> history = new ArrayDeque<>();
  private Track current;
  private MediaPlayer player;
  private boolean paused = false;
  private static boolean fxInited = false;

  private static void initFx() {
    if (fxInited) return;
    new javafx.embed.swing.JFXPanel(); // JavaFX 런타임 초기화
    fxInited = true;
  }

  public Track current(){ return current; }

  public void play(Track t) {
    initFx();
    if (current != null) history.push(current);
    current = t;
    replaceAndPlay(t.previewUrl());
    paused = false;
  }

  public Track loadPrevious() {
    initFx();
    if (history.isEmpty()) return null;
    Track prev = history.pop();
    current = prev;
    replaceAndPlay(prev.previewUrl());
    paused = false;
    return prev;
  }

  public void pause()  { if (player != null) { player.pause(); paused = true; } }
  public void resume() { if (player != null) { player.play();  paused = false; } }
  public boolean isPaused() { return paused; }
  public void togglePause(){ if (isPaused()) resume(); else pause(); }

  public void stop() {
    if (player != null) {
      try { player.stop(); } catch (Exception ignore){}
      try { player.dispose(); } catch (Exception ignore){}
      player = null;
    }
    paused = false;
  }

  public boolean hasPrevious(){ return !history.isEmpty(); }

  private void replaceAndPlay(String previewUrl) {
    stop();
    Media media = new Media(previewUrl);
    player = new MediaPlayer(media);
    player.setOnEndOfMedia(() -> { /* 30초 끝났을 때 동작 필요하면 여기 */ });
    player.play();
  }
}
