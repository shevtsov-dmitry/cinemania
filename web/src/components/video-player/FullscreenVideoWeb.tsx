import useContentPageState from '@/src/state/contentPageState'
import React from 'react'
import WebVideoPlayer from './WebVideoPlayer'

interface FullscreenVideoWebProps {
    url: string
}

const FullscreenVideoWeb = ({ url }: FullscreenVideoWebProps) => {
    const { contentPageMetadata } = useContentPageState()

    if (!url) {
        return (
            <div className="flex h-screen items-center justify-center bg-black text-white">
                <div>
                    <p className="mb-4 text-2xl">Видео не найдено</p>
                    <button
                        className="rounded bg-cyan-700 px-4 py-2 text-white hover:bg-cyan-500"
                        onClick={() => window.history.back()}
                    >
                        Вернуться
                    </button>
                </div>
            </div>
        )
    }

    return <WebVideoPlayer url={url} variant="main" />
}

export default FullscreenVideoWeb
