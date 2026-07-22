package com.mangban.blindboard.repository;

import com.mangban.blindboard.entity.BoardProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardProjectRepository extends JpaRepository<BoardProject, Long> {
}