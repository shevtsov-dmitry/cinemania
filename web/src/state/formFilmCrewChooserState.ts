import { create } from 'zustand'
import ContentCreator from '../types/ContentCreator'
import PersonCategory from '../types/PersonCategory'

interface FilmCrewChooserStore {
    isVisible: boolean
    selectedDirector: ContentCreator | undefined
    selectedActors: ContentCreator[]
    choosingType: PersonCategory
    setSelectedDirector: (director: ContentCreator) => void
    setSelectedActors: (actors: ContentCreator[]) => void
    setChoosingType: (type: PersonCategory) => void
    show: () => void
    hide: () => void
    toggle: () => void
}

const useFilmCrewChooserStore = create<FilmCrewChooserStore>((set) => ({
    isVisible: false,
    selectedDirector: undefined,
    selectedActors: [],
    choosingType: PersonCategory.ACTOR,
    setSelectedDirector: (director: ContentCreator) => {
        set({ selectedDirector: director })
    },
    setSelectedActors: (actors: ContentCreator[]) => {
        set({ selectedActors: actors })
    },
    setChoosingType: (type: PersonCategory) => {
        set({ choosingType: type })
    },
    show: () => set(() => ({ isVisible: true })),
    hide: () => set(() => ({ isVisible: false })),
    toggle: () =>
        set((state) => ({
            isVisible: state.isVisible,
        })),
}))

export default useFilmCrewChooserStore
