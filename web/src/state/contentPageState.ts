import { create } from 'zustand'
import ContentMetadata from '../types/ContentMetadata'

interface ContentPageState {
    isContentPageVisible: boolean
    contentPageMetadata?: ContentMetadata
    showContentPage: () => void
    hideContentPage: () => void
    toggleContentPage: () => void
    setContentPageMetadata: (metadata: ContentMetadata) => void
}

const useContentPageState = create<ContentPageState>((set) => ({
    isContentPageVisible: false,
    contentPageMetadata: undefined,
    showContentPage: () => set(() => ({ isContentPageVisible: true })),
    hideContentPage: () => set(() => ({ isContentPageVisible: false })),
    toggleContentPage: () =>
        set((state) => ({
            isContentPageVisible: state.isContentPageVisible,
        })),
    setContentPageMetadata: () =>
        set((state) => ({ contentPageMetadata: state.contentPageMetadata })),
}))

export default useContentPageState
