package com.example.demo.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Article;

@Repository
public class ArticleRepositoryJdbc implements ArticleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ArticleRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Article> articleRowMapper = (rs, rowNum) -> new Article(
            rs.getLong("id"),
            rs.getLong("author_id"),
            rs.getLong("board_id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getTimestamp("created_date").toLocalDateTime(),
            rs.getTimestamp("modified_date").toLocalDateTime()
    );

    @Override
    public List<Article> findAll() {
        return jdbcTemplate.query("""
                SELECT id,  board_id,  author_id,  title,  content,  created_date,  modified_date
                FROM article
                """, articleRowMapper);
    }

    @Override
    public List<Article> findAllByBoardId(Long boardId) {
        return jdbcTemplate.query("""
                SELECT id,  board_id,  author_id,  title,  content,  created_date,  modified_date
                FROM article
                WHERE board_id = ?
                """, articleRowMapper, boardId);
    }

    @Override
    public List<Article> findAllByMemberId(Long memberId) {
        return jdbcTemplate.query("""
                SELECT id,  board_id,  author_id,  title,  content,  created_date,  modified_date
                FROM article
                WHERE author_id = ?
                """, articleRowMapper, memberId);
    }

    @Override
    public Article findById(Long id) {
        return jdbcTemplate.queryForObject("""
                SELECT id,  board_id,  author_id,  title,  content,  created_date,  modified_date
                FROM article
                WHERE id = ?
                """, articleRowMapper, id);
    }

    @Override
    public Article insert(Article article) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                            INSERT INTO article (author_id, board_id, title, content)
                            VALUES (?, ?, ?, ?)
                            """,
                    new String[]{"id"});
            ps.setLong(1, article.getAuthor_id());
            ps.setLong(2, article.getBoard_id());
            ps.setString(3, article.getTitle());
            ps.setString(4, article.getContent());
            return ps;
        }, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Article update(Article article) {
        jdbcTemplate.update("""
                        UPDATE article
                        SET board_id = ?, title = ?, content = ?
                        WHERE id = ?
                        """,
                article.getBoard_id(),
                article.getTitle(),
                article.getContent(),
                article.getId()
        );
        return findById(article.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("""
                DELETE FROM article
                WHERE id = ?
                """, id);
    }
}
