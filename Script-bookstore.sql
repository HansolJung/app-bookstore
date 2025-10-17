

/* 권한 테이블 */
create table user_role(
	role_id			varchar(255) 	not null		comment '아이디',
	role_name		varchar(255)	not null		comment '이름',
	use_yn			char(1)			default 'Y' 	comment 'Y,N',
	create_date		datetime		default now()	comment '생성일',
	update_date		datetime		default null	comment '수정일',
	
	primary key(role_id)
);

/* 권한 데이터 insert */
insert into user_role(role_id, role_name) values('ADMIN', '관리자'), ('USER', '사용자');



/* 회원 테이블 */
create table kb_users(
   user_id         	varchar(100)   	not null       	comment '아이디',
   passwd         	varchar(255)   	not null       	comment '패스워드',
   user_name      	varchar(50)     not null       	comment '이름',
   birth         	varchar(30)     not null       	comment '생년월일',
   gender         	varchar(30)     not null       	comment '성별',
   phone         	varchar(30)     not null       	comment '전화번호',
   email         	varchar(100)   	not null       	comment '이메일',
   addr         	varchar(255)   	not null       	comment '주소',
   addr_detail      varchar(255)   	default ''      comment '상세주소',
   deposit         	int            	default 0      	comment '보유금',
   user_role      	varchar(50)     default 'USER'  comment '권한',
   use_yn         	char(1)         default 'Y'    	comment '사용여부: Y,N',
   del_yn         	char(1)         default 'N'    	comment '삭제여부: Y,N',
   create_date      datetime      	default now()   comment '생성일',
   update_date      datetime      	default now()   comment '수정일',
   
   primary key(user_id),
   constraint user_role_fk foreign key(user_role) references user_role(role_id)
);

/* 어드민 유저 insert (비밀번호 1234) */
insert into kb_users (user_id, passwd, user_name, birth, gender, phone, email, addr, user_role)
values('admin', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', 
'관리자', '901011', '남자', '010-5555-4444', 'admin@gmail.com', '서울시 성북구', 'ADMIN');

/* 일반 사용자 insert (비밀번호 1234) */
insert into kb_users (user_id, passwd, user_name, birth, gender, phone, email, addr, user_role)
values('user', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', 
'사용자', '981227', '여자', '010-2222-3333', 'user@gmail.com', '서울시 구로구', 'USER');

