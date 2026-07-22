import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getBoardProjects,
  getBoardProject,
  createBoardProject,
  updateBoardProject,
  deleteBoardProject
} from '@/api/blindboard'
import type { BoardProject } from '@/api/blindboard'

export const useBoardStore = defineStore('board', () => {
  const projects = ref<BoardProject[]>([])
  const currentProject = ref<BoardProject | null>(null)
  const loading = ref(false)

  async function fetchProjects() {
    loading.value = true
    try {
      const res: any = await getBoardProjects()
      projects.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function fetchProject(id: number) {
    loading.value = true
    try {
      const res: any = await getBoardProject(id)
      currentProject.value = res.data
      return res.data
    } finally {
      loading.value = false
    }
  }

  async function saveProject(name: string, svgJson: object, thumbnail?: string) {
    if (currentProject.value) {
      const res: any = await updateBoardProject(currentProject.value.id, {
        name,
        svgJson: JSON.stringify(svgJson),
        thumbnail
      })
      currentProject.value = res.data
      return res.data
    } else {
      const res: any = await createBoardProject({
        name,
        svgJson: JSON.stringify(svgJson),
        thumbnail
      })
      currentProject.value = res.data
      return res.data
    }
  }

  async function removeProject(id: number) {
    await deleteBoardProject(id)
    projects.value = projects.value.filter(p => p.id !== id)
  }

  function setCurrentProject(project: BoardProject | null) {
    currentProject.value = project
  }

  return {
    projects,
    currentProject,
    loading,
    fetchProjects,
    fetchProject,
    saveProject,
    removeProject,
    setCurrentProject
  }
})