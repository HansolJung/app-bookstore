package it.korea.app_bookstore.book.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.korea.app_bookstore.basket.repository.BasketRepository;
import it.korea.app_bookstore.book.dto.BookDTO;
import it.korea.app_bookstore.book.dto.BookFileDTO;
import it.korea.app_bookstore.book.dto.BookSearchDTO;
import it.korea.app_bookstore.book.dto.CategoryDTO;
import it.korea.app_bookstore.book.entity.BookEntity;
import it.korea.app_bookstore.book.entity.BookFileEntity;
import it.korea.app_bookstore.book.entity.CategoryEntity;
import it.korea.app_bookstore.book.repository.BookFileRepository;
import it.korea.app_bookstore.book.repository.BookRepository;
import it.korea.app_bookstore.book.repository.BookSearchSpecification;
import it.korea.app_bookstore.book.repository.CategoryRepository;
import it.korea.app_bookstore.common.dto.PageVO;
import it.korea.app_bookstore.common.utils.FileUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    @Value("${server.file.book.path}")
    private String filePath;

    private final BookRepository bookRepository;
    private final BookFileRepository fileRepository;
    private final CategoryRepository categoryRepository;
    private final BasketRepository basketRepository;
    private final FileUtils fileUtils;
    private List<String> extensions = Arrays.asList("jpg", "jpeg", "gif", "png", "webp", "bmp");

    /**
     * 도서 리스트 가져오기
     * @param pageable 페이징 정보
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBookList(Pageable pageable) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Page<BookEntity> entityList = bookRepository.findAllByDelYn("N", pageable);
        List<BookDTO.Response> dtoList = entityList.getContent().stream().map(BookDTO.Response::of).toList(); 

        PageVO pageVO = new PageVO();
        pageVO.setData(entityList.getNumber(), (int) entityList.getTotalElements());

        resultMap.put("total", entityList.getTotalElements());
        resultMap.put("page", entityList.getNumber());
        resultMap.put("content", dtoList);
        resultMap.put("pageHTML", pageVO.pageHTML());

        return resultMap;
    }

    /**
     * 도서 리스트 가져오기 (with Axios 호출 or 검색)
     * @param pageable 페이징 객체
     * @param searchDTO 검색 객체
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBookList(Pageable pageable, BookSearchDTO searchDTO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Page<BookEntity> pageList = null;

        BookSearchSpecification searchSpecification = new BookSearchSpecification(searchDTO);
        pageList = bookRepository.findAll(searchSpecification, pageable);

        List<BookDTO.Response> bookList = pageList.getContent().stream().map(BookDTO.Response::of).toList();

        PageVO pageVO = new PageVO();
        pageVO.setData(pageList.getNumber(), (int) pageList.getTotalElements());

        resultMap.put("total", pageList.getTotalElements());
        resultMap.put("content", bookList);
        resultMap.put("pageHTML", pageVO.pageHTML());
        resultMap.put("page", pageList.getNumber());
        
        return resultMap;
    }

    /**
     * 도서 상세정보 가져오기
     * @param bookId 도서 아이디
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public BookDTO.Detail getBook(int bookId) throws Exception {
        return BookDTO.Detail.of(bookRepository.getBook(bookId)
            .orElseThrow(()-> new RuntimeException("도서 없음")));
    }

    /**
     * 도서 등록
     * @param request
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> createBook(BookDTO.Request request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        MultipartFile mainImage = request.getMainImage();

        if (mainImage == null || mainImage.isEmpty()) {   // 메인 이미지가 없거나 빈 파일일 경우
            throw new RuntimeException("메인 이미지는 필수입니다.");
        }

        // 메인 이미지 파일 업로드
        Map<String, Object> mainImageMap = uploadImageFiles(request.getMainImage());

        BookEntity entity = new BookEntity();
        entity.setTitle(request.getTitle());
        entity.setAuthor(request.getAuthor());
        entity.setPublisher(request.getPublisher());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());
        entity.setStock(request.getStock());
        entity.setReleaseDate(request.getReleaseDate());
        entity.setDelYn("N");

        CategoryEntity categoryEntity = categoryRepository.findById(request.getCaId())
            .orElseThrow(()-> new RuntimeException("카테고리 없음"));

        entity.setCategory(categoryEntity);

        // 메인 이미지 파일이 있을 경우에만 파일 엔티티 생성
        if (mainImageMap != null) {  
            BookFileEntity fileEntity = new BookFileEntity();
            fileEntity.setFileName(mainImageMap.get("fileName").toString());
            fileEntity.setStoredName(mainImageMap.get("storedFileName").toString());
            fileEntity.setFilePath(mainImageMap.get("filePath").toString());
            fileEntity.setFileThumbName(mainImageMap.get("thumbName").toString());
            fileEntity.setFileSize(request.getMainImage().getSize());
            fileEntity.setMainYn("Y");
            fileEntity.setCreateDate(LocalDateTime.now());  // 파일 엔티티는 BaseEntity 를 상속하지 않기 때문에 등록 일자를 직접 만들어줘야함
            entity.addFiles(fileEntity, false);  // 도서 엔티티와 파일 엔티티 관계를 맺어줌
        }

        List<MultipartFile> imageList = request.getImage();

        if (imageList != null && imageList.size() > 0) {
            for (MultipartFile image : imageList) {
                if (image != null && !image.isEmpty()) {
                    // 기타 이미지 파일 업로드
                    Map<String, Object> imageMap = uploadImageFiles(image);

                    // 기타 이미지 파일이 있을 경우에만 파일 엔티티 생성
                    if (imageMap != null) {
                        BookFileEntity fileEntity = new BookFileEntity();
                        fileEntity.setFileName(imageMap.get("fileName").toString());
                        fileEntity.setStoredName(imageMap.get("storedFileName").toString());
                        fileEntity.setFilePath(imageMap.get("filePath").toString());
                        fileEntity.setFileThumbName(imageMap.get("thumbName").toString());
                        fileEntity.setFileSize(image.getSize());
                        fileEntity.setMainYn("N");
                        fileEntity.setCreateDate(LocalDateTime.now());  // 파일 엔티티는 BaseEntity 를 상속하지 않기 때문에 등록 일자를 직접 만들어줘야함
                        entity.addFiles(fileEntity, false);  // 도서 엔티티와 파일 엔티티 관계를 맺어줌
                    }
                }
            }
        }

        bookRepository.save(entity);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }


    /**
     * 도서 수정
     * @param request
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> updateBook(BookDTO.Request request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 1. 수정하기 위해 기존 정보를 불러온다 
        BookEntity entity = bookRepository.getBook(request.getBookId())   
            .orElseThrow(()-> new RuntimeException("도서 없음"));

        entity.setTitle(request.getTitle());
        entity.setAuthor(request.getAuthor());
        entity.setPublisher(request.getPublisher());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());
        entity.setStock(request.getStock());
        entity.setReleaseDate(request.getReleaseDate());

        CategoryEntity categoryEntity = categoryRepository.findById(request.getCaId())
            .orElseThrow(()-> new RuntimeException("카테고리 없음"));

        entity.setCategory(categoryEntity);

        BookDTO.Detail detail = BookDTO.Detail.of(entity);
        
        // 2. 업로드 할 메인 이미지 파일이 있으면 업로드
        if (request.getMainImage() != null && !request.getMainImage().isEmpty()) {
            // 2-1. 파일 업로드
            Map<String, Object> fileMap = uploadImageFiles(request.getMainImage());   // 파일 업로드 과정 공통화해서 분리

            Set<BookFileEntity> fileEntitySet = entity.getFileList().stream().filter((file) -> 
                file.getMainYn().equals("N")).collect(Collectors.toSet());  // 기존 파일 리스트에서 메인 이미지 파일만 삭제          
            entity.getFileList().clear();  // 기존 파일 리스트 삭제
            entity.getFileList().addAll(fileEntitySet);
            
            // 2-2. 파일 등록
            // 파일이 있을 경우에만 파일 엔티티 생성
            if (fileMap != null) {  
                BookFileEntity fileEntity = new BookFileEntity();
                fileEntity.setFileName(fileMap.get("fileName").toString());
                fileEntity.setStoredName(fileMap.get("storedFileName").toString());
                fileEntity.setFilePath(fileMap.get("filePath").toString());
                fileEntity.setFileThumbName(fileMap.get("thumbName").toString());
                fileEntity.setFileSize(request.getMainImage().getSize());
                fileEntity.setMainYn("Y");
                fileEntity.setCreateDate(LocalDateTime.now());

                // 파일만 수정했을 경우에도 도서 entity 의 updateDate 를 갱신하기 위해서 isUpdate 값을 true 로 줌.
                entity.addFiles(fileEntity, true);  // 도서 엔티티와 파일 엔티티 관계를 맺어줌
            }
        }

        List<MultipartFile> imageList = request.getImage();
        boolean isFirst = true;   // 기타 이미지 파일 업로드를 처음 시도하는지 여부

        // 2. 업로드 할 기타 이미지 파일이 있으면 업로드
        if (imageList != null && imageList.size() > 0) {
            for (MultipartFile image : imageList) {
                if (image != null && !image.isEmpty()) {
                    // 2-1. 파일 업로드
                    Map<String, Object> fileMap = uploadImageFiles(image);   // 파일 업로드 과정 공통화해서 분리

                    if (isFirst) {   // 기타 이미지 파일 업로드를 처음 시도하는 경우는...
                        Set<BookFileEntity> fileEntitySet = entity.getFileList().stream().filter((file) -> 
                            file.getMainYn().equals("Y")).collect(Collectors.toSet());  // 기존 파일 리스트에서 기타 이미지 파일만 삭제

                        entity.getFileList().clear();
                        entity.getFileList().addAll(fileEntitySet);
                        isFirst = false;
                    }

                    // 2-2. 파일 등록
                    // 파일이 있을 경우에만 파일 엔티티 생성
                    if (fileMap != null) {  
                        BookFileEntity fileEntity = new BookFileEntity();
                        fileEntity.setFileName(fileMap.get("fileName").toString());
                        fileEntity.setStoredName(fileMap.get("storedFileName").toString());
                        fileEntity.setFilePath(fileMap.get("filePath").toString());
                        fileEntity.setFileThumbName(fileMap.get("thumbName").toString());
                        fileEntity.setFileSize(image.getSize());
                        fileEntity.setMainYn("N");
                        fileEntity.setCreateDate(LocalDateTime.now());

                        // 파일만 수정했을 경우에도 도서 entity 의 updateDate 를 갱신하기 위해서 isUpdate 값을 true 로 줌.
                        entity.addFiles(fileEntity, true);  // 도서 엔티티와 파일 엔티티 관계를 맺어줌
                    }
                }
            }
        }

        bookRepository.save(entity);

        if (request.getMainImage() != null && !request.getMainImage().isEmpty()) {
            // 2-3. 기존 파일 삭제 (작업 도중 DB에 문제가 생길 수도 있기 때문에 물리적 파일 삭제는 제일 마지막에 진행)
            // 도서 상세 정보 DTO 가 가지고 있는 파일 DTO 리스트 순회
            if (detail.getFileList() != null && detail.getFileList().size() > 0) {
                for (BookFileDTO fileDTO : detail.getFileList()) {
                    if (fileDTO.getMainYn().equals("Y")) {
                        deleteImageFiles(fileDTO);

                        break;   // 메인 이미지 파일은 하나뿐이기 때문에 지웠다면 반복문을 더 순회할 필요가 없기 때문에 바로 break;
                    }
                }
            }
        }

        if (imageList != null && imageList.size() > 0) {
            for (MultipartFile image : imageList) {
                if (image != null && !image.isEmpty()) {
                    // 2-3. 기존 파일 삭제 (작업 도중 DB에 문제가 생길 수도 있기 때문에 물리적 파일 삭제는 제일 마지막에 진행)
                    // 도서 상세 정보 DTO 가 가지고 있는 파일 DTO 리스트 순회
                    if (detail.getFileList() != null && detail.getFileList().size() > 0) {
                        for (BookFileDTO fileDTO : detail.getFileList()) {
                            if (fileDTO.getMainYn().equals("N")) {
                                deleteImageFiles(fileDTO);
                            }
                        }
                        break;
                    }
                }
            }
        }

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }

    /**
     * 도서 삭제(실제로 삭제되지는 않음. 삭제 여부만 변경)
     * @param bookId 도서 아이디
     * @param request
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> deleteBook(int bookId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        BookEntity entity = bookRepository.getBook(bookId)   // fetch join 을 사용한 getBook 메서드 호출
            .orElseThrow(()-> new RuntimeException("도서 없음"));
        entity.setDelYn("Y");  // 삭제 여부 Y로 변경    

        bookRepository.save(entity);

        // 모든 사람들의 장바구니에서 해당 도서들 전부 삭제하기
        basketRepository.deleteAllByBook_bookId(entity.getBookId());

        // 도서의 삭제 여부를 Y 로 변경만하고 도서의 이미지들도 삭제하지 않음
        // 왜냐하면 주문 내역 등에서 해당 도서의 상세 정보를 보여줘야 하기 때문

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }


    /**
     * 파일 삭제
     * @param bfId 파일 아이디
     * @return
     * @throws Exception
     */
    @Transactional
    public Map<String, Object> deleteFile(int bfId) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        
        // 파일 정보 
        BookFileEntity fileEntity = fileRepository.findById(bfId)
                .orElseThrow(()-> new RuntimeException("파일정보 DB에 없음"));

        BookFileDTO fileDTO = BookFileDTO.of(fileEntity);

        String fullPath = fileDTO.getFilePath() + fileDTO.getStoredName();
        
        // 파일 DB에서 삭제
        fileRepository.delete(fileEntity);

        // 도서 엔티티 updateDate 갱신하기
        BookEntity bookEntity = fileEntity.getBook();
        bookEntity.preUpdate();

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new RuntimeException("파일이 경로에 없음");
        }

        // 실제 파일 삭제
        fileUtils.deleteFile(fullPath);

        resultMap.put("resultCode", 200);
        resultMap.put("resultMsg", "OK");

        return resultMap;
    }


    /**
     * 파일 업로드 과정 공통화해서 분리
     * @param file request 에서 넘어온 파일
     * @return
     * @throws Exception
     */
    private Map<String, Object> uploadImageFiles(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();

        if (StringUtils.isBlank(fileName)) {
            return null;
        }

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        if (!extensions.contains(ext.toLowerCase())) {
            throw new RuntimeException("파일 형식이 맞지 않습니다. 이미지 파일만 가능합니다.");
        }

        Map<String, Object> fileMap = fileUtils.uploadFiles(file, filePath);

        if (fileMap == null) {
            throw new RuntimeException("파일 업로드 실패");
        }

        String thumbFilePath = filePath + "thumb" + File.separator;
        String storedFilePath = filePath + fileMap.get("storedFileName").toString();

        File thumbFile = new File(storedFilePath);

        if (!thumbFile.exists()) {
            throw new RuntimeException("업로드 파일이 존재하지 않음");
        }

        String thumbName = fileUtils.thumbNailFile(225, 270, thumbFile, thumbFilePath);
        fileMap.put("thumbName", thumbName);

        return fileMap;
    }

    /**
     * 파일 삭제과정 공통화해서 분리
     * @param detail 도서 파일 정보 DTO
     * @throws Exception
     */
    private void deleteImageFiles(BookFileDTO dto) throws Exception {
        // 파일 정보
        String fullPath = dto.getFilePath() + dto.getStoredName();
        String thumbFilePath = filePath + "thumb" + File.separator + dto.getFileThumbName();

        File file = new File(fullPath);

        if (!file.exists()) {
            throw new RuntimeException("파일이 경로에 없음");
        }

        // 실제 파일 삭제
        fileUtils.deleteFile(fullPath);
        fileUtils.deleteFile(thumbFilePath);  // 썸네일 파일까지 삭제
    }

    /**
     * 카테고리 정보 가져오기
     * @param pageable 페이징 정보
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoryList() throws Exception {
        return categoryRepository.findAll().stream().map(CategoryDTO::of).toList();
    }
}
