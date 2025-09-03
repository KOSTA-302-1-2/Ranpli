package cli.ui; // 패키지 선언

import app.config.Config;          // 설정 로더 클래스 import
import app.music.PlayerController; // 오디오 재생 제어 클래스 import
import app.music.RandomEngine;     // 랜덤 곡 선택 + DB 저장 엔진 import
import app.repo.TrackDao;          // ERD(tb_music) 기반 DAO import

import java.util.Scanner;          // 메뉴 입력용 Scanner import

// === JLine 관련 import (raw 모드, 키 입력 즉시 처리) ===
import org.jline.terminal.Terminal;           // JLine 터미널 객체
import org.jline.terminal.TerminalBuilder;    // 터미널 빌더
import org.jline.utils.NonBlockingReader;     // 블로킹 없이 입력 읽기
import org.jline.utils.InfoCmp;               // 터미널 제어 capability (clear 등)

/**
 * 메인 CLI 루프 프로그램
 * - 메뉴 입력은 Scanner 사용
 * - [R]에서 JLine 세션으로 전환
 * - JLine 세션에서는 진행바 실시간 갱신, P/B/N/M 입력 즉시 처리
 * - 30초 지나면 자동으로 다음 곡 재생
 */
public class MainView {

  // 콘솔 전체 지우기 (메뉴 표시 전 사용)
  static void clear() {
    System.out.print("\u001B[2J\u001B[H"); // ANSI escape: 화면 clear + 커서 홈
    System.out.flush();                    // 출력 버퍼 비우기
  }

  // 메뉴/세션 헤더 출력
  static void header(String t){ System.out.println("==== " + t + " ===="); }

  public static void main(String[] args) {
    Config.load();                                // application.properties 로드
    var trackDao = new TrackDao();                // 트랙 DAO 생성
    var random   = new RandomEngine(trackDao);    // 랜덤 엔진 생성
    var player   = new PlayerController();        // 플레이어 컨트롤러 생성
    var sc       = new Scanner(System.in);        // Scanner로 메뉴 입력 처리

    while (true) {                                // 메인 메뉴 루프
      clear(); header("RANPLI • MENU");           // 화면 초기화 + 헤더 출력
      System.out.println("[R] 랜덤플레이");         
      System.out.println("[L] 플레이 리스트");                       // 빈 줄 출력
      System.out.println("[A] 계정");
      System.out.println("[I] 로그인");
      System.out.println("[J] 회원가입"); 
      System.out.println("[Q] 종료"); 
      System.out.print("\n> ");                   // 프롬프트

      String cmd = sc.nextLine().trim().toLowerCase(); // 입력 받기 + 소문자 변환

      switch (cmd) {                              // 입력 분기
        case "r", "" -> {                         // R 또는 엔터 입력 시
          var track = random.pickOneAndPersist(); // 랜덤 곡 1개 선택 + DB 저장
          if (track == null) {                    // 실패한 경우
            System.out.println("랜덤 선택 실패");   // 에러 메시지
            try { Thread.sleep(900); } catch (Exception ignore) {} // 잠시 대기
            break;                                // 메뉴로 복귀
          }
          player.play(track);                     // 곡 재생 시작
          NowPlayingSession.runJLine(player, random); // ▶ JLine 세션으로 진입
        }
        case "q" -> { player.stop(); return; }    // Q 입력 시: 재생 종료 후 프로그램 종료
        default   -> System.out.println("지원하지 않는 입력"); // 그 외 입력: 안내 메시지
      }
    }
  }

  /** === 재생 세션 클래스 (JLine 기반) === */
  static class NowPlayingSession {
    private static final int TOTAL_SECONDS = 30;  // 미리듣기 30초
    private static final int TICK_MS = 100;       // 진행바 갱신 주기 100ms

    /** 진행바 클래스 */
    static class ProgressBar {
      final int totalSec;                         // 총 재생 길이(초)
      long startEpochMs = System.currentTimeMillis(); // 시작 시각 (ms)
      long pausedAccumMs = 0;                     // 누적 일시정지 시간
      long pauseStartMs = 0;                      // 현재 일시정지 시작 시각
      boolean paused = false;                     // 일시정지 여부

      // 생성자
      ProgressBar(int totalSec) { this.totalSec = totalSec; }

      // 초기화 (새 곡 시작 시)
      void reset(){ 
        startEpochMs = System.currentTimeMillis(); 
        pausedAccumMs=0; 
        pauseStartMs=0; 
        paused=false; 
      }

      // 일시정지/재개 토글
      void toggle(){ if (paused) resume(); else pause(); }

      // 일시정지 시작
      void pause(){ if(!paused){ paused = true; pauseStartMs = System.currentTimeMillis(); } }

      // 일시정지 해제
      void resume(){ 
        if(paused){ 
          paused = false; 
          pausedAccumMs += System.currentTimeMillis() - pauseStartMs; 
          pauseStartMs = 0; 
        } 
      }

      // 현재 일시정지 상태 반환
      boolean isPaused(){ return paused; }

      // 경과 시간(초) 계산
      int elapsedSec(){
        long base = paused ? pauseStartMs : System.currentTimeMillis(); // 정지 상태면 멈춘 시각
        long ms = (base - startEpochMs) - pausedAccumMs;                // 전체 경과 - 정지 누적
        if (ms < 0) ms = 0;                                            // 음수 방지
        int s = (int)(ms / 1000L);                                     // 초 단위 변환
        return Math.min(s, totalSec);                                  // 총 길이 초과 방지
      }

