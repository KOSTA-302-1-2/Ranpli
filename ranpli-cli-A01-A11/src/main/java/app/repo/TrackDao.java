package app.repo;

import app.db.Db;
import java.sql.*;

/**
 * ERD 기준 tb_music DAO
 * - (music_title, music_artist, music_url)로 먼저 조회 → 없으면 INSERT → music_no 반환
 */
public class TrackDao {

  /** 존재하면 ID 반환, 없으면 INSERT 후 생성된 music_no 반환 */
  public int findOrInsert(String title, String artist, String album, String url) {
    Integer id = findIdByNaturalKey(title, artist, url);
    if (id != null) return id;

    final String sql = """
        INSERT INTO tb_music (music_title, music_artist, music_album, is_before_music, music_url)
        VALUES (?,?,?,?,?)
        """;
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, title);
      ps.setString(2, artist);
      ps.setString(3, album);
      ps.setInt(4, 0);
      ps.setString(5, url);
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }

      // 동시성으로 이미 생겼을 수 있으니 재조회
      id = findIdByNaturalKey(title, artist, url);
      if (id != null) return id;

      throw new RuntimeException("tb_music INSERT 후 PK를 가져오지 못했습니다.");
    } catch (SQLException e) {
      // 문제 원인 바로 보이도록 감싸서 throw
      throw new RuntimeException("tb_music INSERT 실패: " + e.getMessage(), e);
    }
  }

  /** (title, artist, url) 조합으로 tb_music 조회 */
  public Integer findIdByNaturalKey(String title, String artist, String url) {
    final String q = """
        SELECT music_no
        FROM tb_music
        WHERE music_title = ? AND music_artist = ? AND music_url = ?
        LIMIT 1
        """;
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(q)) {
      ps.setString(1, title);
      ps.setString(2, artist);
      ps.setString(3, url);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
        return null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("tb_music 조회 실패: " + e.getMessage(), e);
    }
  }
}
