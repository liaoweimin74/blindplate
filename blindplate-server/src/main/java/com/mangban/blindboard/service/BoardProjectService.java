package com.mangban.blindboard.service;

import com.mangban.blindboard.entity.BoardProject;
import com.mangban.blindboard.repository.BoardProjectRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardProjectService {

    private final BoardProjectRepository boardProjectRepository;

    public List<BoardProject> findAll() {
        return boardProjectRepository.findAll();
    }

    public BoardProject findById(Long id) {
        return boardProjectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "项目不存在"));
    }

    public BoardProject create(BoardProject project) {
        return boardProjectRepository.save(project);
    }

    public BoardProject update(Long id, BoardProject project) {
        BoardProject existing = findById(id);
        existing.setName(project.getName());
        existing.setSvgJson(project.getSvgJson());
        existing.setThumbnail(project.getThumbnail());
        return boardProjectRepository.save(existing);
    }

    public void delete(Long id) {
        boardProjectRepository.deleteById(id);
    }
}