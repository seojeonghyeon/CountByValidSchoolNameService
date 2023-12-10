# 첨부 파일에서 유효한 학교 이름을 찾아내 학교별로 카운트

## CONDITIONS
- 첨부된 댓글리스트 파일에서 유효한 학교 이름을 찾아내고 학교별로 카운트
- Java 8 또는 17 중 자유롭게 선택
- 결과는 result.txt, 로그는 result.log로 저장
- 학교이름과 숫자 사이에는 탭문자가 들어감(ex. OO중학교\t192\nOO고등학교\t254\n)

## OUTPUT
- /output/result.txt : 출력 결과물
- /output/result.log : 출력 Log

## JDK
- openjdk-18

## 설치 및 사용 방법
1. src/main/resources/csv 내 comments.csv 파일 이름으로 댓글 CSV 파일 업로드
2. src/main/java/me/justin/Main Class 내 main Method 실행
3. /output/ Directory 내 result.txt(출력 결과), result.log(로그 파일)로 결과물 확인

## 참고
- [학교 알리미] (https://www.schoolinfo.go.kr/)