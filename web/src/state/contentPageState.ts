import { create } from 'zustand'
import ContentMetadata from '../types/ContentMetadata'

interface ContentPageState {
    contentPageMetadata?: ContentMetadata
    setContentPageMetadata: (metadata: ContentMetadata) => void
}

const useContentPageState = create<ContentPageState>((set) => ({
    contentPageMetadata: undefined,
    setContentPageMetadata: (metadata: ContentMetadata) =>
        set((state) => ({ contentPageMetadata: metadata })),
}))

export default useContentPageState
