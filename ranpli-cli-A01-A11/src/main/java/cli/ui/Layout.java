package cli.ui;

import app.music.BgmPlayer;
import cli.ui.MainView;        
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import java.io.*;
import java.nio.charset.StandardCharsets;
import com.sun.jna.platform.win32.Kernel32;

public class Layout {
	private static final String BGM_RESOURCE = "/audio/bgm.wav";
    private static final String DIV =
        "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"
        + "::::::::::::::::::::::::::::::::::::::::::::::::::";
    
    public static void header(String title, String logoColorAnsi) {
        clear();
        if (logoColorAnsi != null && !logoColorAnsi.isEmpty()) {
            System.out.print(logoColorAnsi);     // 색 적용
        }
        MainView.printLogoStdout();
        System.out.print("\u001B[0m");            // 색 리셋
        System.out.println();
        System.out.println(title);
        System.out.println("────────────────────────────────");
    }

    
    /** 표준 출력/오류 스트림을 UTF-8로 강제 */
    public static void forceUtf8StdStreams() {
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
        } catch (Exception ignore) {}
    }

    /** Windows 콘솔 코드페이지까지 UTF-8(65001)로 전환 */
    public static void forceWindowsUtf8Console() {
        try {
            if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
                Kernel32.INSTANCE.SetConsoleCP(65001);
                Kernel32.INSTANCE.SetConsoleOutputCP(65001);
            }
        } catch (Throwable ignore) {}
    }
    	   
    private static boolean isRandomTitle(String title) {
        if (title == null) return false;
        String norm = title.replaceAll("\\s+", "").toLowerCase(); // 공백 제거 + 소문자
        // ↓ 어떤 타이틀을 써도 잡히게
        return norm.equals("랜플리") || norm.equals("랜덤재생") || norm.equals("랜덤플레이")
                || norm.equals("randomplay") || norm.equals("random")
                || norm.equals("미리듣기") || norm.equals("preview");
        }
    public static void clear() {
        System.out.print("\u001B[2J\u001B[H");
        System.out.flush();
    }

    /** 표준 콘솔:로고 + 구분선 + 타이틀 */
    public static void header(String title) {
        clear();
        MainView.printLogoStdout(); 
        System.out.println(DIV);
        System.out.println(title);
        System.out.println(DIV);
        System.out.println();
    
    
        if (!isRandomTitle(title)) {
            BgmPlayer.get().ensureLoopingFromResource(BGM_RESOURCE);
        } else {
            BgmPlayer.get().stop();
        }
    }
 // Layout.java 마지막에 추가
    public static void pressAnyKeyToContinue(java.util.Scanner sc) {
        System.out.println("계속하려면 Enter를 누르세요...");
        if (sc != null) sc.nextLine();
    }


    /** JLine 터미널 버전(필요 시) */
    public static void headerJLine(Terminal term, String title) {
        term.puts(InfoCmp.Capability.clear_screen);
        term.flush();
        MainView.printLogoJLine(term);  
        term.writer().println(DIV);
        term.writer().println(title);
        term.writer().println(DIV);
        term.writer().println();
        term.flush();
    }
}
