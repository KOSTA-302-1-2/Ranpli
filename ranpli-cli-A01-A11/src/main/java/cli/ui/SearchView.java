package cli.ui;

import search.dto.SearchedMusicDTO;
import search.service.HybridSearchService;

import app.music.PlayerController;
import app.music.model.Track;
import playlist.controller.PlaylistController;
import app.repo.TrackDao;

import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.nio.charset.StandardCharsets;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.InfoCmp;

public class SearchView implements Screen {

    private final PlayerController player = new PlayerController();

    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("검색");

        System.out.print("검색어 입력 (뒤로 가려면 Enter만): ");
        String keyword = sc.nextLine().trim();
        if (keyword.isEmpty()) return ViewId.MAIN_MENU;

        List<SearchedMusicDTO> results = safeSearch(keyword);
        if (results.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
            Layout.pressAnyKeyToContinue(sc);
            return ViewId.MAIN_MENU;
        }

        printResults(results);

        while (true) {
            System.out.print("미리듣기할 번호 선택 (B: 뒤로): ");
            String in = sc.nextLine().trim().toLowerCase();
            if (in.equals("b")) return ViewId.MAIN_MENU;

            Integer idx = parseIndex(in, results.size());
            if (idx == null) { System.out.println("잘못된 입력입니다."); continue; }

            SearchedMusicDTO sel = results.get(idx);

            String title  = nn(sel.getMusicTitle());
            String artist = nn(sel.getMusicArtist());
            String album  = nn(sel.getMusicAlbum());
            String url    = nn(sel.getMusicUrl());
            int musicNo   = sel.getMusicNo();

            Track track = new Track(musicNo, -1L, title, artist, url, null);

            Layout.header("미리듣기");

            try {
                player.play(track);
                System.out.printf("▶ 미리듣기 재생: %s - %s%n", artist, title);
                // ✅ 메뉴는 나중에 터미널로 출력할 것이라 여기서는 찍지 않음
                // System.out.println("[P] 일시정지/재개  [N] 다음곡(랜덤 이동)  [S] 저장  [B] 뒤로");
            } catch (Exception e) {
                System.out.println("미리듣기 재생 중 오류 발생");
                e.printStackTrace();
                continue;
            }

            final long TIMEOUT_MS = 31_000;
            final long start = System.currentTimeMillis();

            final int  BAR_W = 42;
            final int  EQ_N  = 48;
            final String[] LV = {" ", "▁","▂","▃","▄","▅","▆","▇","█"};

            long lastDraw = 0;
            final Random rand = new Random();

            try (Terminal term = TerminalBuilder.builder()
                        .system(true)
                        .encoding(StandardCharsets.UTF_8)
                        .build();
                 NonBlockingReader reader = term.reader()) {

                // ▼ 1) 프로그레스/EQ 먼저 출력
                term.writer().println();
                term.writer().println("│" + " ".repeat(EQ_N) + "│");
                term.writer().println("00:00 [" + "-".repeat(BAR_W) + "] 00:31");

                // ▼ 2) 앵커(두 줄 아래) 저장 → 이후 갱신은 항상 이 기준으로
                term.writer().print("\u001B[s");
                term.writer().flush();

                // ▼ 3) 그 다음 줄에 메뉴 출력(= 프로그레스 아래로 이동)
                term.writer().println();
                term.writer().println("[P] 일시정지/재개  [N] 다음곡(랜덤 이동)  [S] 저장  [B] 뒤로");
                term.writer().flush();

                while (System.currentTimeMillis() - start < TIMEOUT_MS) {
                    // --- 키 입력 처리(논블로킹) ---
                    int ch = reader.read(120);
                    if (ch != -1) {
                        char c = Character.toLowerCase((char) ch);
                        if (c == 'p') {
                            player.togglePause();
                        } else if (c == 'n') {
                            cleanupAndClear(term, player);
                            return ViewId.RANDOM;
                        } else if (c == 'b') {
                            cleanupAndClear(term, player);
                            return ViewId.MAIN_MENU;
                        } else if (c == 's') {
                            // 저장
                            if (session == null || session.getUser() == null) {
                                term.writer().println("로그인이 필요합니다.");
                                term.writer().flush();
                            } else {
                                if (musicNo <= 0) {
                                    try {
                                        musicNo = new TrackDao().findOrInsert(title, artist, album, url);
                                    } catch (Exception ex) {
                                        term.writer().println("tb_music 등록 실패");
                                        term.writer().flush();
                                        continue;
                                    }
                                }
                                try {
                                    String userId = session.getUser().getUserId();
                                    PlaylistController.saveMusicToPlaylist(userId, musicNo);
                                    term.writer().println("✓ 플레이리스트에 저장되었습니다.");
                                    term.writer().flush();
                                } catch (Exception ex) {
                                    term.writer().println("저장 중 오류");
                                    term.writer().flush();
                                }
                            }
                        }
                    }

                    // --- EQ/Progress 갱신 (120ms 주기) ---
                    long now = System.currentTimeMillis();
                    if (now - lastDraw >= 120) {
                        lastDraw = now;

                        long elapsed = Math.min(now - start, TIMEOUT_MS);
                        int  fill    = (int) (BAR_W * elapsed / (double) TIMEOUT_MS);
                        String bar   = "▇".repeat(fill) + "-".repeat(Math.max(0, BAR_W - fill));

                        StringBuilder eq = new StringBuilder();
                        eq.append("│");
                        for (int i = 0; i < EQ_N; i++) {
                            int lv = 1 + rand.nextInt(8);
                            eq.append(LV[lv]);
                        }
                        eq.append("│");

                        // 앵커 복원 → 2줄 위로(= EQ/Progress 자리) → 두 줄 덮어쓰기 → 다시 앵커 저장
                        term.writer().print("\u001B[u");
                        term.writer().print("\u001B[2A");
                        term.writer().print("\r");
                        term.writer().println(eq.toString());
                        term.writer().print("\r");
                        term.writer().println(mmss(elapsed) + " [" + bar + "] " + mmss(TIMEOUT_MS));
                        term.writer().print("\u001B[s");
                        term.writer().flush();
                    }
                }

                // 타임아웃(자동 전환)
                cleanupAndClear(term, player);
                return ViewId.RANDOM;

            } catch (Exception ignore) {
                // 터미널 문제 시, 단순 대기 후 랜덤으로
                try {
                    Thread.sleep(Math.max(0, TIMEOUT_MS - (System.currentTimeMillis() - start)));
                } catch (InterruptedException ignored) {}
                return ViewId.RANDOM;
            }
        }
    }

    // 화면 전환 전, 재생 중지 + JLine로 화면 초기화
    private static void cleanupAndClear(Terminal term, PlayerController player) {
        try { player.stop(); } catch (Exception ignore) {}
        try {
            term.puts(InfoCmp.Capability.clear_screen);
            term.writer().print("\u001B[0m");
            term.writer().print("\u001B[?25h"); // 커서 보이기
            term.flush();
        } catch (Exception ignore) {}
    }

    private static void printResults(List<SearchedMusicDTO> results) {
        System.out.println();
        System.out.println("번호 | 제목 - 아티스트 [앨범]");
        System.out.println("----+---------------------------------------------");
        for (int i = 0; i < results.size(); i++) {
            SearchedMusicDTO m = results.get(i);
            System.out.printf("%3d | %s - %s [%s]%n",
                    i, nn(m.getMusicTitle()), nn(m.getMusicArtist()), nn(m.getMusicAlbum()));
        }
        System.out.println();
    }

    private static List<SearchedMusicDTO> safeSearch(String keyword) {
        try { return new HybridSearchService().search(keyword, 30); }
        catch (Exception e) { return java.util.Collections.emptyList(); }
    }

    private static Integer parseIndex(String s, int size) {
        try { int n = Integer.parseInt(s); return (0 <= n && n < size) ? n : null; }
        catch (Exception e) { return null; }
    }

    private static String nn(String s) { return s == null ? "" : s; }

    private static String mmss(long ms) {
        long s = ms / 1000;
        return String.format("%02d:%02d", s / 60, s % 60);
    }
}
