package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> index() {
        return articleRepository.findAll();
    }

    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(ArticleForm dto) {
        Article article = dto.toEntity();
        if (article.getId() != null) {
            return null;
        }
        return  articleRepository.save(article);
    }

    public Article update(Long id, ArticleForm dto) {
        // 1. DTO -> 엔티티 변환하기
        Article article = dto.toEntity();
        log.info("id: {}, article: {}", id, article.toString());

        // 2. 타깃 조회하기
        Article target = articleRepository.findById(id).orElse(null);

        // 3. 잘못된 요청 처리하기
        if (target == null || id != article.getId()) {
            // 400, 잘못된 요청 응답!
            log.info("잘못된 요청! id: {}, article: {}", id, article.toString());
            return null;
        }

        // 4. 업데이트 및 정상 응답(200)하기
        target.patch(article);
        Article updated = articleRepository.save(target);
        return updated;

    }

    public Article delete(Long id) {
        // 1. 대상찾기
        Article target = articleRepository.findById(id).orElse(null);
        // 2. 잘못된 요청 처리하기
        if (target == null){
            return null;
        }
        // 3. 대상 삭제하기
        articleRepository.delete(target);
        return target;
    }

    @Transactional
    public List<Article> createArticles(List<ArticleForm> dtos) {
        // 1. dto묶음(리스트)을 엔티티묶음(리스트)으로 변환하기
        List<Article> articleList = dtos.stream() // dtos를 스트림화한다.
                .map(dto -> dto.toEntity())      // map으로 dto가 하나하나 올때마다 dto.toEntity()를 수행해 매핑한다.
                .collect(Collectors.toList());   // 매핑한 것을 리스트로 묶는다.
        // 2. 엔티티 묶음(리스트)을 DB에 저장
        articleList.stream()  // articleList를 스트림화
                .forEach(article -> articleRepository.save(article));  //article이 하나씩 올때마다 articleRepository를 통해 DB에 저장
        // 3. 강제로 에러를 발생시키키
        articleRepository.findById(-1L)
                .orElseThrow(()-> new IllegalArgumentException("결제 실패"));
        // 4. 결과값 반환하기
        return  articleList;
    }
}
