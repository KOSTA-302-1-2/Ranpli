package cli.ui;

import app.config.Config;

import java.util.Arrays;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

/**
 * 메인 진입 + 공통 로고 + 재생세션(NowPlayingSession) 보관
 * - printLogoStdout / printLogoJLine : 모든 화면 상단에서 공통으로 사용
 * - NowPlayingSession : RandomPlayView에서 그대로 호출
 * - main() : ViewRouter로 화면만 제어 (기능 로직은 분리된 뷰에서 수행)
 */
public class MainView {

    // ─────────────────────────────────────────────────────────────
    //  로고 도안 (8행 고정, 각 행 길이 10칸 고정)
    // ─────────────────────────────────────────────────────────────
    public static final String[] LOGO_MAP = new String[]{
        // R
        "#######   ",
        "##   ##   ",
        "##   ##   ",
        "#######   ",
        "##  ##    ",
        "##   ##   ",
        "##   ##   ",
        "          ",

        // A
        " #####    ",
        "##   ##   ",
        "##   ##   ",
        "#######   ",
        "##   ##   ",
        "##   ##   ",
        "##   ##   ",
        "          ",

        // N
        "##     ## ",
        "###    ## ",
        "## #   ## ",
        "##  #  ## ",
        "##   # ## ",
        "##    ### ",
        "##     ## ",
        "          ",

        // P
        "#######   ",
        "##    ##  ",
        "##    ##  ",
        "#######   ",
        "##        ",
        "##        ",
        "##        ",
        "          ",

        // L
        "##        ",
        "##        ",
        "##        ",
        "##        ",
        "##        ",
        "##        ",
        "########  ",
        "          ",

        // I
        "########  ",
        "   ##     ",
        "   ##     ",
        "   ##     ",
        "   ##     ",
        "   ##     ",
        "########  ",
        "          "
    };

    // ─────────────────────────────────────────────────────────────
    //  로고 스타일 (색/간격/그림자)
    // ─────────────────────────────────────────────────────────────
    public static final String COLOR_MAIN   = "\u001B[38;5;15m";   // 본문: 화이트
    public static final String COLOR_SHADOW = "\u001B[38;5;99m";   // 그림자: 퍼플(차분)
    public static final String RESET        = "\u001B[0m";

    // 폭 일치가 핵심 (둘 다 2칸)
    public static final String FILL_CHAR = "##";
    public static final String SPACE     = "  ";

    // 여백/간격 및 드롭 섀도우 오프셋
    public static final int PAD = 2;       // 좌여백
    public static final int GAP = 4;       // 글자 간격
    public static final int SHADOW_DX = 2; // 그림자 오른쪽 오프셋
    public static final int SHADOW_DY = 1; // 그림자 아래 오프셋 (행 순서상 shadow를 먼저 출력)
    
    

    // ─────────────────────────────────────────────────────────────
    //  로고 출력: 표준 콘솔
    // ─────────────────────────────────────────────────────────────
    public static void printLogoStdout() {
        int pad = 2, gap = 2;
        int rowsPerLetter = 8;
        int letters = LOGO_MAP.length / rowsPerLetter;
        int cols = LOGO_MAP[0].length(); // 모든 줄 길이는 동일해야 함

        for (int row = 0; row < rowsPerLetter; row++) {
            StringBuilder line = new StringBuilder(" ".repeat(pad));
            for (int letter = 0; letter < letters; letter++) {
                // 현재 글자 블록에서 row 줄 문자열
                String cur = LOGO_MAP[letter * rowsPerLetter + row];

                for (int c = 0; c < cols; c++) {
                    boolean face = cur.charAt(c) == '#';

                    // 이웃(상하좌우대각선)에 #가 하나라도 있으면 outline
                    boolean neighbor = false;
                    if (!face) {
                        for (int dr = -1; dr <= 1 && !neighbor; dr++) {
                            int rr = row + dr;
                            if (rr < 0 || rr >= rowsPerLetter) continue;
                            String neigh = LOGO_MAP[letter * rowsPerLetter + rr];
                            for (int dc = -1; dc <= 1; dc++) {
                                if (dr == 0 && dc == 0) continue;
                                int cc = c + dc;
                                if (cc < 0 || cc >= cols) continue;
                                if (neigh.charAt(cc) == '#') { neighbor = true; break; }
                            }
                        }
                    }

                    if (face) {
                        line.append(COLOR_MAIN).append(FILL_CHAR).append(RESET);
                    } else if (neighbor) {
                        line.append(COLOR_SHADOW).append(FILL_CHAR).append(RESET);
                    } else {
                        line.append(SPACE);
                    }
                }
                line.append(" ".repeat(gap));
            }
            System.out.println(line.toString());
        }
        System.out.println();
    }

