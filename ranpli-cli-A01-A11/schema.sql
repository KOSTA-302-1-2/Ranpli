-- === Ranpli ERD 스키마 ===
CREATE DATABASE IF NOT EXISTS ranpli
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;


-- 기존 테이블 있으면 삭제 (FK 순서 고려)
DROP TABLE IF EXISTS tb_playlist_detail;
DROP TABLE IF EXISTS tb_playlist;
DROP TABLE IF EXISTS tb_music;
DROP TABLE IF EXISTS tb_members;

-- =====================
-- 회원: tb_members
-- =====================
CREATE TABLE tb_members (
  users_no           INT NOT NULL AUTO_INCREMENT,
  users_id           VARCHAR(20) NOT NULL,
  users_pwd          VARCHAR(20) NOT NULL,
  users_reg_date     TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  users_delete_date  TIMESTAMP NULL,
  PRIMARY KEY (users_no),
  UNIQUE KEY uk_members_users_id (users_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================
-- 음악: tb_music
-- =====================
CREATE TABLE tb_music (
  music_no        INT NOT NULL AUTO_INCREMENT,
  music_title     VARCHAR(30) NOT NULL,
  music_artist    VARCHAR(30) NOT NULL,
  music_album     VARCHAR(30) NULL,
  is_before_music INT NOT NULL DEFAULT 0,
  music_url       VARCHAR(1024) NOT NULL,
  PRIMARY KEY (music_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================
-- 플레이리스트: tb_playlist
-- =====================
CREATE TABLE tb_playlist (
  playlist_no INT NOT NULL AUTO_INCREMENT,
  uses_no     INT NOT NULL,
  PRIMARY KEY (playlist_no),
  KEY idx_playlist_uses_no (uses_no),
  CONSTRAINT fk_playlist_member FOREIGN KEY (uses_no)
    REFERENCES tb_members (users_no)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================
-- 플레이리스트 상세: tb_playlist_detail
-- =====================
CREATE TABLE tb_playlist_detail (
  playlist_detail_no INT NOT NULL AUTO_INCREMENT,
  playlist_no        INT NOT NULL,
  music_no           INT NOT NULL,
  music_save_time    TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (playlist_detail_no),
  KEY idx_pldet_playlist_no (playlist_no),
  KEY idx_pldet_music_no (music_no),
  CONSTRAINT fk_pldet_playlist FOREIGN KEY (playlist_no)
    REFERENCES tb_playlist (playlist_no)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_pldet_music FOREIGN KEY (music_no)
    REFERENCES tb_music (music_no)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================
-- (선택) 중복 방지 유니크 키
-- =====================
-- 곡(title+artist+url) 중복 방지하려면 아래 실행
-- ALTER TABLE tb_music ADD UNIQUE KEY uk_music_tat (music_title, music_artist, music_url);