/* 일반 사용자들 insert (비밀번호 1234) */
insert into kb_users (user_id, passwd, user_name, birth, gender, phone, email, addr, user_role)
values('user02', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user02@gmail.com', '서울시 구로구', 'USER'),
('user03', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user03@gmail.com', '서울시 구로구', 'USER'),
('user04', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user04@gmail.com', '서울시 구로구', 'USER'),
('user05', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user05@gmail.com', '서울시 구로구', 'USER'),
('user06', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user06@gmail.com', '서울시 구로구', 'USER'),
('user07', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user07@gmail.com', '서울시 구로구', 'USER'),
('user08', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user08@gmail.com', '서울시 구로구', 'USER'),
('user09', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user09@gmail.com', '서울시 구로구', 'USER'),
('user10', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user10@gmail.com', '서울시 구로구', 'USER'),
('user11', '$2a$10$8QleDVBEZYfrYrcrM9nRKuWIiqb15STUzGsY5jHRv0i0KGuU.9H5y', '사용자', '981227', '여자', '010-2222-3333', 'user10@gmail.com', '서울시 구로구', 'USER');





/* 카테고리 테이블 */
create table kb_categories(
   ca_id        int               	auto_increment      comment '카테고리 번호',
   ca_name      varchar(100)      	not null            comment '카테고리명',
   
   primary key(ca_id)
);

/* 도서 테이블 */
create table kb_books(
   book_id      	int             	auto_increment      comment '책 번호',
   ca_id         	int               	not null            comment '카테고리 번호',
   title         	varchar(255)      	not null            comment '책 제목',
   author      		varchar(100)   		not null         	comment '저자',
   publisher    	varchar(100)      	not null            comment '출판사',
   price      		int            		not null         	comment '가격(원)',
   description  	text            	not null            comment '책 설명',
   stock        	int             	default 0           comment '수량',
   release_date 	date         		not null         	comment '발행일',
   create_date  	datetime         	default now()       comment '등록일',
   update_date  	datetime         	default now()       comment '수정일',
   del_yn         	char(1)         	default 'N'    		comment '삭제여부: Y,N',
   
   primary key(book_id),
   constraint kb_fk foreign key(ca_id) references kb_categories(ca_id)
);

/* 도서 파일 테이블 */
create table kb_book_files(
   bf_id               	int             auto_increment      comment '번호',
   book_id            	int             not null            comment '책 번호',
   file_name            varchar(255)    not null            comment '파일이름',
   stored_name        	varchar(255)    not null            comment '저장파일이름',
   file_path            varchar(255)    not null            comment '파일경로',
   file_thumb_name      varchar(255)    not null            comment '썸네일 파일 이름',
   file_size         	bigint         	not null         	comment '파일 크기(단위: byte)',
   main_yn            	char(1)         default 'N'       	comment '메인 이미지 여부: Y,N',
   create_date        	datetime        default now()       comment '등록일',
   
   primary key(bf_id),
   constraint bf_fk2 foreign key(book_id) references kb_books(book_id) on delete cascade
);

/* 주문 테이블 */
create table kb_orders(
   order_id   		int             auto_increment      comment '주문 번호',
   user_id     		varchar(100)   	not null          	comment '주문한 유저아이디',
   order_date   	datetime      	default now()      	comment '주문 일시',
   total_price   	int            	not null         	comment '총액',
   status      		varchar(50)     not null         	comment '상태',
   
   primary key(order_id),
   constraint kb_fk2 foreign key(user_id) references kb_users(user_id)
);

/* 주문 상세 테이블 */
create table kb_order_item(
   item_id      int          	auto_increment      comment '주문 항목 번호',
   order_id   	int            	not   null         	comment   '주문 번호',
   book_id      int             not null          	comment '책 번호',
   quantity   	int            	default 0         	comment '수량',
   total_price  int            	default 0         	comment '총액',
   
   primary key(item_id),
   constraint kb_fk3 foreign key(book_id) references kb_books(book_id),
   constraint kb_fk4 foreign key(order_id) references kb_orders(order_id)
);

/* 장바구니 테이블 */
create table kb_basket(
   basket_id    int             auto_increment      comment '장바구니 번호',
   user_id     	varchar(100)   	not null          	comment '장바구니 주인아이디',
   book_id      int             not null          	comment '책 번호',
   quantity   	int            	default 0         	comment '수량',
   total_price  int            	default 0         	comment '총액',
   
   primary key(basket_id),
   constraint kb_fk22 foreign key(user_id) references kb_users(user_id),
   constraint kb_fk33 foreign key(book_id) references kb_books(book_id)
);



/* 카테고리 데이터 insert */
insert into kb_categories (ca_name)
values('총류'),('철학'),('종교'),('사회과학'),('순수과학'),('기술과학'),('예술'),('언어'),('문학'),('역사');



/* 기본 도서 데이터 insert (이미지 없는 상태) */
insert into kb_books(ca_id, title, author, publisher, price, description, stock, release_date)
values(1, '백과사전', '홍길동', '길동출판사', 40000, '이 책은 백과사전입니다.', 3, '2022-10-22'),
(2, '철학을 논하다', '김길수', '길동출판사', 34000, '이 책은 철학에 관련된 책입니다.', 2, '2022-10-22'),
(3, '종교전쟁', '아무개', '한빛출판사', 28000, '이 책은 종교에 관련된 책입니다.', 1, '2022-10-22'),
(4, '사회 과학책', '구갑룡', '한빛출판사', 30000, '이 책은 과학책입니다.', 5, '2022-10-22'),
(5, '순수 과학책', '구갑룡', '한빛출판사', 30000, '이 책은 과학책입니다.', 5, '2022-10-22'),
(6, '기술 과학책', '구갑룡', '한빛출판사', 30000, '이 책은 과학책입니다.', 5, '2022-10-22'),
(7, '예술이야', '싸이', '나무출판사', 20000, '이 책은 예술책입니다.', 1, '2022-10-22'),
(8, '영어를 배워보자', '루미', '나무출판사', 30000, '이 책은 영어책입니다.', 1, '2022-10-22'),
(8, '일본어를 배워보자', '나카무라', '나무출판사', 30000, '이 책은 일본어책입니다.', 2, '2022-10-22'),
(9, '문학소녀', '김경식', '아침출판사', 32000, '이 책은 문학책입니다.', 2, '2022-10-22'),
(9, '문학소년', '김경식', '아침출판사', 32000, '이 책은 문학책입니다.', 1, '2022-10-22'),
(10, '역사책', '이세종', '세종출판사', 38000, '이 책은 역사책입니다.', 1, '2022-10-22');