    public static void printLogoJLine(Terminal term) {
        int pad = 2, gap = 2;
        int rowsPerLetter = 8;
        int letters = LOGO_MAP.length / rowsPerLetter;
        int cols = LOGO_MAP[0].length();

        for (int row = 0; row < rowsPerLetter; row++) {
            StringBuilder line = new StringBuilder(" ".repeat(pad));
            for (int letter = 0; letter < letters; letter++) {
                String cur = LOGO_MAP[letter * rowsPerLetter + row];

                for (int c = 0; c < cols; c++) {
                    boolean face = cur.charAt(c) == '#';
                    boolean neighbor = false;
                    if (!face) {
                        for (int dr = -1; dr <= 1 && !neighbor; dr++) {
                            int rr = row + dr;
                            if (rr < 0 || rr >= rowsPerLetter) continue;
                            String neigh = LOGO_MAP[letter * rowsPerLetter + rr];
                            for (int dc = -1; dc <= 1; dc++) {
                                if (dr == 0 && dc == 0) continue;
                                int cc = c + dc;
                                if (cc < 0 || cc >= cols) continue;
                                if (neigh.charAt(cc) == '#') { neighbor = true; break; }
                            }
                        }
                    }

                    if (face) {
                        line.append(COLOR_MAIN).append(FILL_CHAR).append(RESET);
                    } else if (neighbor) {
                        line.append(COLOR_SHADOW).append(FILL_CHAR).append(RESET);
                    } else {
                        line.append(SPACE);
                    }
                }
                line.append(" ".repeat(gap));
            }
            term.writer().println(line.toString());
        }
        term.writer().println();
        term.flush();
    }

    // ─────────────────────────────────────────────────────────────
    //  메인(엔트리) : 화면 라우터 실행
    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { Config.load(); } catch (Throwable ignore) {}
        try (Terminal term = TerminalBuilder.builder().system(true).jna(true).build()) {
            term.enterRawMode();
            AsciiLogoAnimator.animateLogoJLine(term);
        } catch (Exception ignore) {}

