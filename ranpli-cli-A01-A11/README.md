# ranpli-cli (A01~A11 구현 + 나머지 틀)

## 구현된 것 (음악 재생 A01~A11)
- A01 랜덤 선택
- A02 데이터 로드
- A03 30초 재생
- A04 정지
- A05 재재생(Resume)
- A06 이전 곡 기록
- A07 이전 곡 로드
- A08 이전 곡 재생
- A09 다음 곡 랜덤 선택
- A10 다음 곡 데이터 로드
- A11 다음 곡 재생

* 랜덤 소스: Apple RSS Top Songs → iTunes Lookup으로 previewUrl 확보 → tracks 테이블에 UPSERT

## 틀만 만든 것 (TODO 주석 포함)
- 회원가입/로그인/프로필/플레이리스트/검색 등의 메뉴와 클래스 골격

## 실행
1) MySQL에서 `schema.sql` 실행
2) `src/main/resources/application.properties` 수정
3) 
```bash
mvn -q compile
mvn -q exec:java
```
