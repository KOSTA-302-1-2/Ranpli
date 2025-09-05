package app.music;

import javax.sound.sampled.*;
import java.io.*;

public final class BgmPlayer {
    private static final BgmPlayer INSTANCE = new BgmPlayer();
    private Clip clip;

    public static BgmPlayer get() { return INSTANCE; }
    private BgmPlayer() {}

    /** 리소스(/audio/bgm.wav 등)에서 WAV를 무한 반복 재생 */
    public synchronized void playLoopingFromResource(String resourcePath) {
        stop();
        try (InputStream raw = BgmPlayer.class.getResourceAsStream(resourcePath);
             BufferedInputStream in = new BufferedInputStream(raw)) {
            if (raw == null) throw new IllegalArgumentException("리소스를 찾을 수 없음: " + resourcePath);
            playLoopingInternal(in);
        } catch (Exception e) {
            System.err.println("[BGM] Resource play 실패: " + e.getMessage());
        }
    }

    /** 로컬 파일 경로에서 WAV 무한 반복 재생 (원하면 이 메서드 써도 됨) */
    public synchronized void playLoopingFromFile(String filePath) {
        stop();
        try (InputStream raw = new FileInputStream(filePath);
             BufferedInputStream in = new BufferedInputStream(raw)) {
            playLoopingInternal(in);
        } catch (Exception e) {
            System.err.println("[BGM] File play 실패: " + e.getMessage());
        }
    }

    private void playLoopingInternal(InputStream in) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(in);

        // 16-bit PCM으로 변환(필요 시)
        AudioFormat src = ais.getFormat();
        AudioFormat pcm = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                src.getSampleRate(),
                16,
                src.getChannels(),
                src.getChannels() * 2,
                src.getSampleRate(),
                false
        );
        AudioInputStream dais =
                (src.getEncoding() == AudioFormat.Encoding.PCM_SIGNED && src.getSampleSizeInBits() == 16)
                        ? ais
                        : AudioSystem.getAudioInputStream(pcm, ais);

        clip = AudioSystem.getClip();
        clip.open(dais);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
 // BgmPlayer.java
    public void ensureLoopingFromResource(String resource) {
        if (clip != null && clip.isRunning()) {
            // 이미 재생 중이면 새로 시작 안 함
            return;
        }
        playLoopingFromResource(resource); // 기존 메서드 호출
    }

    /** 정지 및 리소스 해제 */
    public synchronized void stop() {
        try {
            if (clip != null) {
                if (clip.isRunning()) clip.stop();
                clip.flush();
                clip.close();
            }
        } catch (Exception ignore) {}
        clip = null;
    }
}
