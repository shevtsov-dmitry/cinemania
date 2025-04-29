import { create } from 'zustand'
import ContentMetadata from '../types/ContentMetadata'
import Base64WithId from '../types/Base64WithId'

interface ContentPageState {
    contentPageMetadata?: ContentMetadata
    posterBase64Object?: Base64WithId
    setContentPageMetadata: (metadata: ContentMetadata) => void
    setPosterBase64Object: (object: Base64WithId) => void
}

const useContentPageState = create<ContentPageState>((set) => ({
    contentPageMetadata: undefined,
    setContentPageMetadata: (metadata: ContentMetadata) =>
        set((state) => ({ contentPageMetadata: metadata })),
    setPosterBase64Object: (object: Base64WithId) =>
        set((state) => ({ posterBase64Object: object })),
}))

export default useContentPageState
