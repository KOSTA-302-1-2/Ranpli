package cli.ui;

import app.music.BgmPlayer;
import cli.ui.MainView;        
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

public class Layout {
	private static final String BGM_RESOURCE = "/audio/bgm.wav";
    private static final String DIV =
        "::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"
        + "::::::::::::::::::::::::::::::::::::::::::::::::::";
    	   
    private static boolean isRandomTitle(String title) {
        if (title == null) return false;
        String norm = title.replaceAll("\\s+", "").toLowerCase(); // 공백 제거 + 소문자
        // ↓ 어떤 타이틀을 써도 잡히게
        return norm.equals("랜플리") || norm.equals("랜덤재생") || norm.equals("랜덤플레이")
            || norm.equals("randomplay") || norm.equals("random");
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
