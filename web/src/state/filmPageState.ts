import { create } from 'zustand'
import ContentMetadata from '../types/ContentMetadata'

interface FilmPageState {
    isVisible: boolean
    metadata?: ContentMetadata
    show: () => void
    hide: () => void
    toggle: () => void
}

const useFilmPageState = create<FilmPageState>((set) => ({
    isVisible: false,
    metadata: undefined,
    show: () => set(() => ({ isVisible: true })),
    hide: () => set(() => ({ isVisible: false })),
    toggle: () =>
        set((state) => ({
            isVisible: state.isVisible,
        })),
    setMetadata: () => set((state) => ({ metadata: state.metadata })),
}))
