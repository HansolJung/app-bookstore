# app-bookstore

applicaion.yml 의 DB 연결 설정은 다음과 같습니다. 해당 부분을 교체하면 됩니다.  

url: jdbc:mariadb://localhost:3306/sp_db  
username: root  
password: 1234


DDL은 Script-bookstore.sql 파일 안에 있는 스크립트로 생성하면 됩니다.  
기본적인 도서 데이터 세팅은 사진이 없는 상태입니다.  
도서의 사진은 직접 넣어야 합니다.  

도서 이미지 예시는 '도서등록시 사용할 수 있는 사진' 에 넣었습니다.  
예를 들어서 book1.jpg 메인 이미지로 등록하고 book1-1.jpg 는 기타 이미지로 등록하는 식으로 도서 등록을 할 때 사용하면 됩니다.