        var session = new cli.ui.UserSession();
        var router  = new cli.ui.ViewRouter(session);
        router.run(); // 메인 메뉴부터 시작 (랜플리는 RandomPlayView에서 기능 그대로)
    }

    // ─────────────────────────────────────────────────────────────
    //  재생 세션 (JLine 기반)
    //  RandomPlayView에서: MainView.NowPlayingSession.runJLine(player, random)
    // ─────────────────────────────────────────────────────────────
 // MainView 클래스 안에 추가 (printLogo들 아래 아무 곳)
    static class AsciiLogoAnimator {
        // 마리오 스프라이트 (작게, 3행)
        private static final String[] MARIO = {
            "  ██ ",
            " ████",
            "█ ██ "
        };
        

        // 색상
        private static final String FG = COLOR_MAIN;     // 로고 색 (기존 흰색)
        private static final String SH = COLOR_SHADOW;   // 그림자 색
        private static final String RS = RESET;

        // 로고 1픽셀의 가로폭: FILL_CHAR 길이(2칸) 기준
        private static final int CELL_W = FILL_CHAR.length(); // 2
        private static final int GAP = 2;   // 글자 간격(로고에서 쓰던 것)

        // 인트로 시작 위치/여백
        private static final int ORIGIN_ROW = 2;   // 화면 위 여백(줄)
        private static final int ORIGIN_COL = 4;   // 화면 왼 여백(칸)

        // 프레임 레이트
        private static final int STEP_SLEEP_MS = 14; // 마리오 이동 1스텝 지연
        private static final int PIXEL_SLEEP_MS = 6; // 픽셀 찍은 직후 살짝 지연
     // 로고 캔버스 크기 계산(문자칸 기준)
        private static final int ROWS = 8;                        // LOGO_MAP 행
        private static final int COLS = LOGO_MAP[0].length();     // 각 행 길이(문자)
        private static final int LETTERS = LOGO_MAP.length / ROWS;
        private static final int CANVAS_W = LETTERS * (COLS * CELL_W + GAP) - GAP; // 전체 너비(터미널 칸)

        // 찍은 픽셀(문자칸 단위)이 채워졌는지 기록(화면 좌표 기준)
        private static final java.util.Set<Long> painted = new java.util.HashSet<>();
        private static long key(int row, int col){ return (((long)row)<<32) | (col & 0xffffffffL); }


        /** 커서 이동 */
        private static void gotoxy(Terminal t, int row, int col) {
            t.writer().print("\u001B[" + Math.max(1,row) + ";" + Math.max(1,col) + "H");
        }
        /** 라인 지우기 */
        private static void clrline(Terminal t){ t.writer().print("\r\u001B[2K"); }
        /** 전체 클리어 */
        private static void clear(Terminal t){ t.puts(InfoCmp.Capability.clear_screen); t.flush(); }

        /** 한 칸(로고 픽셀) 칠하기: 본문 + 아웃라인 */
        private static void paintPixel(Terminal t, int row, int col){
            // 본문
            gotoxy(t, row, col);
            t.writer().print(FG + FILL_CHAR + RS);
            // 간단한 테두리 효과(오른쪽/아래 그림자)
            gotoxy(t, row+1, col);
            t.writer().print(SH + FILL_CHAR + RS);
            gotoxy(t, row, col+CELL_W);
            // (원하면 여기도 약한 음영 넣을 수 있음)
            t.flush();
            gotoxy(t, row, col);
            t.writer().print(FG + FILL_CHAR + RS);
            // 아래쪽으로 간단 그림자
            gotoxy(t, row+1, col);
            t.writer().print(SH + FILL_CHAR + RS);
            t.flush();

            // 메모리에 기록(본문 칸만 기록)
            painted.add(key(row, col));
            
        }
        private static final int MARIO_H = 3;
        private static final int MARIO_W = 5; // "  ██ " 기준 대략 폭

        private static void drawMario(Terminal t, int row, int col){
            for (int r=0; r<MARIO.length; r++){
                gotoxy(t, row+r, col);
                // 해당 너비만 “스프라이트”로 덮어씀 (전체 라인 클리어 X)
                t.writer().print(MARIO[r]);
            }
            t.flush();
        }

        /** 마리오 지울 때는, 스프라이트 영역만 원래 배경(픽셀/빈칸)으로 복원 */
        private static void eraseMario(Terminal t, int row, int col){
            for (int r=0; r<MARIO_H; r++){
                // 스프라이트가 차지하던 영역 폭만큼 한 칸=문자단위로 복원
                int y = row + r;
                gotoxy(t, y, col);

                StringBuilder line = new StringBuilder();
                for (int x = 0; x < MARIO_W; x++){
                    int cx = col + x;

                    // 이 위치가 “픽셀의 왼쪽 칸”인지 체크 (FILL_CHAR 폭=2칸이므로 왼칸만 기록)
                    boolean isPixelLeft = painted.contains(key(y, cx));
                    if (isPixelLeft) {
                        line.append(FG).append(FILL_CHAR).append(RS);
                        x += (CELL_W - 1); // 2칸 폭을 한 번에 그렸으므로 추가 보정
                    } else {
                        // 아니면 그냥 공백
                        line.append(" ");
                    }
                }
                // 영역 출력
                t.writer().print(line.toString());
            }
            t.flush();
        }

        /** 로고의 모든 '#' 좌표를 화면 좌표로 변환해 순서 리스트 생성 (왼→오, 위→아래 래스터 스캔) */
        private static java.util.List<int[]> buildPixelTargets(){
            int rowsPerLetter = 8;
            int letters = LOGO_MAP.length / rowsPerLetter; // 6글자
            int colsPerLetter = LOGO_MAP[0].length();      // 각 행 문자열 길이(고정)
            java.util.List<int[]> pts = new java.util.ArrayList<>();

            // 시작 위치
            int x = ORIGIN_COL;
            int y = ORIGIN_ROW;

            for (int L=0; L<letters; L++){
                // 글자 L 에 대한 좌표들 (지그재그로 모으기)
                java.util.List<int[]> letterPts = new java.util.ArrayList<>();

                for (int r=0; r<rowsPerLetter; r++){
                    String line = LOGO_MAP[L*rowsPerLetter + r];

                    // 짝수행은 왼→오, 홀수행은 오→왼 (마리오가 덜 튀게)
                    if ((r % 2) == 0) {
                        for (int c=0; c<colsPerLetter; c++){
                            if (line.charAt(c) == '#') {
                                int px = x + c*CELL_W;
                                int py = y + r;
                                letterPts.add(new int[]{py, px});
                            }
                        }
                    } else {
                        for (int c=colsPerLetter-1; c>=0; c--){
                            if (line.charAt(c) == '#') {
                                int px = x + c*CELL_W;
                                int py = y + r;
                                letterPts.add(new int[]{py, px});
                            }
                        }
                    }
                }

                // 글자 단위로 이어붙이기 (R 끝 → A 시작 → ...)
                pts.addAll(letterPts);

                // 다음 글자 시작 X 로 이동
                x += colsPerLetter * CELL_W + GAP;
            }
            return pts;
        }

        /** (y0,x0) -> (y1,x1)로 점프하는 작은 포물선 이동 */
        private static void jumpTo(Terminal t, int y0, int x0, int y1, int x1){
            int steps = Math.max(8, (int)(Math.hypot(y1-y0, (x1-x0)) / 2));
            for (int s=1; s<=steps; s++){
                double t01 = s / (double) steps;             // 0..1
                double nx = x0 + (x1 - x0) * t01;            // 선형 x
                // 부드러운 점프: 포물선(높이 스케일 3~5)
                double arc = Math.sin(Math.PI * t01);        // 0..1..0
                double ny = y0 + (y1 - y0) * t01 - arc * 3;  // 위로 솟게
                int ix = (int)Math.round(nx);
                int iy = (int)Math.round(ny);

                // 그리기
                drawMario(t, iy, ix);
                try { Thread.sleep(STEP_SLEEP_MS); } catch(Exception ignore){}
                // 다음 프레임 위해 지우기
                eraseMario(t, iy, ix);
            }
            // 착지 지점에 최종 마리오 표시(살짝)
            drawMario(t, y1, x1);
            try { Thread.sleep(PIXEL_SLEEP_MS); } catch(Exception ignore){}
            eraseMario(t, y1, x1);
        }

        /** 메인: 빈 화면 → 마리오가 점프하며 픽셀 찍어서 로고 완성 */
        static void animateLogoJLine(Terminal term){
            clear(term);
            // 캔버스 안내(선택)
            gotoxy(term, ORIGIN_ROW-1, ORIGIN_COL);
            term.writer().println(" ");
            term.flush();

            var targets = buildPixelTargets();
            // 시작 위치: 좌상단 부근
            int curY = ORIGIN_ROW + 6;
            int curX = ORIGIN_COL - 2;

            for (int[] pt : targets){
                int ty = pt[0], tx = pt[1];
                // 점프해서 이동
                jumpTo(term, curY, curX, ty-1, tx-1); // 픽셀 왼위 한칸 위에 착지
                // 픽셀 찍기
                paintPixel(term, ty, tx);
                // 현재 위치 갱신
                curY = ty-1; curX = tx-1;
            }
            // 끝난 뒤 커서 아래로 내려 한 줄 여백
            gotoxy(term, curY + 4, ORIGIN_COL);
            term.writer().println();
            term.flush();
        }
    }

    public static class NowPlayingSession {
        private static final int TOTAL_SECONDS = 30;  // 미리듣기 30초
        private static final int TICK_MS = 100;       // 진행바 갱신 주기 100ms
     // ── EQ 설정 (세로형 막대 그래프)
        private static final int EQ_BANDS = 12;   // 막대 개수
        private static final int EQ_MAX   = 15;    // 막대 최대 높이(줄)
        private static int[] eqLevels = new int[EQ_BANDS]; // 현재 높이 상태


        // 진행바
        static class ProgressBar {
            final int totalSec;
            long startEpochMs = System.currentTimeMillis();
            long pausedAccumMs = 0;
            long pauseStartMs = 0;
            boolean paused = false;

            ProgressBar(int totalSec) { this.totalSec = totalSec; }
            void reset() {
                startEpochMs = System.currentTimeMillis();
                pausedAccumMs = 0;
                pauseStartMs = 0;
                paused = false;
            }
            void toggle(){ if (paused) resume(); else pause(); }
            void pause(){ if(!paused){ paused = true; pauseStartMs = System.currentTimeMillis(); } }
            void resume(){
                if(paused){
                    paused = false;
                    pausedAccumMs += System.currentTimeMillis() - pauseStartMs;
                    pauseStartMs = 0;
                }
            }
            boolean isPaused(){ return paused; }
            int elapsedSec(){
                long base = paused ? pauseStartMs : System.currentTimeMillis();
                long ms = (base - startEpochMs) - pausedAccumMs;
                if (ms < 0) ms = 0;
                int s = (int)(ms / 1000L);
                return Math.min(s, totalSec);
            }
            String render(){
                int elapsed = elapsedSec();
                int width = 30;
                int filled = (int)Math.round((elapsed/(double)totalSec)*width);
                if (filled < 0) filled = 0; if (filled > width) filled = width;
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i=0;i<width;i++) sb.append(i<filled ? '▇' : '-');
                sb.append(']').append(' ').append(elapsed).append("s / ").append(totalSec).append("s");
                if (paused) sb.append("  (PAUSED)");
                return sb.toString();
            }
        }

        static void drawProgress(Terminal t, String s){
            t.writer().print("\r\u001B[2K");
            t.writer().print(s);
            t.flush();
        }
        static void println(Terminal t, String s){ t.writer().println(s); }

        /**
         * 기존 재생 루프 (플레이어/랜덤 엔진은 호출 측에서 주입)
         * player: app.music.PlayerController
         * random: app.music.RandomEngine
         */
        
        private static void ensureEqCapacityForTerminal(Terminal t) {
            int cols = Math.max(0, t.getWidth());
            int desiredBands = Math.max(8, cols / 2); // 최소 8개, 가로폭/2 만큼
            if (eqLevels.length != desiredBands) {
                eqLevels = Arrays.copyOf(eqLevels, desiredBands);
                // 새로 생긴 슬롯은 0으로 초기화 → 바닥에서부터 차오르는 효과
                for (int i = 0; i < eqLevels.length; i++) {
                    if (eqLevels[i] < 0 || eqLevels[i] > EQ_MAX) eqLevels[i] = 0;
                }
            }
        }

        /** 진행바 바로 아래의 EQ 라인을 터미널 폭에 정확히 맞춰 채우기(패딩 포함) */
        private static void drawVerticalEQFrame(Terminal t) {
            int cols = Math.max(0, t.getWidth());
            int bands = eqLevels.length;              // 현재 밴드 수 (터미널 폭 기반)
            int lineWidth = bands * 2;                // "▇ " 기준 2칸 * 밴드수
            int pad = Math.max(0, cols - lineWidth);  // 남는 오른쪽 여백

            // 진행바 바로 아래로 1줄 내려감
            t.writer().print("\u001B[1B");
            // 위에서 아래로(높은 줄부터) 그리기
            for (int row = EQ_MAX; row >= 1; row--) {
                StringBuilder sb = new StringBuilder();
                sb.append("\r\u001B[2K"); // 해당 줄 전체 클리어
                for (int b = 0; b < bands; b++) {
                    sb.append(eqLevels[b] >= row ? "▇ " : "  ");
                }
                if (pad > 0) sb.append(" ".repeat(pad)); // 오른쪽 여백도 꽉 채우기
                t.writer().println(sb.toString());
            }
            // (EQ_MAX + 우리가 내린 1줄) 만큼 위로 올려서 진행바 줄로 복귀
            t.writer().print("\u001B[" + (EQ_MAX + 1) + "A");
            t.flush();
       
        
        }
        
        private static void stepEqLevels() {
            for (int i = 0; i < eqLevels.length; i++) {
                int delta;
                double r = Math.random();
                if (r < 0.10)       delta = (int)(Math.random()*5) - 2; // -2..+2 (가끔 크게)
                else                delta = (int)(Math.random()*3) - 1; // -1..+1 (보통)
                int v = eqLevels[i] + delta;
                if (v < 0) v = 0;
                if (v > EQ_MAX) v = EQ_MAX;
                eqLevels[i] = v;
            }
        }

        public static void runJLine(app.music.PlayerController player, app.music.RandomEngine random) {
            Terminal term = null;
            NonBlockingReader in = null;

            try {
                term = TerminalBuilder.builder().system(true).jna(true).build();
                term.enterRawMode();
                in = term.reader();

                // 화면 초기화 + 고정 텍스트
                term.puts(InfoCmp.Capability.clear_screen);
                term.flush();
                cli.ui.MainView.printLogoJLine(term);
                println(term, "============================================================ NOW PLAYING =============================================================");
                               
               
                var t = player.current();
                if (t != null) println(term, "♪ " + t.title() + " - " + t.artist());
                println(term, "");
                println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [M] 메뉴");
                println(term, "");

                var bar = new ProgressBar(TOTAL_SECONDS); 
             // 터미널 폭에 맞춰 EQ 밴드 수 준비 & 모두 0으로 시작(바닥에서 위로 차오르게)
                ensureEqCapacityForTerminal(term);
                Arrays.fill(eqLevels, 0);


                while (true) {
                    // 1) 진행바 갱신
                    drawProgress(term, bar.render());
                 // 터미널 크기 변화 대응(창 너비 바뀌면 밴드 수도 즉시 갱신)
                    ensureEqCapacityForTerminal(term);

                    // 세로 EQ 갱신 및 렌더 (오른쪽 여백까지 꽉 채움)
                    stepEqLevels();
                    drawVerticalEQFrame(term);


                    // 2) 입력 즉시 처리
                    while (in.ready()) {
                        int ch = in.read();
                        if (ch == -1) break;
                        ch = Character.toLowerCase(ch);
                        if (ch == '\r' || ch == '\n') continue;

                        switch (ch) {
                            case 'p' -> { player.togglePause(); bar.toggle(); }
                            case 'b' -> {
                                var prev = player.loadPrevious();
                                if (prev != null) {
                                    bar.reset();
                                    term.puts(InfoCmp.Capability.clear_screen);
                                    term.flush();
                                    cli.ui.MainView.printLogoJLine(term);
                                    println(term, "============================================================ NOW PLAYING =============================================================");
                                    println(term, "♪ " + prev.title() + " - " + prev.artist());
                                    println(term, "");
                                    println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [M] 메뉴");
                                    println(term, "");
                                }
                            }
                            case 'n' -> {
                                var next = random.pickOneAndPersist();
                                if (next != null) {
                                    player.play(next);
                                    bar.reset();
                                    term.puts(InfoCmp.Capability.clear_screen);
                                    cli.ui.MainView.printLogoJLine(term);
                                    term.flush();
                                    println(term, "============================================================ NOW PLAYING =============================================================");
                                    println(term, "♪ " + next.title() + " - " + next.artist());
                                    println(term, "");
                                    println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [M] 메뉴");
                                    println(term, "");
                                }
                            }
                            case 'm' -> { player.stop(); return; }
                            default -> { /* ignore */ }
                        }
                    }

                    // 3) 자동 다음곡
                    if (!bar.isPaused() && bar.elapsedSec() >= TOTAL_SECONDS) {
                        var next = random.pickOneAndPersist();
                        if (next != null) {
                            player.play(next);
                            bar.reset();
                            term.puts(InfoCmp.Capability.clear_screen);
                            cli.ui.MainView.printLogoJLine(term);
                            term.flush();
                            println(term, "============================================================ NOW PLAYING =============================================================");
                            println(term, "♪ " + next.title() + " - " + next.artist());
                            println(term, "");
                            println(term, "[P] 일시정지/재생   [B] 이전곡   [N] 다음곡 [M] 메뉴");
                            println(term, "");
                        } else {
                            player.stop();
                            return;
                        }
                    }

                    // 4) 틱
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