      // 진행바 문자열 렌더링
      String render(){
        int elapsed = elapsedSec();                                    // 현재 경과 초
        int width = 30;                                                // 바 너비
        int filled = (int)Math.round((elapsed/(double)totalSec)*width);// 채운 칸 수
        if (filled < 0) filled = 0; if (filled > width) filled = width;// 보정
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<width;i++) sb.append(i<filled ? '#' : '-');     // 진행 상태
        sb.append(']').append(' ').append(elapsed).append("s / ").append(totalSec).append("s");
        if (paused) sb.append("  (PAUSED)");                           // 일시정지 표시
        return sb.toString();
      }
    }

    /** 한 줄 진행바 출력 유틸 (ANSI 사용) */
    static void drawProgress(Terminal t, String s){
      t.writer().print("\r\u001B[2K"); // 커서 맨 앞으로 이동 + 라인 전체 삭제
      t.writer().print(s);             // 진행바 문자열 출력
      t.flush();                       // 즉시 반영
    }

    /** println 유틸 (JLine writer 사용) */
    static void println(Terminal t, String s){ t.writer().println(s); }

    /** JLine 기반 세션 실행 */
    static void runJLine(PlayerController player, RandomEngine random) {
      Terminal term = null;              // 터미널 객체
      NonBlockingReader in = null;       // 입력 리더

      try {
        term = TerminalBuilder.builder()
                .system(true)            // 현재 콘솔 사용
                .jna(true)               // Windows 콘솔 호환 강화
                .build();
        term.enterRawMode();             // Raw 모드 (엔터 없이 키 입력)
        in = term.reader();              // 입력 리더 얻기

        // 화면 초기화 + 고정 텍스트 출력
        term.puts(InfoCmp.Capability.clear_screen);
        term.flush();
        println(term, "==== NOW PLAYING ===="); // 제목 줄
        var t = player.current();               // 현재 곡 정보
        if (t != null) println(term, "♪ " + t.title() + " - " + t.artist());
        println(term, "");                      // 빈 줄
        println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [S] 저장 [M] 메뉴"); // 도움말
        println(term, "(키 입력은 화면에 표시되지 않습니다)");                  // 안내 문구
        println(term, "");                      // 진행바 라인 확보

        var bar = new ProgressBar(TOTAL_SECONDS); // 진행바 인스턴스

        // 세션 메인 루프
        while (true) {
          // 1) 진행바를 라이브로 갱신
          drawProgress(term, bar.render());

          // 2) 입력 버퍼에 있는 모든 키를 즉시 처리
          while (in.ready()) {
            int ch = in.read();                       // 입력 한 글자 읽기
            if (ch == -1) break;                      // -1이면 무시
            ch = Character.toLowerCase(ch);           // 소문자로 통일
            if (ch == '\r' || ch == '\n') continue;   // 엔터 키는 무시

            switch (ch) {
              case 'p' -> { // 일시정지/재개
                player.togglePause(); 
                bar.toggle(); 
              }
              case 'b' -> { // 이전곡
                var prev = player.loadPrevious();
                if (prev != null) {
                  bar.reset();
                  term.puts(InfoCmp.Capability.clear_screen); // 화면 초기화
                  term.flush();
                  println(term, "==== NOW PLAYING ====");
                  println(term, "♪ " + prev.title() + " - " + prev.artist());
                  println(term, "");
                  println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [S] 저장 [M] 메뉴");
                  println(term, "(키 입력은 화면에 표시되지 않습니다)");
                  println(term, ""); // 진행바 라인 확보
                }
              }
              case 'n' -> { // 다음곡
                var next = random.pickOneAndPersist();
                if (next != null) {
                  player.play(next);
                  bar.reset();
                  term.puts(InfoCmp.Capability.clear_screen);
                  term.flush();
                  println(term, "==== NOW PLAYING ====");
                  println(term, "♪ " + next.title() + " - " + next.artist());
                  println(term, "");
                  println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [S] 저장 [M] 메뉴");
                  println(term, "(키 입력은 화면에 표시되지 않습니다)");
                  println(term, "");
                }
              }
              case 'm' -> { // 메뉴 복귀
                player.stop();
                return;
              }
              default -> { /* 그 외 키는 무시 */ }
            }
          }

          // 3) 자동 다음곡 처리 (30초 도달 시)
          if (!bar.isPaused() && bar.elapsedSec() >= TOTAL_SECONDS) {
            var next = random.pickOneAndPersist();
            if (next != null) {
              player.play(next);
              bar.reset();
              term.puts(InfoCmp.Capability.clear_screen);
              term.flush();
              println(term, "==== NOW PLAYING ====");
              println(term, "♪ " + next.title() + " - " + next.artist());
              println(term, "");
              println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [S] 저장 [M] 메뉴");
              println(term, "(키 입력은 화면에 표시되지 않습니다)");
              println(term, "");
            } else {
              player.stop(); // 곡이 없으면 정지 후 메뉴 복귀
              return;
            }
          }

          // 4) 프레임 간격 유지 (틱)
          try { Thread.sleep(TICK_MS); } catch (InterruptedException ignore) {}
        }

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try { if (term != null) { term.flush(); term.close(); } } catch (Exception ignore) {}
      }
    }
  }
}
