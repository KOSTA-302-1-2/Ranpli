package cli.ui;

import search.dto.SearchedMusicDTO;
import search.service.HybridSearchService;

import app.music.PlayerController;
import app.music.model.Track;

// 저장
import playlist.controller.PlaylistController;
// import playlist.exception.DuplicateMusicException; // 컨트롤러가 예외 안 던지면 굳이 필요 없음

import java.util.List;
import java.util.Scanner;

// JLine (논블로킹 키 입력)
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

// ★ 추가: DB에 없을 때 보장 저장용 DAO
import app.repo.TrackDao;

public class SearchView implements Screen {

    private final PlayerController player = new PlayerController();

    @Override
    public ViewId render(UserSession session, Scanner sc) {
        Layout.header("                                                            검색");

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

            // DTO → 값 추출
            String title  = nn(sel.getMusicTitle());
            String artist = nn(sel.getMusicArtist());
            String album  = nn(sel.getMusicAlbum());
            String url    = nn(sel.getMusicUrl());

            // musicNo : 검색 결과가 DB에 없으면 0일 수 있음
            int musicNo = sel.getMusicNo();

            // Track(record) = (musicNo, itunesTrackId, title, artist, previewUrl, artworkUrl)
            Track track = new Track(musicNo, -1L, title, artist, url, null);

            // 미리듣기 화면(무음)으로 전환
            Layout.header("미리듣기");

            // 재생 시작 (터미널 열기 전: System.out 사용 OK)
            try {
                player.play(track);
                System.out.printf("▶ 미리듣기 재생: %s - %s%n", artist, title);
                System.out.println("[P] 일시정지/재개  [N] 다음곡(랜덤 이동)  [S] 저장");
            } catch (Exception e) {
                System.out.println("미리듣기 재생 중 오류 발생");
                e.printStackTrace();
                continue;
            }

            // 31초 동안 논블로킹으로 키 처리(P/N/S)
            long start = System.currentTimeMillis();
            long TIMEOUT_MS = 31_000;

            Terminal term = null;
            NonBlockingReader reader = null;
            try {
                term = TerminalBuilder.builder()
                        .system(true)    // OS 기본 인코딩 사용 (랜덤 화면과 동일)
                        .jna(true)
                        .build();
                reader = term.reader();

                while (System.currentTimeMillis() - start < TIMEOUT_MS) {
                    int ch = reader.read(200); // 200ms 대기
                    if (ch == -1) continue;

                    char c = Character.toLowerCase((char) ch);
                    if (c == 'p') {
                        player.togglePause();
                    } else if (c == 'n') {
                        // 랜덤 화면으로 이동 (raw 모드 정리 후)
                        player.stop();
                        safeReset(term);
                        safeClose(term, reader);
                        Layout.clear();
                        return ViewId.RANDOM;
                    } else if (c == 's') {  // ★ 저장 처리
                        if (session == null || session.getUser() == null) {
                            wprintln(term, "로그인이 필요합니다.");
                            continue;
                        }

                        // DB에 아직 없으면 먼저 넣고 music_no 확보
                        if (musicNo <= 0) {
                            try {
                                musicNo = new TrackDao().findOrInsert(title, artist, album, url);
                            } catch (Exception ex) {
                                wprintln(term, "저장 준비 중 오류가 발생했습니다. (tb_music 등록 실패)");
                                continue;
                            }
                        }

                        // 확보된 music_no로 저장 실행
                        try {
                            String userId = session.getUser().getUserId();
                            PlaylistController.saveMusicToPlaylist(userId, musicNo);
                            wprintln(term, "✓ 플레이리스트에 저장되었습니다.");
                        } catch (Exception ex) {
                            wprintln(term, "저장 중 오류가 발생했습니다.");
                        }
                    }
                } // while
            } catch (Exception ignore) {
                try {
                    Thread.sleep(Math.max(0, TIMEOUT_MS - (System.currentTimeMillis() - start)));
                } catch (InterruptedException ignored) {}
            } finally {
                // raw 모드/스타일 정리 + 터미널 닫기
                safeReset(term);
                safeClose(term, reader);
            }

            // 자동 전환 (미리듣기 끝)
            player.stop();
            Layout.clear();
            return ViewId.RANDOM;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 헬퍼들
    // ─────────────────────────────────────────────────────────────

    /** raw 모드에서 안전하게 한 줄 출력 */
    private static void wprintln(Terminal term, String msg) {
        try {
            if (term != null) {
                term.writer().println(msg);
                term.writer().flush();
            } else {
                System.out.println(msg);
            }
        } catch (Exception ignore) {}
    }

    /** ANSI 스타일 리셋 */
    private static void safeReset(Terminal term) {
        try {
            if (term != null) {
                term.writer().print("\u001B[0m");
                term.writer().flush();
            }
        } catch (Exception ignore) {}
    }

    /** 터미널/리더 안전 종료 */
    private static void safeClose(Terminal term, NonBlockingReader reader) {
        try { if (reader != null) reader.close(); } catch (Exception ignore) {}
        try { if (term != null) term.close(); } catch (Exception ignore) {}
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
}
